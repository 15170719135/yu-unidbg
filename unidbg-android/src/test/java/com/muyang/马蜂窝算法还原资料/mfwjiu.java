package com.muyang.马蜂窝算法还原资料;

import com.github.unidbg.Emulator;
import com.github.unidbg.hook.hookzz.*;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.utils.Inspector;
import com.sun.jna.Pointer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class mfwjiu extends AbstractJni{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    mfwjiu() {
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.mfw.roadbook").build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/mfw/mfw9.3.7.apk")); // 创建Android虚拟机
        DalvikModule dm = vm.loadLibrary("mfw", true); // 加载so到虚拟内存
        module = dm.getModule(); //获取本SO模块的句柄

        vm.setJni(this);
        vm.setVerbose(true);
        dm.callJNI_OnLoad(emulator);
    };
    public void hook_312E0(){
        // 获取HookZz对象
        IHookZz hookZz = HookZz.getInstance(emulator); // 加载HookZz
        // hook MDStringOld
        hookZz.wrap(module.base + 0x312E0 + 1, new WrapCallback<HookZzArm32RegisterContext>() { // inline wrap导出函数
            @Override
            // 方法执行前
            public void preCall(Emulator<?> emulator, HookZzArm32RegisterContext ctx, HookEntryInfo info) {
                Pointer input = ctx.getPointerArg(0);
                byte[] inputhex = input.getByteArray(0, ctx.getR2Int());
                Inspector.inspect(inputhex, "input");

                Pointer out = ctx.getPointerArg(1);
                ctx.push(out);
            };

            @Override
            // 方法执行后
            public void postCall(Emulator<?> emulator, HookZzArm32RegisterContext ctx, HookEntryInfo info) {
                Pointer output = ctx.pop();
                byte[] outputhex = output.getByteArray(0, 20);
                Inspector.inspect(outputhex, "output");
            }
        });
    };

    public void hook_3151C(){
        // 获取HookZz对象
        IHookZz hookZz = HookZz.getInstance(emulator); // 加载HookZz，支持inline hook，文档看https://github.com/jmpews/HookZz
        // enable hook
        hookZz.enable_arm_arm64_b_branch(); // 测试enable_arm_arm64_b_branch，可有可无
        // hook MDStringOld
        hookZz.wrap(module.base + 0x3151C + 1, new WrapCallback<HookZzArm32RegisterContext>() { // inline wrap导出函数
            @Override
            // 方法执行前
            public void preCall(Emulator<?> emulator, HookZzArm32RegisterContext ctx, HookEntryInfo info) {
                // 类似于Frida args[0]
                Pointer input = ctx.getPointerArg(0);
                byte[] inputhex = input.getByteArray(0, 20);
                Inspector.inspect(inputhex, "IV");

                Pointer text = ctx.getPointerArg(1);
                byte[] texthex = text.getByteArray(0, 64);
                Inspector.inspect(texthex, "block");
                ctx.push(input);
                ctx.push(text);
            };

            @Override
            // 方法执行后
            public void postCall(Emulator<?> emulator, HookZzArm32RegisterContext ctx, HookEntryInfo info) {
                Pointer text = ctx.pop();
                Pointer IV = ctx.pop();

                byte[] IVhex = IV.getByteArray(0, 20);
                Inspector.inspect(IVhex, "IV");

                byte[] outputhex = text.getByteArray(0, 64);
                Inspector.inspect(outputhex, "block out");

            }
        });
        hookZz.disable_arm_arm64_b_branch();
    };



    public String xPreAuthencode(){
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        Object custom = null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(custom);// context
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm, "muyang")));
        list.add(vm.addLocalObject(new StringObject(vm, "com.mfw.roadbook")));

        Number number = module.callFunction(emulator, 0x2e301, list.toArray());
        String result = vm.getObject(number.intValue()).getValue().toString();
        return result;
    }

    public static void main(String[] args) throws Exception {
        mfwjiu test = new mfwjiu();
//        test.hook_312E0();
        test.hook_3151C();
        System.out.println(test.xPreAuthencode());
    }
}
