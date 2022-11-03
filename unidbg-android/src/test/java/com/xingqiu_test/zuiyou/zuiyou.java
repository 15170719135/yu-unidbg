package com.xingqiu_test.zuiyou;

// 导入通用且标准的类库
import com.alibaba.fastjson.JSONObject;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.hook.hookzz.HookEntryInfo;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.hook.hookzz.HookZzArm32RegisterContext;
import com.github.unidbg.hook.hookzz.WrapCallback;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.AssetManager;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import com.sun.jna.Pointer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.lang.Number;
import java.util.Locale;


public class zuiyou extends AbstractJni{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    zuiyou() {
        // 创建模拟器实例,进程名建议依照实际进程名填写，可以规避针对进程名的校验
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("cn.xiaochuankeji.tieba").build();
        // 获取模拟器的内存操作接口
        emulator.getSyscallHandler().setEnableThreadDispatcher(true);
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));
        // 创建Android虚拟机,传入APK，Unidbg可以替我们做部分签名校验的工作
        vm = emulator.createDalvikVM(new File("D:\\hecai_pan\\unidbg\\案例\\第二个小函数\\zuiyou\\right573.apk"));
        //
//        vm = emulator.createDalvikVM(null);

        // 加载目标SO
        DalvikModule dm = vm.loadLibrary(new File("D:\\hecai_pan\\unidbg\\案例\\第二个小函数\\zuiyou\\libnet_crypto.so"), true); // 加载so到虚拟内存
        //获取本SO模块的句柄,后续需要用它
        module = dm.getModule();
        vm.setJni(this); // 设置JNI
        vm.setVerbose(true); // 打印日志

        dm.callJNI_OnLoad(emulator); // 调用JNI OnLoad

        emulator.attach().addBreakPoint(0x5D090+1);
    };
    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "com/izuiyou/common/base/BaseApplication->getAppContext()Landroid/content/Context;":
                return vm.resolveClass("android/content/Context").newObject(null);
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }
    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "android/content/Context->getClass()Ljava/lang/Class;": {
                return dvmObject.getObjectType();
            }
            case "java/lang/Class->getSimpleName()Ljava/lang/String;": {
                return new StringObject(vm, "AppController");
            }
            case "android/content/Context->getFilesDir()Ljava/io/File;":{
                return new StringObject(vm,"/data/user/0/cn.xiaochuankeji.tieba/files");
            }
            case "java/lang/String->getAbsolutePath()Ljava/lang/String;":{
                return new StringObject(vm,"/data/user/0/cn.xiaochuankeji.tieba/files");
            }

        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }
    @Override
    public boolean callStaticBooleanMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "android/os/Debug->isDebuggerConnected()Z":{
                return false;
            }
        }
        return super.callStaticBooleanMethodV(vm,dvmClass,signature,vaList);
    }
    @Override
    public int callStaticIntMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "android/os/Process->myPid()I":{
                return emulator.getPid();
            }
        }
        return super.callStaticIntMethodV(vm,dvmClass,signature,vaList);
    }
    public String cllsign(){
        ArrayList<Object> args = new ArrayList<>(3);
        args.add(vm.getJNIEnv());
        args.add(0);
        String str = "http://api.izuiyou.com/account/login";
        args.add(vm.addLocalObject(new StringObject(vm,str)));
        ByteArray byteArray = new ByteArray(vm, "123456".getBytes(StandardCharsets.UTF_8));
        args.add(vm.addLocalObject(byteArray));
        Number number = module.callFunction(emulator, 0x4a28d, args.toArray());
        String result = vm.getObject(number.intValue()).getValue().toString();
        return result;
    }
    public void callEncode(){
        ArrayList<Object> args = new ArrayList<>(3);
        args.add(vm.getJNIEnv());
        args.add(0);
        String str="{'phone':'17606600458','pw':'e10adc3949ba59ab','region_code':86,'hemera':'BIQsQiSqLKkWsctlN4xzC+T6elyROkghTS121TSvdT77d8KMVayl9aM1keofKShD+heXYXFD+2o4aeagum86AqQ==','h_av':'5.7.3','h_dt':0,'h_os':27,'h_app':'zuiyou','h_model':'Pixel','h_did':'d35aecaa085abc01','h_nt':1,'h_m':275337489,'h_ch':'huawei','h_ts':1647598437037,'token':'TfK8N_4lCtdsVXHVBODJJfQXEkf0zOaq6DVzLfBUYGJdLVjU5aATjx86_auvXYehZ---6SsMCNzbDciFUwW54-U-EMA==','android_id':'d35aecaa085abc01','h_ids':{'meid':'35253108407664','imei1':'352531084076643'}}";
        args.add(vm.addLocalObject(new ByteArray(vm,str.getBytes(StandardCharsets.UTF_8))));
        Number number = module.callFunction(emulator, 0x4a0b9, args.toArray());
        Inspector.inspect((byte[]) vm.getObject(number.intValue()).getValue(),"AESencode");
    }

    public void hookRandom(){
        emulator.attach().addBreakPoint(module.findSymbolByName("lrand48", true).getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                System.out.println("call lrand48");
                emulator.getUnwinder().unwind();
                return true;
            }
        });
    }

    public void callsetProtocolKey(String str){
        ArrayList<Object> args = new ArrayList<>(3);
        args.add(vm.getJNIEnv());
        args.add(0);
        args.add(vm.addLocalObject(new StringObject(vm,str)));
        module.callFunction(emulator, 0x4a479, args.toArray());
    }
    /**
     * hex转byte数组
     * @param hex
     * @return
     */
    public static byte[] hexToByte(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte) intVal;
        }
        return ret;
    }
    public void callDecode(){
        ArrayList<Object> args = new ArrayList<>(3);
        args.add(vm.getJNIEnv());
        args.add(0);
        String str="7C 0D 8F 19 19 19 19 19 19 19 19 19 19 19 19 19 FA 9A EA E0 3A C6 47 C4 D0 87 6E 8D 87 27 AA 65".replace(" ","").toLowerCase(Locale.ROOT);
        args.add(vm.addLocalObject(new ByteArray(vm,hexToByte(str))));
        args.add(1);
        Number number = module.callFunction(emulator, 0x4a14d, args.toArray());
        System.out.println(new String((byte[])vm.getObject(number.intValue()).getValue()));
    }
    public void native_init(){
        ArrayList<Object> args = new ArrayList<>(3);
        args.add(vm.getJNIEnv());
        args.add(0);
        module.callFunction(emulator,0x4a069,args.toArray());
    }

    public void hook(){
        emulator.attach().addBreakPoint(module.base+0x5E1A2,new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
//                String fakeInput = "C1 C1 C1 C1 C1 C1 C1 4B 4B 4B 4B 4B 4B 31 32 33".replace(" ", "");
//                emulator.getBackend().mem_write(0x403df1b8, hexToByte(fakeInput));
                Inspector.inspect(emulator.getBackend().mem_read(0x403eea00, 256), " input ");
                return true;
            }
        });//固定str
        emulator.attach().addBreakPoint(module.base+0xB38A8,new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                String fakeInput = "7C 0D 8F 19 19 19 19 19 19 19 19 19 19 19 19 19".replace(" ", "");
                emulator.getBackend().mem_write(0x403df040, hexToByte(fakeInput));
                Inspector.inspect(emulator.getBackend().mem_read(0x403df040, 16), " 0x403df040 ");
                return true;
            }
        });//固定iv hook sl
        emulator.attach().addBreakPoint(module.base+0x5E0F8,new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                String fakeInput = "37 54 54 54 54 54 54 54 54 54 54 54 54 54 DD DD".replace(" ", "");
                emulator.getBackend().mem_write(0x403df050, hexToByte(fakeInput));
                Inspector.inspect(emulator.getBackend().mem_read(0x403df050, 16), " key ");
                return true;
            }
        });//固定key
        emulator.attach().addBreakPoint(module.base+0x5E274,new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                String fakeInput = "37 54 54 54 54 54 54 54 54 54 54 54 54 54 DD DD".replace(" ", "");
                emulator.getBackend().mem_write(0x403df050, hexToByte(fakeInput));
                Inspector.inspect(emulator.getBackend().mem_read(0x403df050, 16), " 0x403df050 ");
                return true;
            }
        });//固定key
        emulator.attach().addBreakPoint(module.base+0x5D090);
        emulator.attach().addBreakPoint(module.base+0x5D130);
        emulator.attach().addBreakPoint(module.base+0x5D130);
    }
    public static void main(String[] args) {
        zuiyou zuiyou = new zuiyou();
        //zuiyou.hook();
        zuiyou.hookRandom();
        zuiyou.native_init();
        System.out.println("callEncode===========================================");
        zuiyou.callEncode();
    }
}
