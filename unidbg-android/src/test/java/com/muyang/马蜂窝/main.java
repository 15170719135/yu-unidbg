package com.muyang.马蜂窝;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.hook.hookzz.HookEntryInfo;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.hook.hookzz.HookZzArm32RegisterContext;
import com.github.unidbg.hook.hookzz.WrapCallback;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.PackageInfo;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
                .for64Bit()
                .setProcessName("com.mfw.roadbook")
                .build();

        memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/muyang/mfw/mfw10.8.0.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dalvikModule = vm.loadLibrary(new File("unidbg-android/src/test/java/com/muyang/mfw/libmfw.so"), true);
        module = dalvikModule.getModule();
        dalvikModule.callJNI_OnLoad(emulator);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        main mainActivity = new main();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");
        mainActivity.call_396c8();
    }

    private void call_396c8() {
        List<Object> list = new ArrayList<>();
        list.add(vm.getJNIEnv());
        list.add(0);
        DvmObject<?> context = vm.resolveClass("com/mfw/roadbook/MainApplication").newObject(null);
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm,"GET&https%3A%2F%2Fmapi.mafengwo.cn%2Fsearch%2Fsearch%2Fget_result_list%2Fv3&app_code%3Dcom.mfw.roadbook%26app_ver%3D10.8.0%26app_version_code%3D1004%26brand%3Dgoogle%26channel_id%3DZhiHuiYun%26dev_ver%3DD2213.0%26device_id%3D4b1a056b0efac3f3%26device_mid%3D352531082210616%26device_type%3Dandroid%26hardware_model%3DPixel%26has_notch%3D0%26jsondata%3D%257B%2522switched_search_type_by_user%2522%253A0%252C%2522page%2522%253A%257B%2522boundary%2522%253A%25220%2522%252C%2522num%2522%253A10%257D%252C%2522keyword%2522%253A%2522%25E9%259D%2592%25E5%25B2%259B%2522%252C%2522is_correct%2522%253A%25220%2522%252C%2522search_id%2522%253A%25222c5b5364-e178-47ce-b7ed-fc4709e52594%2522%257D%26mfwsdk_ver%3D20140507%26o_coord%3Dgcj%26o_lat%3D30.166782%26o_lng%3D120.160189%26oauth_consumer_key%3D5%26oauth_nonce%3Dca81057d-e742-469c-ac41-95558f85294d%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1659968712%26oauth_token%3D0_0969044fd4edf59957f4a39bce9200c6%26oauth_version%3D1.0%26open_udid%3D4b1a056b0efac3f3%26screen_height%3D1794%26screen_scale%3D2.88%26screen_width%3D1080%26sys_ver%3D8.1.0%26time_offset%3D480%26x_auth_mode%3Dclient_auth")));
        list.add(vm.addLocalObject(new StringObject(vm,"com.mfw.roadbook")));
        Number number = module.callFunction(emulator, 0x396c8, list.toArray());
        System.out.println(vm.getObject(number.intValue()).getValue().toString());
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
