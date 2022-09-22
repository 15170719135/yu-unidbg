package com.muyang._8_32;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.*;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.unix.UnixSyscallHandler;
import com.github.unidbg.utils.Inspector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class qutoutiao extends AbstractJni implements IOResolver<AndroidFileIO>{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    public final Memory memory;

    public static void main(String[] args) {
        qutoutiao test = new qutoutiao();
        test.call_0x8ff1();
    }
    qutoutiao() {
//        emulator = AndroidEmulatorBuilder.for32Bit().setRootDir(new File("target/rootfs")).setProcessName("com.jifen.qukan").build();
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

        emulator = builder.setProcessName("com.jifen.qukan").build();
        memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析

        emulator.getSyscallHandler().addIOResolver(this);

        SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                System.out.println("获取系统环境变量 key为 :"+key);
                switch (key){
                    case "ro.serialno": // todo 手机里 getprop 得到值去补 , 如 getprop ro.serialno
                        return "FA69R0301889";
                    case "ro.product.manufacturer":
                        return "Google";
                    case "ro.product.brand":
                        return "google";
                    case "ro.product.model":
                        return "Pixel";
                }
                return "";
            }
        });
        memory.addHookListener(systemPropertyHook);
        vm = emulator.createDalvikVM(new File("C:\\D\\YiDong_Pan\\apk\\qutoutiao.apk"));


        DalvikModule LibcModule = vm.loadLibrary(new File("unidbg-android/src/main/resources/android/sdk23/lib/libc.so"), true);
        Module moduleLibc = LibcModule.getModule();

        //todo 给 popen() 函数下debug 级别日志
        int popenAddress = (int) moduleLibc.findSymbolByName("popen").getAddress();
        emulator.attach().addBreakPoint(popenAddress, new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                RegisterContext context = emulator.getContext();
                String shuxin = context.getPointerArg(0).getString(0);
                System.out.println("popen 命令行窗口, 命令为为:"+shuxin);
//                emulator.attach().debug();// duandian
                emulator.set("shuxin",shuxin); // todo set 进去 , MyARMSyscallHandler emulator.get("shuxin") 出来
                return true;
            }
        });

        vm.setVerbose(true);

        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/java/com/muyang/_8_32/libyoda.so"), true);
        module = dm.getModule();


        vm.setJni(this);
        dm.callJNI_OnLoad(emulator);
    }


    private void call_0x8ff1() {
        List<Object> objectList = new ArrayList<>();
        objectList.add(vm.getJNIEnv());
        objectList.add(0);
        String str1 = "{\"instance\":\"com.inno.yodasdk.info.Infos@f4bc55b\",\"app_version\":\"3.10.48.000.0714.1521\",\"sensor_count\":\"27\",\"mac\":\"AC:37:43:4F:82:5E\",\"platform\":\"android\",\"manufacturer\":\"Google\",\"scene\":\"qtt_login\",\"sid\":\"1f3be17a-0a22-480d-9d17-12394ba4591a\",\"cpu_model\":\"0 ,4,2150400\",\"sdk_version\":\"1.0.7.210128\",\"model\":\"Pixel\",\"screen_size\":\"1080,1920,2.625\",\"brand\":\"google\",\"adb\":\"1\",\"gyro\":\"0.07,-0.04,1.01\",\"hardware\":\"sailfish\",\"screen_scale\":\"5.0\",\"os_version\":\"27\",\"serial_number\":\"FA69R0301889\",\"sim_state\":\"1\",\"screen_brightness\":\"82\",\"volume\":\"5,0,0,0,6\",\"boot_time\":\"1662280407318\",\"wifi_name\":\"<unknown ssid>\",\"tk\":\"ACFbtSqZ2b_tcxjzTSGbtpCT6BMdQioa4VY0NzUxNDk1MDg5NTIyNQ\",\"charge_state\":\"1\",\"package_name\":\"com.jifen.qukan\",\"imei\":\"352531082210616\",\"wifi_mac\":\"02:00:00:00:00:00\",\"apps_count\":\"29,167\",\"android_id\":\"a5a2da3f9afb73da\",\"cid\":\"47514950895225\"}, str2: dubo, str3: 1662282407\n" +
                "bulwark is called, str: {\"instance\":\"com.inno.yodasdk.info.Infos@f4bc55b\",\"app_version\":\"3.10.48.000.0714.1521\",\"sensor_count\":\"27\",\"mac\":\"AC:37:43:4F:82:5E\",\"platform\":\"android\",\"manufacturer\":\"Google\",\"scene\":\"qtt_login\",\"sid\":\"4b382dd2-9fe3-4eb1-b2e4-e79c63a4bcdf\",\"cpu_model\":\"0 ,4,2150400\",\"sdk_version\":\"1.0.7.210128\",\"model\":\"Pixel\",\"screen_size\":\"1080,1920,2.625\",\"brand\":\"google\",\"adb\":\"1\",\"gyro\":\"0.07,-0.04,1.01\",\"hardware\":\"sailfish\",\"ext\":\"{\\\"login_way\\\":\\\"2\\\"}\",\"screen_scale\":\"5.0\",\"os_version\":\"27\",\"serial_number\":\"FA69R0301889\",\"sim_state\":\"1\",\"screen_brightness\":\"82\",\"volume\":\"5,0,0,0,6\",\"boot_time\":\"1662280407317\",\"wifi_name\":\"<unknown ssid>\",\"tk\":\"ACFbtSqZ2b_tcxjzTSGbtpCT6BMdQioa4VY0NzUxNDk1MDg5NTIyNQ\",\"charge_state\":\"1\",\"package_name\":\"com.jifen.qukan\",\"imei\":\"352531082210616\",\"wifi_mac\":\"02:00:00:00:00:00\",\"apps_count\":\"29,167\",\"android_id\":\"a5a2da3f9afb73da\",\"cid\":\"47514950895225\"}";
        String str2 = "dudo";
        String str3 = "1662282513";
        StringObject obj1 = new StringObject(vm,str1);
        objectList.add(vm.addLocalObject(obj1));
        StringObject obj2= new StringObject(vm,str2);
        objectList.add(vm.addLocalObject(obj2));
        StringObject obj3 = new StringObject(vm,str3);
        objectList.add(vm.addLocalObject(obj3));

        Number number = module.callFunction(emulator, 0x8ff1, objectList.toArray());
        byte[] result = (byte[]) vm.getObject(number.intValue()).getValue();
        Inspector.inspect(result,"result");
    }

    @Override
    public FileResult<AndroidFileIO> resolve(Emulator<AndroidFileIO> emulator, String pathname, int oflags) {
        System.out.println("muyang pathname:"+pathname);
        return null;
    }
}

