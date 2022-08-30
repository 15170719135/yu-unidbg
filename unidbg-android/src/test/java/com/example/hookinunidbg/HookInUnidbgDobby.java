package com.example.hookinunidbg;
 
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.hook.HookContext;
import com.github.unidbg.hook.ReplaceCallback;
import com.github.unidbg.hook.hookzz.*;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import com.sun.jna.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
import java.io.File;
 
public class HookInUnidbgDobby {
    private static final Log logger = LogFactory.getLog(HookInUnidbgDobby.class);
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    public static void main(String[] args) {
        HookInUnidbgDobby mydemo = new HookInUnidbgDobby();
        mydemo.hookByDobby();
        mydemo.call();
    }
    HookInUnidbgDobby() {
 
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
 
    public void hookByDobby(){
        Dobby dobby = Dobby.getInstance(emulator);
        /**
         * 只是打印入参和出参, 而之前你的案例已经后很多类似的功能啦,所以想问下.. 有它的特点吗 -- 暂时不知道
         */
        long base64_encode_address = module.findSymbolByName("base64_encode").getAddress();
        System.out.println("base64_encode_address:"+base64_encode_address);
        dobby.replace(base64_encode_address, new ReplaceCallback() { // 使用Dobby inline hook导出函数
            UnidbgPointer outPointer = null;
            @Override
            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
                Pointer input = context.getPointerArg(0);
                int length = context.getIntArg(1);
                outPointer = context.getPointerArg(2);
                System.out.println("hookByDobby base64_encode_address.onCall 入参arg0:"+input.getString(0));
                System.out.println("hookByDobby base64_encode_address.onCall 入参arg1:"+length);
                System.out.println("hookByDobby base64_encode_address.onCall originFunction=0x" + Long.toHexString(originFunction));
 
                context.push(1);
                context.push(2);
                context.push(3);
                return HookStatus.RET(emulator, originFunction);
            }
            @Override
            public void postCall(Emulator<?> emulator, HookContext context) {
                System.out.println("hookByDobby base64.postCall 返回结果： R0=" + context.getIntArg(0));
                String repString = outPointer.getString(0);
                System.out.println("hookByIHookZz base64 加密后的结果:"+repString);
 
                int pushResult0 = context.pop();
                int pushResult1 = context.pop();
                int pushResult2 = context.pop();
                System.out.println("hookByIHookZz base64 pushResult0="+pushResult0+",pushResult1="+pushResult1+",pushResult2="+pushResult2);
 
            }
        }, true);
 
    }
 
 
//    调用native方法
    public void call(){
        DvmClass dvmClass = vm.resolveClass("com/example/hookinunidbg/MainActivity");
        String methodSign = "call()V";
        DvmObject<?> dvmObject = dvmClass.newObject(null);
 
        dvmObject.callJniMethodObject(emulator, methodSign);
    }

 
}