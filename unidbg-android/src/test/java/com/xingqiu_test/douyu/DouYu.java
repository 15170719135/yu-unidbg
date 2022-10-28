package com.xingqiu_test.douyu;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.Backend;
import com.github.unidbg.arm.backend.CodeHook;
import com.github.unidbg.arm.backend.UnHook;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DouYu extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    public static void main(String[] args) {
        DouYu douyu = new DouYu();
        douyu.traceLength();
        douyu.hookStrCat(); //0xbffff69b
        System.out.println("result:"+douyu.getMakeUrl());
    }

    public DouYu() {
        emulator = AndroidEmulatorBuilder.for32Bit().build();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("D:\\hecai_pan\\unidbg\\一个小函数分析--斗鱼\\files\\douyu.apk"));
        vm.setVerbose(true);
        vm.setJni(this);
        emulator.traceWrite(0x402d20d5,0x402d20d5+0x20); // 大概有20条日志
        DalvikModule dm = vm.loadLibrary(new File("D:\\hecai_pan\\unidbg\\一个小函数分析--斗鱼\\files\\libmakeurl2.5.0.so"), true);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        // 1. 分析
        //JNIEnv->NewStringUTF("aid=android1&client_sys=android&time=1638452332&auth=5e60a3002273d85bee3b9ad0893e9c37") was called from RX@0x4000336f[libmakeurl.so]0x336f
        // RX@0x4000336f  --> 0x336f
        //.text:0000336C                 BLX             R2
        //.text:0000336E                 STR             R0, [SP,#0xE8+var_98]
        // ida 看汇编, 日志提示调用处在0x336f，这个地址实际上是LR（返回地址），所以NewStringUTF函数调用是 0x336f 的上一条 0x336C

        //2.在 0x336C 下断点
//        emulator.attach().addBreakPoint(module.base + 0x336c);

        // mr1:  r1=RW@0x402d20a0  --> 数据从 0x402d20a0 就得到了加密值

        //3. 对 0x402d20a0 监听
//        emulator.traceWrite(0x402d20d5,0x402d20d5+0x20); // 大概有20条日志

        // 4. 从下往上寻找对内存最晚的操作 , 然后 那个地址是拼接字符串, 继续hook: hookStrCat

        //5. 找到加密串对应的地址 0xbffff69b
        //6. 对来源 0xbffff69bL 做traceWrite，千万记得加后缀L
    }

    public String getMakeUrl(){
        // args list
        List<Object> list = new ArrayList<>(10);
        // arg1 env
        list.add(vm.getJNIEnv());
        // arg2 jobject/jclazz 一般用不到，直接填0
        list.add(0);

        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);
        list.add(vm.addLocalObject(context));

        list.add(vm.addLocalObject(new StringObject(vm, "")));

        StringObject input3_1 = new StringObject(vm, "aid");
        StringObject input3_2 = new StringObject(vm, "client_sys");
        StringObject input3_3 = new StringObject(vm, "time");

        vm.addLocalObject(input3_1);
        vm.addLocalObject(input3_2);
        vm.addLocalObject(input3_3);

        list.add(vm.addLocalObject(new ArrayObject(input3_1, input3_2, input3_3)));


        StringObject input4_1 = new StringObject(vm, "android1");
        StringObject input4_2 = new StringObject(vm, "android");
        StringObject input4_3 = new StringObject(vm, "1638452332");

        vm.addLocalObject(input4_1);
        vm.addLocalObject(input4_2);
        vm.addLocalObject(input4_3);

        list.add(vm.addLocalObject(new ArrayObject(input4_1, input4_2, input4_3)));

        StringObject input5_1 = new StringObject(vm, "");
        StringObject input5_2 = new StringObject(vm, "");
        StringObject input5_3 = new StringObject(vm, "");
        StringObject input5_4 = new StringObject(vm, "");
        StringObject input5_5 = new StringObject(vm, "");
        StringObject input5_6 = new StringObject(vm, "");
        StringObject input5_7 = new StringObject(vm, "");
        StringObject input5_8 = new StringObject(vm, "");
        StringObject input5_9 = new StringObject(vm, "");
        StringObject input5_10 = new StringObject(vm, "");
        StringObject input5_11 = new StringObject(vm, "");
        StringObject input5_12 = new StringObject(vm, "");
        StringObject input5_13 = new StringObject(vm, "");

        vm.addLocalObject(input5_1);
        vm.addLocalObject(input5_2);
        vm.addLocalObject(input5_3);
        vm.addLocalObject(input5_4);
        vm.addLocalObject(input5_5);
        vm.addLocalObject(input5_6);
        vm.addLocalObject(input5_7);
        vm.addLocalObject(input5_8);
        vm.addLocalObject(input5_9);
        vm.addLocalObject(input5_10);
        vm.addLocalObject(input5_11);
        vm.addLocalObject(input5_12);
        vm.addLocalObject(input5_13);

        list.add(vm.addLocalObject(new ArrayObject(input5_1, input5_2, input5_3,input5_4, input5_5, input5_6,input5_7, input5_8, input5_9,input5_10, input5_11, input5_12,input5_13)));

        StringObject input6_1 = new StringObject(vm, "");
        StringObject input6_2 = new StringObject(vm, "");
        StringObject input6_3 = new StringObject(vm, "");
        StringObject input6_4 = new StringObject(vm, "");
        StringObject input6_5 = new StringObject(vm, "");
        StringObject input6_6 = new StringObject(vm, "");
        StringObject input6_7 = new StringObject(vm, "");
        StringObject input6_8 = new StringObject(vm, "");
        StringObject input6_9 = new StringObject(vm, "");
        StringObject input6_10 = new StringObject(vm, "");

        vm.addLocalObject(input6_1);
        vm.addLocalObject(input6_2);
        vm.addLocalObject(input6_3);
        vm.addLocalObject(input6_4);
        vm.addLocalObject(input6_5);
        vm.addLocalObject(input6_6);
        vm.addLocalObject(input6_7);
        vm.addLocalObject(input6_8);
        vm.addLocalObject(input6_9);
        vm.addLocalObject(input6_10);
        list.add(vm.addLocalObject(new ArrayObject(input6_1, input6_2, input6_3,input6_4, input6_5, input6_6,input6_7, input6_8, input6_9,input6_10)));
        list.add(0);
        list.add(1);
        // 参数准备完成
        // call function
        Number number = module.callFunction(emulator, 0x2f91, list.toArray());
        return vm.getObject(number.intValue()).getValue().toString();
    }

    public void hookStrCat(){
        emulator.attach().addBreakPoint(module.findSymbolByName("strcat", true).getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                UnidbgPointer r1 = emulator.getContext().getPointerArg(1);
                System.out.println("strcat:"+ r1);
                System.out.println(r1.getString(0));
                return true;
            }
        });
    }

    public void hookMemcpy(){
        emulator.attach().addBreakPoint(module.findSymbolByName("memcpy", true).getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                UnidbgPointer r1 = emulator.getContext().getPointerArg(1);
                int length = emulator.getContext().getIntArg(2);
                System.out.println("memcpy");
                Inspector.inspect(r1.getByteArray(0, length), r1.toString());
                return true;
            }
        });
    }

    // 统计汇编指令行数
    int count = 0;
    public void traceLength(){
        emulator.getBackend().hook_add_new(new CodeHook() {

            @Override
            public void hook(Backend backend, long address, int size, Object user) {
                count += 1;
            }

            @Override
            public void onAttach(UnHook unHook) {

            }

            @Override
            public void detach() {

            }
        }, module.base, module.size+module.base, null);
    }
}
