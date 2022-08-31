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
        treeMap.put("s","xxx");
        DvmObject<?> dvmObject = ProxyDvmObject.createObject(vm, this);
        /*DvmObject<?> dvmObjectres = dvmObject.callJniMethodObject(emulator, "s(Ljava/util/SortedMap;)Lcom/bilibili/nativelibrary/SignedQuery;",
                vm.resolveClass("java.util.TreeMap").newObject(treeMap));*/

//        StringObject result = (StringObject) ((DvmObject[])((ArrayObject)vm.getObject(number.intValue())).getValue())[0];

        list.add(vm.addLocalObject(vm.resolveClass("java.util.TreeMap").newObject(treeMap)));
        Number number = module.callFunction(emulator, 0x1c97 +1, list.toArray()); //上面日志会打印这个方法地址
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
