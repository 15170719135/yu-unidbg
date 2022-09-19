package com.muyang._8_23;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// implements IOResolver<AndroidFileIO>
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
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/muyang/_8_23/DogPlus.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dalvikModule = vm.loadLibrary("dogplus", true);
        module = dalvikModule.getModule();
        vm.callJNI_OnLoad(emulator, module);
        obj = vm.resolveClass("com/example/dogplus/MainActivity").newObject(null);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        main mainActivity = new main();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");
        mainActivity.detectAccessibilityManager();
    }

    private void detectAccessibilityManager() {
        obj.callJniMethod(emulator,"detectAccessibilityManager()V");
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        if(signature.equals("android/app/ActivityThread->getApplication()Landroid/app/Application;")){
            return vm.resolveClass("android/app/Application").newObject(null);
        }
        if(signature.equals("android/view/accessibility/AccessibilityManager->getInstalledAccessibilityServiceList()Ljava/util/List;")){
            List<DvmObject<?>> list = new ArrayList<>();
            DvmClass dvmClass = vm.resolveClass("android/accessibilityservice/AccessibilityServiceInfo");
            // todo 自己构造一个 交给虚拟机, 因为下面要用到 这些属性 getObjectField()
            AccessibilityServiceInfo s = new AccessibilityServiceInfo("com.google.android.marvin.talkback.TalkBackService",
                    "com.google.android.marvin.talkback",
                    "TalkBack");
            AccessibilityServiceInfo ss = new AccessibilityServiceInfo("com.ss.android.ugc.aweme.live.livehostimpl.AudioAccessibilityService",
                    "com.ss.android.ugc.aweme",
                    "抖音");
            list.add(dvmClass.newObject(s));
            list.add(dvmClass.newObject(ss));
            return new ArrayListObject(vm,list);
        }
        if(signature.equals("android/accessibilityservice/AccessibilityServiceInfo->getResolveInfo()Landroid/content/pm/ResolveInfo;")){
            return vm.resolveClass("android/content/pm/ResolveInfo").newObject(dvmObject.getValue());
        }
        if(signature.equals("android/content/pm/ServiceInfo->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;")){
            AccessibilityServiceInfo Info = (AccessibilityServiceInfo) dvmObject.getValue();
            return vm.resolveClass("java/lang/CharSequence").newObject(Info.tag);
        }
        if(signature.equals("java/lang/CharSequence->toString()Ljava/lang/String;")){
            return new StringObject(vm,dvmObject.getValue().toString());
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        if(signature.equals("android/content/pm/ResolveInfo->serviceInfo:Landroid/content/pm/ServiceInfo;")){
            return vm.resolveClass("android/content/pm/ServiceInfo").newObject(dvmObject.getValue());
        }
        if(signature.equals("android/content/pm/ServiceInfo->name:Ljava/lang/String;")){ // 返回我们自己构建的对象
            AccessibilityServiceInfo Info = (AccessibilityServiceInfo) dvmObject.getValue();
            return new StringObject(vm,Info.name);
        }
        if(signature.equals("android/content/pm/ServiceInfo->packageName:Ljava/lang/String;")){ // 返回我们自己构建的对象
            AccessibilityServiceInfo Info = (AccessibilityServiceInfo) dvmObject.getValue();
            return new StringObject(vm,Info.packageName);
        }
        if(signature.equals("android/accessibilityservice/AccessibilityServiceInfo->packageNames:[Ljava/lang/String;")){
            return new ArrayObject();
        }
        return super.getObjectField(vm, dvmObject, signature);
    }
}
