package com.bilibili.nativelibrary;
import com.github.unidbg.Emulator;
import com.github.unidbg.hook.hookzz.*;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.PackageInfo;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.utils.Inspector;
import com.mfw.main;
import com.sun.jna.Pointer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class LibBili extends AbstractJni {
    private final AndroidEmulator emulator; //AndroidARM64Emulator
    private final VM vm;
    private final Memory memory;
    private final Module module;
    private DvmObject<?> obj;

    public LibBili() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .setProcessName("tv.danmaku.bili")
                .build();

        memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("D:\\hecai_pan\\apk\\b站.apk")); //公司
//        vm = emulator.createDalvikVM(new File("C:\\D\\YiDong_Pan\\apk\\b站.apk"));//家
//        vm = emulator.createDalvikVM();
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dalvikModule = vm.loadLibrary(new File("unidbg-android/src/test/java/com/bilibili/nativelibrary/so/libbili.so"), true);
        module = dalvikModule.getModule();
        dalvikModule.callJNI_OnLoad(emulator);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        LibBili mainActivity = new LibBili();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");

        mainActivity.call_396c8();
    }

    private void call_396c8() {

        DvmObject<?> context = vm.resolveClass("com.bilibili.nativelibrary.LibBili").newObject(null);

        List<Object> list = new ArrayList<>();
        list.add(vm.getJNIEnv());
        list.add(context.hashCode()); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0这里是不行的，此样本参数2被使用了


        TreeMap<String,String> treeMap = new TreeMap<>();
        treeMap.put("ad_extra", "E1133C23F36571A3F1FDE6B325B17419AAD45287455E5292A19CF51300EAF0F2664C808E2C407FBD9E50BD48F8ED17334F4E2D3A07153630BF62F10DC5E53C42E32274C6076A5593C23EE6587F453F57B8457654CB3DCE90FAE943E2AF5FFAE78E574D02B8BBDFE640AE98B8F0247EC0970D2FD46D84B958E877628A8E90F7181CC16DD22A41AE9E1C2B9CB993F33B65E0B287312E8351ADC4A9515123966ACF8031FF4440EC4C472C78C8B0C6C8D5EA9AB9E579966AD4B9D23F65C40661A73958130E4D71F564B27C4533C14335EA64DD6E28C29CD92D5A8037DCD04C8CCEAEBECCE10EAAE0FAC91C788ECD424D8473CAA67D424450431467491B34A1450A781F341ABB8073C68DBCCC9863F829457C74DBD89C7A867C8B619EBB21F313D3021007D23D3776DA083A7E09CBA5A9875944C745BB691971BFE943BD468138BD727BF861869A68EA274719D66276BD2C3BB57867F45B11D6B1A778E7051B317967F8A5EAF132607242B12C9020328C80A1BBBF28E2E228C8C7CDACD1F6CC7500A08BA24C4B9E4BC9B69E039216AA8B0566B0C50A07F65255CE38F92124CB91D1C1C39A3C5F7D50E57DCD25C6684A57E1F56489AE39BDBC5CFE13C540CA025C42A3F0F3DA9882F2A1D0B5B1B36F020935FD64D58A47EF83213949130B956F12DB92B0546DADC1B605D9A3ED242C8D7EF02433A6C8E3C402C669447A7F151866E66383172A8A846CE49ACE61AD00C1E42223");
        treeMap.put("appkey", "1d8b6e7d45233436");
        treeMap.put("autoplay_card", "11");
        treeMap.put("banner_hash", "10687342131252771522");
        treeMap.put("build", "6180500");
        treeMap.put("c_locale", "zh_CN");
        treeMap.put("channel", "shenma117");
        treeMap.put("column", "2");
        treeMap.put("device_name", "MIX2S");
        treeMap.put("device_type", "0");
        treeMap.put("flush", "6");
        treeMap.put("ts", "1612693177");
        DvmObject<?> dvmObject = ProxyDvmObject.createObject(vm, this);
        /*DvmObject<?> dvmObjectres = dvmObject.callJniMethodObject(emulator, "s(Ljava/util/SortedMap;)Lcom/bilibili/nativelibrary/SignedQuery;",
                vm.resolveClass("java.util.TreeMap").newObject(treeMap));*/

//        StringObject result = (StringObject) ((DvmObject[])((ArrayObject)vm.getObject(number.intValue())).getValue())[0];
        DvmClass Map = vm.resolveClass("java/util/Map");
        DvmClass AbstractMap = vm.resolveClass("java/util/AbstractMap",Map);
        DvmObject<?> input_map = vm.resolveClass("java/util/TreeMap", AbstractMap).newObject(treeMap);
        list.add(vm.addLocalObject(input_map));
        Number number = module.callFunction(emulator, 0x1c97 , list.toArray()); //上面日志会打印这个方法地址
//        System.out.println(dvmObjectres);

//        System.out.println(vm.getObject(number.intValue()).getValue().toString());
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        if(signature.equals("com/mfw/roadbook/MainApplication->getPackageManager()Landroid/content/pm/PackageManager;")){
            return vm.resolveClass("android/content/pm/PackageManage").newObject(null);
        }
        if(signature.equals("com/mfw/roadbook/MainApplication->getPackageName()Ljava/lang/String;")){
            return new StringObject(vm,vm.getPackageName());
        }
        if(signature.equals("android/content/pm/PackageManage->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;")){
            StringObject packageName = vaList.getObjectArg(0);
            int flags = vaList.getIntArg(1);
            return new PackageInfo(vm, packageName.getValue(), flags);
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

}
