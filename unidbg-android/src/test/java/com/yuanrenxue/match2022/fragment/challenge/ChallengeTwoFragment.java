package com.yuanrenxue.match2022.fragment.challenge;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.LibraryResolver;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.StringObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.memory.Memory;

import java.io.File;

/**
 * https://bbs.pediy.com/thread-263345.htm
 */
public class ChallengeTwoFragment {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ChallengeTwoFragment mainActivity = new ChallengeTwoFragment();
        System.out.println("load offset=" + (System.currentTimeMillis() - start) + "ms");
        System.out.println(mainActivity.sign("xxxx"));
    }

    private final AndroidEmulator emulator;
    private final VM vm;

    private ChallengeTwoFragment() {
        emulator = AndroidEmulatorBuilder
                .for64Bit()
                .addBackendFactory(new DynarmicFactory(true))
                .build();
        Memory memory = emulator.getMemory();
        LibraryResolver resolver = new AndroidResolver(23);
        memory.setLibraryResolver(resolver);

        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/yuanrenxue/match2022/fragment/challenge/yuanrenxue17.apk")); //apk
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/java/com/yuanrenxue/match2022/fragment/challenge/so/libmatch02.so"), false);
        dm.callJNI_OnLoad(emulator);
    }

    public String sign(String str){
        DvmObject<?> object = ProxyDvmObject.createObject(vm, this);
        StringObject stringObject = object.callJniMethodObject(emulator, "sign(Ljava/lang/String;)Ljava/lang/String;",
                vm.addLocalObject(new StringObject(vm, str)));
        return stringObject.getValue();
    }




}
