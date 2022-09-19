package com.muyang._8_31;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.*;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.unix.UnixSyscallHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class getproperty extends AbstractJni implements IOResolver<AndroidFileIO>{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    public final Memory memory;

    getproperty() {
//        emulator = AndroidEmulatorBuilder.for32Bit().setRootDir(new File("target/rootfs")).setProcessName("xxxx").build();
        AndroidEmulatorBuilder builder = new AndroidEmulatorBuilder(false){
            @Override
            public AndroidEmulator build() {
                return new AndroidARMEmulator(processName,rootDir,backendFactories){
                    @Override
                    protected UnixSyscallHandler<AndroidFileIO> createSyscallHandler(SvcMemory svcMemory) {
                        return new MyARMSyscallHandler(svcMemory);
                    }
                };
            }
        };

        emulator = builder.setProcessName("com.muyang.lesson31").build();
        memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析

        emulator.getSyscallHandler().addIOResolver(this);
        SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                System.out.println("muyang getProperty key:"+key);
                switch (key){
                    case "ro.build.id":
                        return "12345";
                }
                return "";
            }
        });
        memory.addHookListener(systemPropertyHook);



        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/muyang/lesson31/app-debug.apk"));
        vm.setVerbose(true);

        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/java/com/muyang/lesson31/libnative-lib.so"), true);
        module = dm.getModule();
        vm.setJni(this);
        dm.callJNI_OnLoad(emulator);
    }
    public int call(){
        List<Object> objectList = new ArrayList<>();
        objectList.add(vm.getJNIEnv());
        objectList.add(0);
        Number number = module.callFunction(emulator, 0x9259, objectList.toArray());
        return number.intValue(); }
    public static void main(String[] args) {
        getproperty test = new getproperty();
        test.call();
    }
    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "android/os/Build->MODEL:Ljava/lang/String;":
                return new StringObject(vm, "Pixel");
        }
        return super.getStaticObjectField(vm, dvmClass, signature);
    }

    @Override
    public FileResult<AndroidFileIO> resolve(Emulator<AndroidFileIO> emulator, String pathname, int oflags) {
        System.out.println("muyang pathname:"+pathname);
        return null;
    }
}

