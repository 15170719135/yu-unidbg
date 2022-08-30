package com.example.hookinunidbg;
 
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.Symbol;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.hook.HookContext;
import com.github.unidbg.hook.ReplaceCallback;
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
import com.github.unidbg.memory.Memory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.sun.jna.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
import java.io.File;

/*
    主要查看多行hook
     了解获取返回值的原理。根据ARM ATPCS调用约定， 函数的返回值总是通过R0传递回来。
     使用whale进行辅助分析
 */
public class HookInUnidbgByWhale {
    private static final Log logger = LogFactory.getLog(HookInUnidbgByWhale.class);
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
 
    HookInUnidbgByWhale() {
 
        // 创建模拟器实例
        emulator = AndroidEmulatorBuilder.for32Bit().build();
 
        // 模拟器的内存操作接口
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));
        // 创建Android虚拟机
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/example/hookinunidbg/hookinunidbg.apk"));
 
//        emulator.attach().addBreakPoint(0x40000000+0xa80);
        // 加载so到虚拟内存
        DalvikModule dm = vm.loadLibrary("hookinunidbg", true);
        // 加载好的 libhookinunidbg.so对应为一个模块
        module = dm.getModule();
 
        // 执行JNIOnLoad（如果有的话）
        dm.callJNI_OnLoad(emulator);
    }
 
    /**
     * 参考文档
     * https://github.com/asLody/whale/blob/master/README.zh-CN.md
     */
    public void hookByIWhale(){
        IWhale whale = Whale.getInstance(emulator);
        Symbol free = emulator.getMemory().findModule("libhookinunidbg.so").findSymbolByName("base64_encode");
        whale.inlineHookFunction(free, new ReplaceCallback() {
            UnidbgPointer outPointer = null;
            @Override
            public HookStatus onCall(Emulator<?> emulator, long originFunction) {
 
                RegisterContext context = emulator.getContext();
                Pointer input = context.getPointerArg(0);
                int length = context.getIntArg(1);
                outPointer = context.getPointerArg(2);
                System.out.println("hookByIWhale base64_encode_address.onCall 入参arg0:"+input.getString(0));
                System.out.println("hookByIWhale base64_encode_address.onCall 入参arg1:"+length);
                System.out.println("hookByIWhale base64_encode_address.onCall originFunction=0x" + Long.toHexString(originFunction));
 
 
                return HookStatus.RET(emulator, originFunction);
            }
            public void postCall(Emulator<?> emulator, HookContext context) {
 
                System.out.println("hookByIWhale base64.postCall 返回结果： R0=" + context.getIntArg(0));
                String repString = outPointer.getString(0);
                System.out.println("hookByIWhale base64 加密后的结果:"+repString);
            }
        },true);
 
    }
 
 
//    调用native方法
    public void call(){
        DvmClass dvmClass = vm.resolveClass("com/example/hookinunidbg/MainActivity");
        String methodSign = "call()V";
        DvmObject<?> dvmObject = dvmClass.newObject(null);
 
        dvmObject.callJniMethodObject(emulator, methodSign);
    }
    public static void main(String[] args) {
        HookInUnidbgByWhale mydemo = new HookInUnidbgByWhale();
         mydemo.hookByIWhale();
        mydemo.call();
    }
 
 
}