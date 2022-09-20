package com.muyang._8_22;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;

import java.io.File;

// 补 Base64.encodeToString() 方法
public class main extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Memory memory;
    private final Module module;
    private DvmObject<?> obj;

    public main() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .build();

        memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/muyang/_8_21/lesson21/DogLite.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dalvikModule = vm.loadLibrary("doglite", true);
        module = dalvikModule.getModule();
        vm.callJNI_OnLoad(emulator, module);
        obj = vm.resolveClass("com.example.doglite.MainActivity").newObject(null);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        main mainActivity = new main();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");
//        mainActivity.SysInfo();
//        mainActivity.getAppFilesDir();
        mainActivity.base64result();
    }

    private void base64result() {
        String input = "muyang";
        obj.callJniMethod(emulator,"base64result(Ljava/lang/String;)V",input);
    }

    private void getAppFilesDir() {
        obj.callJniMethod(emulator,"getAppFilesDir()V");
    }

    private void SysInfo() {
        obj.callJniMethod(emulator,"SysInfo()V");
    }
//  SysInfo
//    @Override
//    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
//        if(signature.equals("android/app/ActivityThread->getApplication()Landroid/app/Application;")){
//            return vm.resolveClass("android.app.Application").newObject(null);
//        }
//        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
//    }
//
//    @Override
//    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
//        if(signature.equals("android/provider/Settings$Secure->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;")){
//            String s = (String) vaList.getObjectArg(1).getValue();
//            System.out.println("args:"+s);
//            return  new StringObject(vm,"123456");
//        }
//        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
//    }



//  getAppFilesDir
//    @Override
//    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
//        if(signature.equals("android/os/Environment->getExternalStorageDirectory()Ljava/io/File;")){
//            return vm.resolveClass("java.io.File").newObject(signature);
//        }else if(signature.equals("android/os/Environment->getStorageDirectory()Ljava/io/File;")){
//            return vm.resolveClass("java.io.File").newObject(signature);
//        }
//        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
//    }
//
//    @Override
//    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
//        if(signature.equals("java/io/File->getAbsolutePath()Ljava/lang/String;")){
//            String tag = dvmObject.getValue().toString();
//            if(tag.equals("android/os/Environment->getExternalStorageDirectory()Ljava/io/File;")){
//                return new StringObject(vm,"/sdcard/");
//            }else if(tag.equals("android/os/Environment->getStorageDirectory()Ljava/io/File;")){
//                return new StringObject(vm,"/");
//            }
//        }
//        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
//    }

    // base64

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        if(signature.equals("android/util/Base64->encodeToString([BI)Ljava/lang/String;")){
            byte[] input = (byte[]) vaList.getObjectArg(0).getValue();
            int i = vaList.getIntArg(1);
            String s = Base64.encodeToString(input, i);
            return new StringObject(vm,s);
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }
}
