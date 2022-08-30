package com.example.hookinunidbg;
 
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import com.sun.jna.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import unicorn.ArmConst;
 
import java.io.File;
import java.nio.charset.StandardCharsets;
 
public class HookInUnidbgByConsoleDebugger {
    private static final Log logger = LogFactory.getLog(HookInUnidbgByConsoleDebugger.class);
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    public static void main(String[] args) {
        HookInUnidbgByConsoleDebugger mydemo = new HookInUnidbgByConsoleDebugger();
//        单行进行hook
//        mydemo.HookByConsoleDebugger4SingleLine();
//       对输入、输出进行hook
        //      mydemo.HookByConsoleDebugger();

        mydemo.replaceArgByConsoleDebugger();
        mydemo.call();
    }
 
    HookInUnidbgByConsoleDebugger() {
 
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
    //todo 1
    public void HookByConsoleDebugger4SingleLine(){
        long base64_encode_address = module.findSymbolByName("base64_encode").getAddress();
        logger.info("base64_encode_address="+base64_encode_address);
        emulator.attach().addBreakPoint(module.findSymbolByName("base64_encode").getAddress());
 
    }
    //todo 2
    //    int __fastcall base64_encode(int a1, unsigned int a2, int a3)
//    j_base64_encode("lilac", 5, v0)
    public void HookByConsoleDebugger(){
        long base64_encode_address = module.findSymbolByName("base64_encode").getAddress();
        logger.info("base64_encode_address="+base64_encode_address);
        emulator.attach().addBreakPoint(base64_encode_address, new BreakPointCallback() {
            UnidbgPointer outPointer = null;
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                RegisterContext context = emulator.getContext();
                Pointer input = context.getPointerArg(0);
                int length = context.getIntArg(1);
                System.out.println("base64 入参arg0:"+input.getString(0));
                System.out.println("base64 入参arg1:"+length);
                outPointer = context.getPointerArg(2);
//                打印输入内容
                Inspector.inspect(input.getByteArray(0, length), "base64 input");
                // OnLeave
                emulator.attach().addBreakPoint(context.getLRPointer().peer, new BreakPointCallback() {
                    @Override
                    public boolean onHit(Emulator<?> emulator, long address) {
//                        根据ARM ATPCS调用约定， 函数的返回值总是通过R0传递回来。
 
                        RegisterContext context = emulator.getContext();
                        int rep = context.getIntArg(0);
                        System.out.println("base64 返回结果:"+rep);
                        String repString = outPointer.getString(0);
                        System.out.println("base64 加密后的结果:"+repString);
                        //BreakPointCallback:返回<code>false</code>表示断点成功，返回<code>true</code>表示不触发断点，继续进行
                        return true;
                    }
                });
                //BreakPointCallback:返回<code>false</code>表示断点成功，返回<code>true</code>表示不触发断点，继续进行
                return true;
            }
        });
    }
 
    // 通过 addBreakPoint 添加
    public void replaceArgByConsoleDebugger(){
        emulator.attach().addBreakPoint(module.findSymbolByName("base64_encode").getAddress(), new BreakPointCallback() {
            Pointer outbuffer = null;
            @Override
            public boolean onHit(Emulator<?> emulator, long address) { //进入切入点
                RegisterContext context = emulator.getContext();
                String fakeInput = "hello world";
                int length = fakeInput.length();
                // 修改r1值为新长度
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R1, length);
                MemoryBlock fakeInputBlock = emulator.getMemory().malloc(length, true);
                fakeInputBlock.getPointer().write(fakeInput.getBytes(StandardCharsets.UTF_8));
                // 修改r0为指向新字符串的新指针
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, fakeInputBlock.getPointer().peer);
 
                outbuffer = context.getPointerArg(2);
                // OnLeave
                emulator.attach().addBreakPoint(context.getLRPointer().peer, new BreakPointCallback() {
                    @Override
                    public boolean onHit(Emulator<?> emulator, long address) {
                        String result = outbuffer.getString(0);
                        System.out.println("base64 result:"+result);
                        return true;
                    }
                });
                return true;
            }
        });
    }
 
//    调用native方法
    public void call(){
        DvmClass dvmClass = vm.resolveClass("com/example/hookinunidbg/MainActivity");
        String methodSign = "call()V";
        DvmObject<?> dvmObject = dvmClass.newObject(null);
 
        dvmObject.callJniMethodObject(emulator, methodSign);
    }

 
 
 
}