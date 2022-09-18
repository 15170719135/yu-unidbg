package com.muyang._8_21.lesson21;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;

import java.io.File;

// implements IOResolver<AndroidFileIO>
public class main_detectFileNew extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Memory memory;
    private final Module module;
    private DvmObject<?> obj;

    public main_detectFileNew() {
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

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        main_detectFileNew mainActivity = new main_detectFileNew();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");
        //detectFile
        mainActivity.detectFileNew();
        //detectFileNew
    }

    private void detectFileNew() {
        obj.callJniMethod(emulator,"detectFileNew()V");
    }

    @Override
    public DvmObject<?> allocObject(BaseVM vm, DvmClass dvmClass, String signature) {
        if(signature.equals("java/io/File->allocObject")){
            String objkey = String.valueOf(System.currentTimeMillis());
           return vm.resolveClass("java/io/File").newObject(objkey);
        }
        return super.allocObject(vm, dvmClass, signature);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        if(signature.equals("java/io/File-><init>(Ljava/lang/String;)V")){
            String path = (String) vaList.getObjectArg(0).getValue();
            //修改源码的方法
//            dvmObject.setValue(path);
            String key = dvmObject.getValue().toString();
            emulator.set(key,path);

            return;
        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        if(signature.equals("java/io/File->exists()Z")){
            //修改源码的方法
//            String path = (String) dvmObject.getValue();

            //总之就是想办法把 path 保存下来 , 然后校验时返回我们的指定值

            String key = (String) dvmObject.getValue();
            String path =emulator.get(key);

            System.err.println("path"+path);
            if(path.equals("/sys/class/power_supply/battery/voltage_now")){
                return true;
            } else if (path.equals("/data/local/tmp/nox")) {
                return false;
            }
            else if (path.equals("/data/local/tmp/Nox")) {
                return false;
            }



            return true;
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }
}
