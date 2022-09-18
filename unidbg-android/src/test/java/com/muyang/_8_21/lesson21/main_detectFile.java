package com.muyang._8_21.lesson21;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;

import java.io.File;

// implements IOResolver<AndroidFileIO>
public class main_detectFile extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Memory memory;
    private final Module module;
    private DvmObject<?> obj;

    public main_detectFile() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .build();

        memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/muyang/_8_21/lesson21/DogLite.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dalvikModule = vm.loadLibrary(new File("unidbg-android/src/test/java/com/muyang/_8_21/lesson21/libdoglite.so"), true);
        module = dalvikModule.getModule();
        vm.callJNI_OnLoad(emulator, module);
        obj = vm.resolveClass("com.example.doglite.MainActivity").newObject(null);
    }

    //这个很简单
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        main_detectFile mainActivity = new main_detectFile();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");
        //detectFile
        mainActivity.detectFile();
        //detectFileNew
    }

    private void detectFile() {
        obj.callJniMethod(emulator,"detectFile()V");
    }

    @Override
    public DvmObject<?> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        if(signature.equals("java/io/File-><init>(Ljava/lang/String;)V")){
            String path = (String) vaList.getObjectArg(0).getValue();
            System.err.println("path---->"+path);
            return vm.resolveClass("java/io/File").newObject(path);
        }
        return super.newObjectV(vm, dvmClass, signature, vaList);
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        if(signature.equals("java/io/File->exists()Z")){
            String path = (String) dvmObject.getValue();
//             (String) vaList.getObjectArg(0).getValue();
            if(path.equals("/sys/class/power_supply/battery/voltage_now")){
                return true;
            }
            if (path.equals("/data/local/tmp/Nox")) {
                return false;
            }
            return true;
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }
}
