package com.sun.jna;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.LibraryResolver;
import com.github.unidbg.Module;
import com.github.unidbg.Symbol;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.hook.HookContext;
import com.github.unidbg.hook.ReplaceCallback;
import com.github.unidbg.hook.hookzz.HookEntryInfo;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.hook.hookzz.IHookZz;
import com.github.unidbg.hook.hookzz.InstrumentCallback;
import com.github.unidbg.hook.whale.IWhale;
import com.github.unidbg.hook.whale.Whale;
import com.github.unidbg.hook.xhook.IxHook;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.XHookImpl;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.jni.ProxyClassFactory;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import unicorn.Arm64Const;

import java.io.File;
import java.io.IOException;

public class JniDispatch32 {

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static AndroidEmulator createARMEmulator() {
        return AndroidEmulatorBuilder.for32Bit()
                .setProcessName("com.sun.jna")
                .addBackendFactory(new DynarmicFactory(true))
                .build();
    }

    private final AndroidEmulator emulator;
    private final Module module;

    private final DvmClass cNative;

    private JniDispatch32() {
        //        初始化arm模拟器
        emulator = createARMEmulator();
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(createLibraryResolver());
        // 创建Android虚拟机
        VM vm = emulator.createDalvikVM();
        vm.setDvmClassFactory(new ProxyClassFactory());
        vm.setVerbose(true);// 设置是否打印Jni调用细节
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/resources/example_binaries/armeabi-v7a/libjnidispatch.so"), false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();

        //  需要模拟的类
        cNative = vm.resolveClass("com/sun/jna/Native");
        // 查找符号__system_property_get?奇怪，该方法在导入函数、导出函数和函数列表里都没有找到，该上哪找呢？
        Symbol __system_property_get = module.findSymbolByName("__system_property_get", true);
        MemoryBlock block = null;
        try {
            block = memory.malloc(0x10, false);
            //手动调用jni方法__system_property_get,获取sdk的版本
            Number ret = __system_property_get.call(emulator, "ro.build.version.sdk", block.getPointer());
            System.out.println("sdk=" + new String(block.getPointer().getByteArray(0, ret.intValue())) + ", libc=" + memory.findModule("libc.so"));
        } finally {
            if (block != null) {
                block.free();
            }
        }
    }

    private void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    public static void main(String[] args) throws Exception {
        JniDispatch32 test = new JniDispatch32();

        test.test();

        test.destroy();
    }

    private void test() {
        /**
         * 计划hook  malloc，使用XHookImpl进行hook
         * malloc是导入函数
         */
        IxHook xHook = XHookImpl.getInstance(emulator);
        xHook.register("libjnidispatch.so", "malloc", new ReplaceCallback() {

            //originFunction 是原函数的地址。
            @Override
            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
                //
                // 可以 emulator.getBackend().reg_read() 进行值的修改 如下
                // emulator.getBackend().reg_write(Arm64Const.UC_ARM64_REG_X3,2);
                int size = context.getIntArg(0);
                context.push(size);
                System.out.println("malloc=" + size);
                // 什么场景下用HookStatus.RET 和HookStatus.LR，需要再了解下
                return HookStatus.RET(emulator, originFunction);
            }
            @Override
            public void postCall(Emulator<?> emulator, HookContext context) {
                int size = context.pop();
                System.out.println("malloc=" + size + ", ret=" + context.getPointerArg(0));
            }
        }, true);
        xHook.refresh();

        IWhale whale = Whale.getInstance(emulator);
        //free是导入函数
        Symbol free = emulator.getMemory().findModule("libc.so").findSymbolByName("free");
        whale.inlineHookFunction(free, new ReplaceCallback() {
            @Override
            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
                System.out.println("WInlineHookFunction free=" + emulator.getContext().getPointerArg(0));
                // 什么场景下用HookStatus.RET 和HookStatus.LR，需要再了解下
                return HookStatus.RET(emulator, originFunction);
            }
        });

        long start = System.currentTimeMillis();
        final int size = 0x20;
        //  调用com/sun/jna/Native类的malloc方法，注意这里返回的是Number类
        Number ret = cNative.callStaticJniMethodLong(emulator, "malloc(J)J", size);
        //       创建一个指针，问题这里为与0xffffffffL (二进制全是1) 最大值进行与操作的原因是为了避免隐式类型转换成long时的错误,所以显示调用，增加准确性。
        Pointer pointer = UnidbgPointer.pointer(emulator, ret.intValue() & 0xffffffffL);
        assert pointer != null;

        // 设置指针的值为当前的类名
        pointer.setString(0, getClass().getName());
        //  侦察发送的数据,这个方法主要的作用是打印日期、内存字节对应的是16进制和内存字节对应的字符信息。有兴趣的可以进行跟读下代码。
        Inspector.inspect(pointer.getByteArray(0, size), "malloc ret=" + ret + ", offset=" + (System.currentTimeMillis() - start) + "ms");

        IHookZz hookZz = HookZz.getInstance(emulator);

        //  这里准备hook导出函数newJavaString
        Symbol newJavaString = module.findSymbolByName("newJavaString");
        hookZz.instrument(newJavaString, new InstrumentCallback<RegisterContext>() {
            @Override
            public void dbiCall(Emulator<?> emulator, RegisterContext ctx, HookEntryInfo info) {
                // 通过ida分析，该函数有三个参数，第一个参数是int，第二个参数是wchar_t *s, 第二个参数是char *a3，
                //__int64 __fastcall newJavaString(__int64 a1, wchar_t *s, char *a3)
                Pointer value = ctx.getPointerArg(1);
                Pointer encoding = ctx.getPointerArg(2);
                System.out.println("newJavaString value=" + value.getString(0) + ", encoding=" + encoding.getString(0));
            }
        });

        //  调用com/sun/jna/Native类的 getNativeVersion 方法，注意这里返回的是DvmObject类,通过ida看到这个方法返回的是int
        //  注意通过IDA静态分析，发现getNativeVersion内部调用了newJavaString 方法。所以前面对方法newJavaString进行了hook
        DvmObject<?> version = cNative.callStaticJniMethodObject(emulator, "getNativeVersion()Ljava/lang/String;");
        System.out.println("getNativeVersion version=" + version.getValue() + ", offset=" + (System.currentTimeMillis() - start) + "ms");

        //调用com/sun/jna/Native类的getNativeVersion方法，注意这里返回的是DvmObject类,通过ida看到这个方法返回的是int
        DvmObject<?> checksum = cNative.callStaticJniMethodObject(emulator, "getAPIChecksum()Ljava/lang/String;");
        System.out.println("getAPIChecksum checksum=" + checksum.getValue() + ", offset=" + (System.currentTimeMillis() - start) + "ms");

        ret = cNative.callStaticJniMethodInt(emulator, "sizeof(I)I", 0);
        System.out.println("sizeof POINTER_SIZE=" + ret.intValue() + ", offset=" + (System.currentTimeMillis() - start) + "ms");
    }

}
