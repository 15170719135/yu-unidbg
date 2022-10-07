package com.xingqiu_test.dfa;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.Backend;
import com.github.unidbg.arm.backend.ReadHook;
import com.github.unidbg.arm.backend.UnHook;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnidbgPointer;
import unicorn.ArmConst;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class LKAES extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    public LKAES(){
        emulator = AndroidEmulatorBuilder.for32Bit().build(); // 创建模拟器实例，要模拟32位或者64位，在这里区分

        // 模拟器的内存操作接口
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));
        // 创建Android虚拟机
        vm = emulator.createDalvikVM(new File("C:\\D\\YiDong_Pan\\unidbg\\从黑盒攻击模型到白盒攻击模型\\第6讲——使用Frida进行DFA攻击\\files\\ruixingkafei_4.9.3.apk"));
        vm.setVerbose(true);
        // 加载so到虚拟内存
        DalvikModule dm = vm.loadLibrary("cryptoDD", true);
        module = dm.getModule();
        // 设置JNI
        vm.setJni(this);
        dm.callJNI_OnLoad(emulator);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString.isEmpty()) {
            return null;
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index  > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    public static String bytesTohexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public void call_wbaes(){
        MemoryBlock inblock = emulator.getMemory().malloc(16, true);
        UnidbgPointer inPtr = inblock.getPointer();
        MemoryBlock outblock = emulator.getMemory().malloc(16, true);
        UnidbgPointer outPtr = outblock.getPointer();
        byte[] stub = hexStringToBytes("30313233343536373839616263646566");
        assert stub != null;
        inPtr.write(0, stub, 0, stub.length);
        module.callFunction(emulator, 0x17bd5, inPtr, 16, outPtr, 0);
        String ret = bytesTohexString(outPtr.getByteArray(0, 0x10));
        System.out.println(ret);
        inblock.free();
        outblock.free();
    }

    // 生成随机数
    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public void dfaAttack(){
        //定位到aes的位值
        emulator.attach().addBreakPoint(module.base + 0x14F98 + 1, new BreakPointCallback() {
            int count = 0;
            UnidbgPointer pointer;
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                count += 1;
                RegisterContext registerContext = emulator.getContext();
                pointer = registerContext.getPointerArg(0);
                emulator.attach().addBreakPoint(registerContext.getLRPointer().peer, new BreakPointCallback() {
                    @Override
                    public boolean onHit(Emulator<?> emulator, long address) {
                    if(count % 9 == 0){ // 前10轮 加密
                        pointer.setByte(randInt(0, 15), (byte) randInt(0, 0xff)); // 只修改首位
                    }
                    return true;
                    }
                });

                return true;
            }
        });
    }

    public void traceAESRead(){
        emulator.getBackend().hook_add_new(new ReadHook() {
            @Override
            public void hook(Backend backend, long address, int size, Object user) {
                long now = emulator.getBackend().reg_read(ArmConst.UC_ARM_REG_PC).intValue();
                if((now>module.base) & (now < (module.base+module.size))){
                    System.out.println(now - module.base);
                }
            }

            @Override
            public void onAttach(UnHook unHook) {

            }

            @Override
            public void detach() {

            }
        }, module.base, module.base+module.size, null);
    }


    public static void main(String[] args) {
        LKAES lkaes = new LKAES();
//        lkaes.traceAESRead();
        lkaes.dfaAttack();
        for(int i =0;i<200;i++){
            lkaes.call_wbaes();
        }

    }
}