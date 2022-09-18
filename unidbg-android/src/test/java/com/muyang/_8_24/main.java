package com.muyang._8_24;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// implements IOResolver<AndroidFileIO>
public class main extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Memory memory;
    private final Module module;
    private final DvmClass manActivity;
    private DvmObject<?> obj;

    public main() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .build();

        memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/muyang/_8_24/MethodID.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dalvikModule = vm.loadLibrary("getpackagename", true);
        module = dalvikModule.getModule();
        vm.callJNI_OnLoad(emulator, module);

        DvmClass ContextdvmClass = vm.resolveClass("android/content/Context");
        DvmClass ContextWrapperdvmClass = vm.resolveClass("android/content/ContextWrapper",ContextdvmClass);
        manActivity = vm.resolveClass("com/example/getpackagename/MainActivity",ContextWrapperdvmClass);

        obj = manActivity.newObject(null);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        main mainActivity = new main();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");
        mainActivity.getAppName();
    }

    private void getAppName() {
        obj.callJniMethod(emulator,"getAppName()V");
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        if(signature.equals("android/app/ActivityThread->getApplication()Landroid/app/Application;")){
            return vm.resolveClass("android/app/Application",manActivity).newObject(null);
        }else if(signature.equals("com/example/getpackagename/MainActivity->getPackageName()Ljava/lang/String;")){
            return new StringObject(vm,vm.getPackageName());
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }
}
