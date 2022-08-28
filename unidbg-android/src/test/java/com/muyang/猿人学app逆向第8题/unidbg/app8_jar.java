package com.muyang.猿人学app逆向第8题.unidbg;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

public class app8_jar extends AbstractJni{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private DvmObject ChallengeEightFragment;
    public  byte[] input;
    public app8_jar() throws IOException {
        emulator = AndroidEmulatorBuilder.for64Bit().setProcessName("com.yuanrenxue.match2022").build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("./yuanrenxuem106.apk")); // 创建Android虚拟机
        DalvikModule dm = vm.loadLibrary(new File("./libmatch08.so"), true); // 加载so到虚拟内存
        module = dm.getModule(); //获取本SO模块的句柄
        vm.setJni(this);
        vm.setVerbose(true);
        dm.callJNI_OnLoad(emulator);
        ChallengeEightFragment = vm.resolveClass("com.yuanrenxue.match2022.fragment.challenge.ChallengeEightFragment").newObject(null);
//        emulator.traceCode();
    }


    public String callgetData(int i){
        DvmObject dvmObject = ChallengeEightFragment.callJniMethodObject(emulator, "data(I)Ljava/lang/String;", i);
        return dvmObject.getValue().toString();
    }
    public static void main(String[] args) throws IOException {
        app8_jar test = new app8_jar();
        test.callgetData(1);
    }
}