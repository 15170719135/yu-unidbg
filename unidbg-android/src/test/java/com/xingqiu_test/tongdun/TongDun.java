package com.xingqiu_test.tongdun;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.*;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.unix.UnixSyscallHandler;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TongDun {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass TongdunClazz;

    public static void main(String[] args) {
        TongDun TD = new TongDun();
        TD.callonSensorChanged();
        TD.callTongdun();
    }
    TongDun(){
        // 创建模拟器实例
        AndroidEmulatorBuilder builder = new AndroidEmulatorBuilder(false){
            @Override
            public AndroidEmulator build() {
                return new AndroidARMEmulator(processName,rootDir,backendFactories) {
                    @Override
                    protected UnixSyscallHandler<AndroidFileIO> createSyscallHandler(SvcMemory svcMemory) {
                        return new TongdunARM32SyscallHandler(svcMemory);
                    }
                };
            }
        };


        emulator = builder.setRootDir(new File("target/rootfs")).build();

        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析

        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/tongdun/runtime/dhgate.apk")); // 创建Android虚拟机
        // 让样本无法获取这些类
        vm.addNotFoundClass("android/provider/MiuiSettings$Ad");
        vm.addNotFoundClass("miui/telephony/TelephonyManagerEx");
        vm.addNotFoundClass("android/telephony/FtTelephonyAdapter");
        vm.addNotFoundClass("android/util/FtDeviceInfo");
        vm.addNotFoundClass("android/telephony/ColorOSTelephonyManager");
        // 虚拟libandroid.so
        new AndroidModule(emulator, vm).register(memory);
        // 补JNI
        vm.setJni(new TongDunJNI(emulator));
        // 补文件访问
        emulator.getSyscallHandler().addIOResolver(new TongDunIO());
        // Hook import functions 模块监听器
        memory.addModuleListener(new hookImports(emulator, "libtongdun.so", "unidbg-android/src/test/resources/tongdun/files/imports.txt"));
//        vm.setVerbose(true); // 设置是否打印Jni调用细节

        String propertyPath = "unidbg-android/src/test/resources/tongdun/files/adbgetprop.txt";
        Map<String, String> propertyMapList = new HashMap<>();
        try {
            propertyMapList = getPropertyMap(propertyPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        final Map<String, String> finalPropertyMapList = propertyMapList;
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                // 这里lib 会获取一些系统属性
                // ro.kernel.qemu 和 libc.debug.malloc不用管
                // 通过adb shell getprop 可以获得全部所需信息
                System.out.println("fuck key:"+key);
                return finalPropertyMapList.get(key);
            }
        });
        emulator.getMemory().addHookListener(systemPropertyHook);

        DalvikModule dm = vm.loadLibrary("tongdun", true);

        module = dm.getModule();

//        Symbol getppid = module.findSymbolByName("getppid");
        dm.callJNI_OnLoad(emulator);

        TongdunClazz = vm.resolveClass("cn/tongdun/android/shell/HelperJNI");

    }

    public void callonSensorChanged(){
        DvmObject<?> sensorManager = vm.resolveClass("android/hardware/SensorManager").newObject(null);
        DvmObject<?> s = vm.resolveClass("cn/tongdun/android/shell/common/s", vm.resolveClass("android/hardware/SensorEventListener")).newObject(null);
        DvmObject<?> sensorEvent = vm.resolveClass("android/hardware/SensorEvent").newObject(null);
        String methodSign = "onSensorChanged(Landroid/hardware/SensorManager;Lcn/tongdun/android/shell/common/s;Landroid/hardware/SensorEvent;)V";
        TongdunClazz.callStaticJniMethod(emulator,methodSign,sensorManager, s, sensorEvent);
    }


    public void callTongdun(){
        String methodSign = "tongdun(Landroid/content/Context;)V";
        TongdunClazz.callStaticJniMethod(emulator,methodSign,vm.resolveClass("android/content/Context").newObject(null));
    }



    private Map<String, String> getPropertyMap(String path) throws IOException {
        File file = new File(path);
        if (!file.exists())
            throw new RuntimeException("Not File!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        Map<String, String> m = new HashMap<>();
        while ((str = br.readLine()) != null) {
            try {
                System.out.println(str);
                String[] splitStr = str.split(": ");
                String key = splitStr[0].replaceAll("\\[|\\]", "");
                String value = splitStr[1].replaceAll("\\[|\\]", "");
                m.put(key, value);
            }catch (Exception ignored) {

            }
        };
        return m;
    }

}
