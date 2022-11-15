package com.xingqiu_test.UnWind;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.debugger.Debugger;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.*;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.DirectoryFileIO;
import com.github.unidbg.linux.file.SimpleFileIO;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.unix.UnixSyscallHandler;
import unicorn.ArmConst;


import java.io.File;
import java.nio.charset.StandardCharsets;

public class UnWind extends AbstractJni implements IOResolver {

    private static final String apkPath = "E:\\BaiduSyncdisk\\sync_pan\\apk\\wb.apk";

    public static void main(String[] args) {
        UnWind unWind = new UnWind();
        //1. hook clock_gettime :用于获取clock所指定的时钟的时间值，返回的时间值置于 ts 所指向的timespec结构中，而函数的返回值为-1或者0，分别代表函数调用失败以及成功
        //
        unWind.myhook();
        //wind.so中的deal函数
        unWind.call();// 分析点
        //https://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@github.com/15170719135/yu-unidbg.git
    }
    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("SO open:"+pathname);
        if(pathname.equals("/proc/self/maps")){
            // do nothing
        }
        if(pathname.equals("/proc/version")){
            //unidbg-android/src/test/java/com/xingqiu_test/UnWind/files/version
            //unidbg-android/src/test/java/com/xingqiu_test/UnWind/files/version
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/java/com/xingqiu_test/UnWind/files/version"), pathname));
        }
        if(pathname.equals("/proc/cpuinfo")){
//            emulator.getUnwinder().unwind();
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/java/com/xingqiu_test/UnWind/files/cpuinfo"), pathname));
        }
        // CPUs that have been identified as being present in the system.
        if(pathname.equals("/sys/devices/system/cpu/present")){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "0-7".getBytes(StandardCharsets.UTF_8)));
        }
        // CPUs that have been allocated resources and can be brought online if they are present.
        if(pathname.equals("/sys/devices/system/cpu/possible")){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "0-7".getBytes(StandardCharsets.UTF_8)));
        }
        if(pathname.equals("/proc/self/auxv")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/java/com/xingqiu_test/UnWind/files/auxv"), pathname));
        }
        // 查看cpu最大频率
        // https://bbs.pediy.com/thread-229579-1.htm
        if("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq".equals(pathname)){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "1766400".getBytes()));
        }
        if(pathname.equals("/proc/meminfo")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/java/com/xingqiu_test/UnWind/files/meminfo"), pathname));
        }
        if(pathname.equals("/proc/"+emulator.getPid()+"/cmdline")){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "com.sina.weibo\0".getBytes(StandardCharsets.UTF_8)));
        }
        if(pathname.equals("/proc/self/maps")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/java/com/xingqiu_test/UnWind/files/fakemaps.txt"), pathname));
        }
        if(pathname.equals("/data/app/com.sina.weibo-x6mgylYN7-50gCnw1ceNwA==/base.apk")){
            return FileResult.success(new SimpleFileIO(oflags, new File(apkPath), pathname));
        }
        if(pathname.equals("ps")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/java/com/xingqiu_test/UnWind/popen/ps.txt"), pathname));
        }
        if (("/proc/" + emulator.getPid() + "/status").equals(pathname)) {
            // todo 检测TracerPid是否为0
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "TracerPid:\t0\n".getBytes()));
        }

        return null;
    }

    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass cNative;

    UnWind() {
        // 创建模拟器实例，使用自定义的SyscallHandler
        AndroidEmulatorBuilder builder = new AndroidEmulatorBuilder(false) {
            public AndroidEmulator build() {
                return new AndroidARMEmulator(processName, rootDir,
                        backendFactories) {
                    @Override
                    protected UnixSyscallHandler<AndroidFileIO>
                    createSyscallHandler(SvcMemory svcMemory) {
                        return new WBARM32SyscallHandler(svcMemory);//todo 重写了获取时间的函数
                    }
                };
            }
        };
        emulator = builder.build();
        // 模拟器的内存操作接口
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));
        // 创建Android虚拟机
        vm = emulator.createDalvikVM(new File(apkPath));
        vm.setJni(this);
        vm.setVerbose(true);
        emulator.getSyscallHandler().addIOResolver(this);
        //我们在导入库里发现了ASensor_getName这一类传感器相关函数，根据开发经验，这些函数来自于 libandroid.so。
        //这个SO很难处理，无法加载，Unidbg中通过虚拟模块对它做了一定的实现，libandroid.so对应于Unidbg中的AndroidModule，我们要把它放到内存中来
        // 自定义虚拟libandroid.so
        new WBAndroidModule(emulator, vm).register(memory);

        // 添加对system_property_get的Hook
        SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                System.out.println("SO call system_property_get:"+key);
                switch (key){
                    case "ro.build.version.sdk":{
                        return "29";
                    }
                    case "ro.serialno":{
                        return "f8a995f5";
                    }
                    case "ro.product.brand":{
                        return "Xiaomi";
                    }
                    case "ro.product.model":{
                        return "MIX 2S";
                    }
                    case "ro.product.manufacturer":{
                        return "Xiaomi";
                    }
                    case "ro.build.product":{
                        return "polaris";
                    }
                    case "ro.product.cpu.abi":{
                        return "arm64-v8a";
                    }
                    case "ro.product.cpu.abilist":{
                        return "arm64-v8a,armeabi-v7a,armeabi";
                    }
                    case "ro.product.device":{
                        return "polaris";
                    }
                    case "ro.build.id":{
                        return "QKQ1.190828.002";
                    }
                    case "ro.build.fingerprint":{
                        return "Xiaomi/polaris/polaris:10/QKQ1.190828.002/V12.0.2.0.QDGCNXM:user/release-keys";
                    }
                    case "ro.build.host":{
                        return "c3-miui-ota-bd134.bj";
                    }
                    case "ro.build.tags":{
                        return "release-keys";
                    }
                    case "ro.build.date.utc":{
                        return "1604422370";
                    }
                    case "build_type":{
                        return "user";
                    }
                    case "ro.build.user":{
                        return "builder";
                    }
                    case "ro.build.version.release":{
                        return "10";
                    }
                    case "ro.build.version.incremental":{
                        return "V12.0.2.0.QDGCNXM";
                    }
                    case "ro.product.board":{
                        return "sdm845";
                    }
                    case "ro.bootloader":{
                        return "unknown";
                    }
                    case "ro.boot.vbmeta.digest":{
                        return "";
                    }
                    case "ro.board.platform":{
                        return "sdm845";
                    }
                    case "ro.sf.lcd_density":{
                        return "440";
                    }
                    case "net.dns1":{
                        return "";
                    }
                    case "ro.opengles.version":{
                        return "196610";
                    }
                    case "ro.debuggable":{
                        return "0";
                    }
                    case "ro.secure":{
                        return "1";
                    }
                    case "init.svc.adbd":{
                        return "stopped";
                    }
                    case "ro.build.display.id":{
                        return "QKQ1.190828.002 test-keys";
                    }
                    case "ro.hardware":{
                        return "qcom";
                    }
                }
                return "";
            };
        });
        memory.addHookListener(systemPropertyHook);

        DalvikModule dm = vm.loadLibrary("wind", true);
        // 加载好的 libhookinunidbg.so对应为一个模块
        module = dm.getModule();
        // hook 一系列可能出问题的导入函数
        // 补完环境后关闭
        // hookLibc();
        // hook popen,从hooklibc中单独拿出来的，因为运行时要用
        hookPopen();
        cNative = vm.resolveClass("com/weibo/ssosdk/LibHelper");
        // 执行JNIOnLoad
        dm.callJNI_OnLoad(emulator);

        // mr0 0x900
        // emulator.attach().addBreakPoint(module.base+0x8f08);
    }

    public void hookLibc(){
        Debugger debugger = emulator.attach();
        debugger.addBreakPoint(module.findSymbolByName("dlopen").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call dlopen");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("dlsym").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call dlysm");
                return false;
            }
        });

//        debugger.addBreakPoint(module.findSymbolByName("clock_gettime").getAddress(), new BreakPointCallback() {
//            @Override
//            public boolean onHit(Emulator<?> emulator, long address) {
//                System.out.println("SO call clock_gettime");
//                return false;
//            }
//        });

        debugger.addBreakPoint(module.findSymbolByName("sigaction").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call sigaction");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("socket").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call socket");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("uname").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call uname");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("stat").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call stat");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("statfs").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call statfs");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("getpid").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call getpid");
                return false;
            }
        });

//        debugger.addBreakPoint(module.findSymbolByName("syscall").getAddress(), new BreakPointCallback() {
//            @Override
//            public boolean onHit(Emulator<?> emulator, long address) {
//                System.out.println("SO call syscall");
//                return false;
//            }
//        });

        debugger.addBreakPoint(module.findSymbolByName("dladdr").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call dladdr");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("getrusage").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call getrusage");
                return false;
            }
        });

        debugger.addBreakPoint(module.findSymbolByName("sysinfo").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call sysinfo");
                return false;
            }
        });

    }

    private void myhook(){
        MemoryBlock v3block = emulator.getMemory().malloc(4, false);
        MemoryBlock macBlock = emulator.getMemory().malloc(0x1000, false);
        final UnidbgPointer v3 = v3block.getPointer();
        final UnidbgPointer macPtr = macBlock.getPointer();

        emulator.attach().addBreakPoint(module, 0x4164, new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("不执行Sub_4164，直接返回符合期待的字符串");
                RegisterContext registerContext = emulator.getContext();
                v3.setPointer(0, macPtr);
                macPtr.setString(0, "[{\"name\":\"bond0\",\"mac\":\"F6:64:A7:9B:C9:F9\"},{\"name\":\"dummy0\",\"mac\":\"52:DC:8E:60:2E:A6\"},{\"name\":\"wlan0\",\"mac\":\"F4:60:E2:96:DB:64\"},{\"name\":\"wlan1\",\"mac\":\"F4:60:E2:17:DB:64\"},{\"name\":\"p2p0\",\"mac\":\"F6:60:E2:18:DB:64\"}]");
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, v3.peer); //r0
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_PC, registerContext.getLRPointer().peer); //r1

                return true;
            }
        });
    }

    private void hookPopen(){
        Debugger debugger = emulator.attach();
        debugger.addBreakPoint(module.findSymbolByName("popen").getAddress(), new BreakPointCallback() {
            // 执行 ps -r
            final UnidbgPointer psptr = UnidbgPointer.pointer(emulator, module.findSymbolByName("fopen").call(emulator, "ps", "r"));
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("SO call popen");
                RegisterContext registerContext = emulator.getContext();
                String cmd = registerContext.getPointerArg(0).getString(0);
                System.out.println("popen arg:"+cmd);
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_PC, registerContext.getLRPointer().peer);
                if(cmd.equals("ps")){
                    emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, psptr.peer);// 把 ps -r 结果 写进去
                }
                return true;
            }
        });
    }

    private void call(){
        String methodSign = "deal(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
        String arg1 = "{\"device_name\":\"PCLM10\"}";
        String arg2 = "01A7YCRFj4qPRAz9pseIeNExMiOIFdx5kXGLQTMyfXSSdpqnE.";
        String arg3 = "2AkMW";
        String arg4 = "5311_4002";
        String arg5 = "10B0095010";
        String arg6 = "11.0.0";
        String arg7 = "00000";
        cNative.newObject(null).callJniMethodObject(emulator, methodSign, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }






}
