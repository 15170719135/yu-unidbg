package com.xingqiu_test.tongdun;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.Enumeration;
import com.github.unidbg.linux.android.dvm.api.ApplicationInfo;
import com.github.unidbg.linux.android.dvm.api.ClassLoader;
import com.github.unidbg.linux.android.dvm.api.PackageInfo;
import com.github.unidbg.linux.android.dvm.api.SystemService;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;

import com.xingqiu_test.tongdun.accessibilityservice.AccessibilityServiceInfo;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TongDunJNI extends AbstractJni {
    private final AndroidEmulator emulator;

    public TongDunJNI(AndroidEmulator emulator){
        this.emulator = emulator;

    }

    @Override
    public void setStaticBooleanField(BaseVM vm, DvmClass dvmClass, String signature, boolean value) {
        switch (signature){
            // 设置 SOLOADSUCCESS域为true，即表明SO已经顺利加载
            case "cn/tongdun/android/shell/FMAgent->SOLOADSUCCESS:Z":{
                return;
            }
        }
        super.setStaticBooleanField(vm, dvmClass, signature, value);
    }

    @Override
    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature){
            // 或许是配置同盾指纹Key的最大长度
            // cn.tongdun.android.shell.FMAgent
            //  int i = blackboxMaxSize;
            //  if (i < 5120) {
            //      i = 5120;
            //  }
            //  mBlackboxMaxSize = i;
            case "cn/tongdun/android/shell/FMAgent->mBlackboxMaxSize:I":{
                return 5120;
            }

            // 获取Android版本
            case "android/os/Build$VERSION->SDK_INT:I":{
                return 29;
            }

            // 不清楚
            case "cn/tongdun/android/shell/FMAgent->FAKEVALUE:I":{
                return 0;
            }
            // 判断是否处于debug模式
            // public static boolean isDebuggable(Context context) {
            //    return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            //}
            case "android/content/pm/ApplicationInfo->FLAG_DEBUGGABLE:I":{
                return 1<<1;
            }
        }
        return super.getStaticIntField(vm, dvmClass, signature);
    }


    @Override
    public boolean getStaticBooleanField(BaseVM vm, DvmClass dvmClass, String signature) {
        // 读取一系列配置信息，每个具体字段意义不明
        switch (signature){
            case "cn/tongdun/android/shell/FMAgent->skipGps:Z":{
                return false;
            }
            case "cn/tongdun/android/shell/FMAgent->alwaysDemotion:Z":{
                return false;
            }
            case "cn/tongdun/android/shell/FMAgent->skipGid:Z":{
                return false;
            }
            case "cn/tongdun/android/shell/FMAgent->overrideCerti:Z":{
                return false;
            }
            case "cn/tongdun/android/shell/FMAgent->installpackagesEnable:Z":{
                return true;
            }
            case "cn/tongdun/android/shell/FMAgent->SENSOR_ENABLE:Z":{
                return true;
            }
            case "cn/tongdun/android/shell/FMAgent->killDbg:Z":{
                return false;
            }
        }
        return super.getStaticBooleanField(vm, dvmClass, signature);
    }

    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature){
            case "cn/tongdun/android/shell/FMAgent->partnerCode:Ljava/lang/String;":{
                return new StringObject(vm, "dunhuang");
            }
            case "cn/tongdun/android/shell/FMAgent->domain:Ljava/lang/String;":{
                return null;
            }
            case "cn/tongdun/android/shell/FMAgent->custProcess:Ljava/lang/String;":{
                return null;
            }
            case "cn/tongdun/android/shell/FMAgent->customUrl:Ljava/lang/String;":{
                return null;
            }
            case "cn/tongdun/android/shell/FMAgent->doubleUrl:Ljava/lang/String;":{
                return null;
            }
            case "cn/tongdun/android/shell/FMAgent->proxyUrl:Ljava/lang/String;":{
                return null;
            }
            case "cn/tongdun/android/shell/FMAgent->googleAid:Ljava/lang/String;":{
                return null;
            }
            case "cn/tongdun/android/shell/FMAgent->appName:Ljava/lang/String;":{
                return null;
            }
            case "cn/tongdun/android/shell/FMAgent->channelNo:Ljava/lang/String;":{
                return new StringObject(vm, "");
            }
            case "cn/tongdun/android/shell/FMAgent->CURRENT_ENV:Ljava/lang/String;":{
                return new StringObject(vm, "production");
            }
            case "cn/tongdun/android/shell/FMAgent->VERSION:Ljava/lang/String;":{
                return new StringObject(vm, "3.6.6");
            }
            case "de/robv/android/xposed/XposedBridge->sHookedMethodCallbacks:Ljava/util/Map;":{
                return vm.resolveClass("java/util/Map").newObject(signature);
            }
            case "android/os/Build$VERSION->RELEASE:Ljava/lang/String;":{
                return new StringObject(vm, "10");
            }
            case "android/os/Build->FINGERPRINT:Ljava/lang/String;":{
                return new StringObject(vm, "Xiaomi/polaris/polaris:10/QKQ1.190828.002/V12.0.2.0.QDGCNXM:user/release-keys");
            }
            case "android/os/Build->TAGS:Ljava/lang/String;":{
                return new StringObject(vm, "release-keys");
            }
            case "android/os/Build->PRODUCT:Ljava/lang/String;":{
                return new StringObject(vm, "polaris");
            }
            case "android/os/Build->DISPLAY:Ljava/lang/String;":{
                return new StringObject(vm, "QKQ1.190828.002 test-keys");
            }
            case "android/os/Build->HOST:Ljava/lang/String;":{
                return new StringObject(vm, "c3-miui-ota-bd134.bj");
            }
            case "android/os/Build->DEVICE:Ljava/lang/String;":{
                return new StringObject(vm, "polaris");
            }
            case "android/os/Build->HARDWARE:Ljava/lang/String;":{
                return new StringObject(vm, "qcom");
            }
            case "android/os/Build->BRAND:Ljava/lang/String;":{
                return new StringObject(vm, "Xiaomi");
            }
            case "android/os/Build->MODEL:Ljava/lang/String;":{
                return new StringObject(vm, "MIX 2S");
            }
            case "cn/tongdun/android/shell/common/CollectorError$TYPE->ERROR_PROFILE_DELAY:Lcn/tongdun/android/shell/common/CollectorError$TYPE;":{
                return dvmClass.newObject(signature);
            }
            case "cn/tongdun/android/shell/common/CollectorError$TYPE->ERROR_JNI:Lcn/tongdun/android/shell/common/CollectorError$TYPE;":{
                return dvmClass.newObject(signature);
            }
        }
        return super.getStaticObjectField(vm, dvmClass, signature);
    }

    @Override
    public void callStaticVoidMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature){
            case "cn/tongdun/android/shell/common/D->a(Landroid/content/Context;)V":{
                return;
            }
            case "cn/tongdun/android/shell/common/CollectorError->addError(Lcn/tongdun/android/shell/common/CollectorError$TYPE;Ljava/lang/String;)V":{
                return;
            }
            case "cn/tongdun/android/shell/common/CollectorError->remove(Lcn/tongdun/android/shell/common/CollectorError$TYPE;)V":{
                return;
            }
            case "cn/tongdun/android/shell/FMAgent->afterLoad()V":{
                return;
            }
        }
        super.callStaticVoidMethod(vm, dvmClass, signature, varArg);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            // 取fm_shared表
            // App重装或清除数据后，重新获取的TD_COOKIEID和td-client-id-3会改变
            // 里面存的可以称之为某种指纹
            // 会访问/data/user/0/com.dhgate.buyermob/files/.td-3 这个或许也是设备指纹，保存在APP数据目录的文件内
            // 还会试图访问 /storage/emulated/0/.td-3 即保存到外部存储中的指纹
            // 最后还会试图访问外部存储中的app文件缓存区
            // /storage/emulated/0/Android/data/com.example.hookinunidbg/files/.td-3
            // 如下是一个正常的fm_shared，我这里故意让它取不到。
            // polaris:/data/data/com.dhgate.buyermob/shared_prefs # cat fm_shared.xml
            //<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
            //<map>
            //    <int name="reqc" value="2" />
            //    <string name="TD_COOKIEID">IlWwB+U3SOs8zacY+I3+S6oWQZwxHuqPXFssU53SOid4f86whZ/cA69KEo3Aqn2YnKe+ZwInKYfQS6Z7CpEUoQ==</string>
            //    <int name="rspc" value="2" />
            //    <string name="td-client-id-3">48ea4fca82ca4450c59b3412092c5e3dfd6f385cd01bc6efc3f095b3de38c4285a846625cde4c5617e2720866b0b0d76</string>
            //</map>
            case "android/content/Context->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;": {
                return null;
            }

            // 怎么把文件和后续的path对应上，两个比较常见的办法
            // 1是通过signature
            // 2是通过emulator get/set
            // 其他办法不是那么好，比如newObject里传真实file，逻辑复杂的话，处理很麻烦
            case "android/content/Context->getFilesDir()Ljava/io/File;": {
                return vm.resolveClass("java/io/File").newObject(signature);
            }
            case "java/io/File->getAbsolutePath()Ljava/lang/String;": {
                String tag = dvmObject.getValue().toString();
                switch (tag) {
                    // 对于这种官方API，不管是JNItrace看，还是查官方文档，或是写Demo测试，以及Frida Hook 目标函数，又或者Hook Native JNI函数返回值，都是可行的。
                    case "android/content/Context->getFilesDir()Ljava/io/File;": {
                        return new StringObject(vm, "/data/user/0/com.dhgate.buyermob/files");
                    }
                    case "android/os/Environment->getExternalStorageDirectory()Ljava/io/File;": {
                        return new StringObject(vm, "/storage/emulated/0");
                    }
                }
            }
            // 获取app存在外部SD卡上的缓存目录
            // https://blog.csdn.net/zhaoyanjun6/article/details/72283289
            case "android/content/Context->getExternalFilesDir(Ljava/lang/String;)Ljava/io/File;": {
                return vm.resolveClass("java/io/File").newObject(signature);
            }

            case "java/io/File->toString()Ljava/lang/String;": {
                String tag = dvmObject.getValue().toString();
                switch (tag) {
                    case "android/content/Context->getExternalFilesDir(Ljava/lang/String;)Ljava/io/File;": {
                        return new StringObject(vm, "/storage/emulated/0/Android/data/com.example.hookinunidbg/files");
                    }
                }
            }
            case "java/util/UUID->toString()Ljava/lang/String;": {
                UUID uuid = (UUID) dvmObject.getValue();
                return new StringObject(vm, uuid.toString());
            }
            // 通过JNI函数检查本地的无障碍服务AccessibilityService，主要指无障碍服务列表，其中的包名、标签名等。收集这些无障碍服务的信息，
            // 和自己的黑白名单进行比对，是否有用于外挂功能的无障碍服务。
            // https://lizhaoxuan.github.io/2018/01/27/AccessibilityService分析与防御/
            case "android/view/accessibility/AccessibilityManager->getInstalledAccessibilityServiceList()Ljava/util/List;": {
                List<DvmObject<?>> AccessibilityServiceInfoList = new ArrayList<>();
                String ServiceInfoClazz = "android/accessibilityservice/AccessibilityServiceInfo";
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.android.settings", "com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService", "无障碍功能菜单", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.google.android.marvin.talkback", "com.google.android.marvin.talkback.TalkBackService", "TalkBack", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.google.android.marvin.talkback", "com.google.android.accessibility.accessibilitymenu.AccessibilityMenuService", "无障碍功能菜单", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.google.android.marvin.talkback", "com.google.android.accessibility.selecttospeak.SelectToSpeakService", "随选朗读", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.google.android.marvin.talkback", "com.android.switchaccess.SwitchAccessService", "开关控制", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.miui.newhome", "com.miui.voicesdk.VoiceAccessibilityService", "内容中心", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.miui.personalassistant", "com.miui.voicesdk.VoiceAccessibilityService", "信息助手", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.miui.securitycenter", "com.miui.gamebooster.gbservices.AntiMsgAccessibilityService", "游戏加速", new String[]{"com.tencent.mobileqq", "com.tencent.mm"})));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.miui.securitycenter", "com.miui.luckymoney.service.LuckyMoneyAccessibilityService", "手机管家", new String[]{"com.tencent.mobileqq", "com.tencent.mm"})));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.miui.voiceassist", "com.miui.voiceassist.accessibility.VoiceAccessibilityService", "小爱同学", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.sohu.inputmethod.sogou.xiaomi", "com.sohu.inputmethod.flx.quicktype.QuickAccessibilityService", "搜狗输入法小米版", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.xiaomi.gamecenter.sdk.service", "com.xiaomi.gamecenter.sdk.ui.mifloat.process.DetectService", "游戏服务", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.xiaomi.misettings", "com.xiaomi.misettings.usagestats.focusmode.service.LRAccessibilityService", "小米设置", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.iflytek.inputmethod.miui", "com.iflytek.libaccessibility.mi.FlyIMEAccessibilityService", "讯飞输入法小米版", new String[]{"com.tencent.mobileqq", "com.tencent.mm"})));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.migu.music.mini", "com.analysys.track.service.AnalysysAccessibilityService", "咪咕音乐极速版", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.netease.edu.ucmooc", "com.edu.app.library.WindowChangeDetectingService", "中国大学MOOC", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.plan.kot32.tomatotime", "com.plan.kot32.tomatotime.remind.keeprunning.MyAccessibilityService", "番茄ToDo-强制防卸载（点击此处重启服务）", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.ss.android.ugc.aweme", "com.ss.android.ugc.aweme.live.livehostimpl.AudioAccessibilityService", "抖音", null)));
                AccessibilityServiceInfoList.add(vm.resolveClass(ServiceInfoClazz).newObject(new AccessibilityServiceInfo("com.xiaomi.scanner", "com.xiaomi.scanner.qrcodeautoprocessing.MyAccessibilityService", "扫一扫", null)));
                return new ArrayListObject(vm, AccessibilityServiceInfoList);
            }
            case "android/accessibilityservice/AccessibilityServiceInfo->getResolveInfo()Landroid/content/pm/ResolveInfo;": {
                return vm.resolveClass("android/content/pm/ResolveInfo").newObject(dvmObject.getValue());
            }
            case "android/content/pm/ResolveInfo->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;": {
                AccessibilityServiceInfo accessibilityServiceInfo = (AccessibilityServiceInfo) dvmObject.getValue();
                return vm.resolveClass("java/lang/CharSequence").newObject(accessibilityServiceInfo.label);
            }
            case "java/lang/CharSequence->toString()Ljava/lang/String;": {
                return new StringObject(vm, dvmObject.getValue().toString());
            }
            // 收集完无障碍服务的信息后，检测进程java世界是否存在某些class ，检测了xposed/subrate等框架的特征类
            // 这里需要注意的是在Unidbg中如何处理好这个问题
            // 除此之外，如果检测到Xposed类存在，获取sHookedMethodCallbacks，这是一个maps，
            // 里面存储的是xposed hook了哪些方法，样本对比其中是否有同盾关心的五六十个方法（获取设备指纹相关的方法）
            case "dalvik/system/PathClassLoader->loadClass(Ljava/lang/String;)Ljava/lang/Class;": {
                String clazzName = vaList.getObjectArg(0).getValue().toString();
                System.out.println("try load class:" + clazzName);
                switch (clazzName) {
                    case "de/robv/android/xposed/XposedBridge": {
                        emulator.getDalvikVM().throwException(vm.resolveClass("java/lang/NoClassDefFoundError").newObject(clazzName));
                        return null;
                    }
                    default: {
                        emulator.getDalvikVM().throwException(vm.resolveClass("java/lang/NoClassDefFoundError").newObject(clazzName));
                        return null;
                    }
                }
            }
            // 检查设备是否存在某些App，这个api从头用到尾，一开始检测xposed/substrate，后面检测比如易码这样的验证码接受平台
            // 以及模拟器特征app，云测平台特征app，多开助手类app，按键精灵类app，等等等等。
            // 除此之外，也用来检查是否有常用的app，比如微博、淘宝等热门应用，以及这些应用的安装时间等等
            // 禁止app读取应用列表的话就没办法获取app列表了
            // try {
            //     PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            //     return true;
            // } catch (PackageManager.NameNotFoundException e) {
            //     return false;
            // }
            case "android/content/pm/PackageManager->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;": {
                StringObject packageName = vaList.getObjectArg(0);
                System.out.println("check app exist or not:" + packageName.getValue().toString());
                if(packageName.getValue().equals("com.dhgate.buyermob")){
                    int flags = vaList.getIntArg(1);
                    return new PackageInfo(vm, packageName.getValue(), flags);
                }
                emulator.getDalvikVM().throwException(vm.resolveClass("java/lang/NoClassDefFoundError").newObject(packageName));
                return null;
            }
            // PathClassLoader的父加载器(parent)是BootClassLoader
            case "dalvik/system/PathClassLoader->getParent()Ljava/lang/ClassLoader;":{
                return vm.resolveClass("java/lang/BootClassLoader").newObject(signature);
            }
            // 获取packageName信息，后续对比是否有多开助手类app
            // https://blog.csdn.net/qq_32227681/article/details/110563688
            case "android/content/pm/PackageManager->getInstalledPackages(I)Ljava/util/List;":{
                int length = 400;
                List<DvmObject<?>> packagelist = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    packagelist.add(vm.resolveClass("android/content/pm/PackageInfo").newObject(i));
                }
                return new ArrayListObject(vm, packagelist);
            }
            case "org/json/JSONObject->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;":{
                JSONObject jsonObject = (JSONObject) dvmObject.getValue();
                System.out.println(vaList.getObjectArg(1));
                String arg1 = vaList.getObjectArg(0).getValue().toString();
                if(vaList.getObjectArg(1) != null){
                    String arg2 = vaList.getObjectArg(1).getValue().toString();
                    jsonObject.put(arg1, arg2);
                }
                return dvmObject;
            }
            case "org/json/JSONArray->put(Ljava/lang/Object;)Lorg/json/JSONArray;":{
                JSONArray jsonArray = (JSONArray) dvmObject.getValue();
                JSONObject jsonObject = (JSONObject) vaList.getObjectArg(0).getValue();
                jsonArray.put(jsonObject);
                System.out.println("debug");
                return dvmObject;
            }
            case "org/json/JSONArray->toString()Ljava/lang/String;":{
                JSONArray jsonArray = (JSONArray) dvmObject.getValue();
                return new StringObject(vm, jsonArray.toString());
            }
            case "android/content/Context->getContentResolver()Landroid/content/ContentResolver;":{
                return vm.resolveClass("android/content/ContentResolver").newObject(signature);
            }
            // 返回设备的软件版本号
            // 例如：GSM手机的IMEI/SV码，如果软件版本是返回null，如果不可用返回null
            case "android/telephony/TelephonyManager->getDeviceSoftwareVersion()Ljava/lang/String;":{
                return null;
            }
            // 获取sim卡唯一标识
            // https://blog.csdn.net/dong5488/article/details/53022696
            case "android/content/ContentResolver->query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;":{
                return null;
            }
            // 返回唯一的用户ID，例如，GSM电话的IMSI。如果不可用，则返回null。参数代表插槽几
            case "android/telephony/TelephonyManager->getSubscriberId(I)Ljava/lang/String;":{
                return null;
            }
            case "android/telephony/TelephonyManager->getSubscriberId()Ljava/lang/String;":{
                return null;
            }
            // 获取android设备的IMEI号,参数代表插槽几
            case "android/telephony/TelephonyManager->getDeviceId(I)Ljava/lang/String;":{
                return null;
            }
            case "android/telephony/TelephonyManager->getDeviceId()Ljava/lang/String;":{
                return null;
            }
            //获取语音邮件号码
            case "android/telephony/TelephonyManager->getVoiceMailNumber(I)Ljava/lang/String;":{
                return null;
            }
            // 获取ISO标准的国家码，即国际长途区号。
            //   * 注意：仅当用户已在网络注册后有效。
            //   *      在CDMA网络中结果也许不可靠。
            case "com/mediatek/telephony/TelephonyManagerEx->getNetworkCountryIso(I)Ljava/lang/String;":{
                return new StringObject(vm, "cn");
            }
            // 运营商名称,注意：仅当用户已在网络注册时有效,在CDMA网络中结果也许不可靠
            case "com/mediatek/telephony/TelephonyManagerEx->getNetworkOperatorName(I)Ljava/lang/String;":{
                return new StringObject(vm, "");
            }
            //  MCC+MNC(mobile country code +mobile network code)
            //   * 注意：仅当用户已在网络注册时有效。
            //   *    在CDMA网络中结果也许不可靠。
            case "com/mediatek/telephony/TelephonyManagerEx->getNetworkOperator(I)Ljava/lang/String;":{
                return new StringObject(vm, "");
            }
            //   * 服务商名称：
            //   * 例如：中国移动、联通
            //   * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
            case "com/mediatek/telephony/TelephonyManagerEx->getSimOperatorName(I)Ljava/lang/String;":{
                return new StringObject(vm, "");
            }
            // Returns the MEID (Mobile Equipment Identifier). Return null if MEID is not available.
            case "android/telephony/TelephonyManager->getMeid()Ljava/lang/String;":{
                return null;
            }
            case "android/net/wifi/WifiManager->getConnectionInfo()Landroid/net/wifi/WifiInfo;":{
                return vm.resolveClass("android/net/wifi/WifiInfo").newObject(signature);
            }
            case "java/net/NetworkInterface->getHardwareAddress()[B": {
                byte[] result = hexStringToByteArray("F460E296DB64");
                return new ByteArray(vm, result);
            }
            case "android/net/wifi/WifiInfo->getSSID()Ljava/lang/String;":{
                return new StringObject(vm, "<unknown ssid>");
            }
            case "android/net/wifi/WifiInfo->getBSSID()Ljava/lang/String;":{
                return new StringObject(vm, "02:00:00:00:00:00");
            }
            case "android/net/wifi/WifiManager->getDhcpInfo()Landroid/net/DhcpInfo;":{
                return vm.resolveClass("android/net/DhcpInfo").newObject(signature);
            }
            case "java/net/NetworkInterface->getName()Ljava/lang/String;":{
                return new StringObject(vm, dvmObject.getValue().toString());
            }
            case "java/net/NetworkInterface->getInetAddresses()Ljava/util/Enumeration;":{
                List<DvmObject<?>> obj = new ArrayList<>();
                return new Enumeration(vm,  obj);
            }
            case "android/net/ConnectivityManager->getActiveNetworkInfo()Landroid/net/NetworkInfo;":{
                return vm.resolveClass("android/net/NetworkInfo").newObject(signature);
            }
            case "android/net/NetworkInfo->getTypeName()Ljava/lang/String;":{
                return new StringObject(vm,"WIFI");//获取网络状态
            }
            // 注册动态广播，监听电池信息
            case "android/content/Context->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;":{
                return vm.resolveClass("android/content/Intent").newObject(vaList.getObjectArg(1).getValue());
            }
            // 传感器信息
            //      // 获取传感器管理器
            //        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            //
            //        // 获取全部传感器列表
            //        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            case "android/hardware/SensorManager->getSensorList(I)Ljava/util/List;":{
                int length = 10;
                List<DvmObject<?>> SensorList = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    SensorList.add(vm.resolveClass("android/hardware/Sensor").newObject(i));
                }
                return new ArrayListObject(vm, SensorList);
            }
            case "android/view/WindowManager->getDefaultDisplay()Landroid/view/Display;": {
                return vm.resolveClass("android/view/Display").newObject(signature);
            }
            case "android/content/Context->getResources()Landroid/content/res/Resources;":{
                return vm.resolveClass("android/content/res/Resources").newObject(signature);
            }
            case "java/io/File->getPath()Ljava/lang/String;":{
                String tag = dvmObject.getValue().toString();
                switch (tag){
                    // Environment.getDataDirectory().getPath()
                    case "android/os/Environment->getDataDirectory()Ljava/io/File;":{
                        return new StringObject(vm, "/data");
                    }
                }
            }
            case "org/json/JSONObject->put(Ljava/lang/String;I)Lorg/json/JSONObject;":{
                JSONObject jsonObject = (JSONObject) dvmObject.getValue();
                jsonObject.put(vaList.getObjectArg(0).getValue().toString(), vaList.getIntArg(1));
                return dvmObject;
            }
            case "org/json/JSONObject->toString()Ljava/lang/String;":{
                JSONObject jsonObject = (JSONObject) dvmObject.getValue();
                return new StringObject(vm, jsonObject.toString());
            }
            case "android/content/res/Resources->getConfiguration()Landroid/content/res/Configuration;":{
                return vm.resolveClass("android/content/res/Configuration").newObject(signature);
            }
            case "android/content/Context->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":{
                StringObject serviceName = vaList.getObjectArg(0);
                assert serviceName != null;
                return new SystemService(vm, serviceName.getValue());
            }
            // 获取安装的输入法
            case "android/view/inputmethod/InputMethodManager->getInputMethodList()Ljava/util/List;":{
                int length = 2;
                List<DvmObject<?>> InputMethodInfoList = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    InputMethodInfoList.add(vm.resolveClass("android/view/inputmethod/InputMethodInfo").newObject(i));
                }
                return new ArrayListObject(vm, InputMethodInfoList);
            }
            case "android/view/inputmethod/InputMethodInfo->getPackageName()Ljava/lang/String;":{
                Object tag =  dvmObject.getValue();
                switch ((int)tag){
                    case 0:{
                        return new StringObject(vm, "com.sohu.inputmethod.sogou.xiaomi");
                    }
                    case 1:{
                        return new StringObject(vm, "com.iflytek.inputmethod.miui");
                    }
                }
            }
            case "android/content/Context->getApplicationInfo()Landroid/content/pm/ApplicationInfo;":{
                return new ApplicationInfo(vm);
            }
            // 这里为什么用ProxyDvmObject？用dvmclass newObject也行，随便你啦。
            case "java/util/zip/ZipFile->entries()Ljava/util/Enumeration;":{
                ZipFile zipFile = (ZipFile) dvmObject.getValue();
                return ProxyDvmObject.createObject(vm, zipFile.entries());
            }
            case "java/util/zip/ZipFile$ZipEntryIterator->nextElement()Ljava/util/zip/ZipEntry;":{
                java.util.Enumeration<?> zipentryiterator = (java.util.Enumeration<?>) dvmObject.getValue();
                return ProxyDvmObject.createObject(vm,zipentryiterator.nextElement());
            }
            case "java/util/zip/ZipEntry->getName()Ljava/lang/String;":{
                return new StringObject(vm, ((ZipEntry) dvmObject.getValue()).getName());
            }
            case "java/lang/String->toLowerCase()Ljava/lang/String;":{
                return new StringObject(vm, dvmObject.getValue().toString().toLowerCase());
            }
            case "java/util/zip/ZipFile->getInputStream(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;":{
                ZipFile zipFile = (ZipFile) dvmObject.getValue();
                try {
                    return ProxyDvmObject.createObject(vm, zipFile.getInputStream((ZipEntry) vaList.getObjectArg(0).getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case "android/content/Context->getPackageResourcePath()Ljava/lang/String;":{
                return new StringObject(vm, "/data/app/com.dhgate.buyermob-4j4PfNvJ27HiE6sPnXQ3gg==/base.apk");
            }
            // 这里处理大概有问题
            case "android/content/pm/PackageManager->getApplicationLabel(Landroid/content/pm/ApplicationInfo;)Ljava/lang/CharSequence;":{
                return vm.resolveClass("java/lang/CharSequence").newObject("DHgate");
            }

            case "java/util/Hashtable->keySet()Ljava/util/Set;":{
                return ProxyDvmObject.createObject(vm, ((Hashtable) dvmObject.getValue()).keySet());
            }
            case "java/util/Collections$SynchronizedSet->toArray()[Ljava/lang/Object;":{
                return ProxyDvmObject.createObject(vm, ((Set) dvmObject.getValue()).toArray());
            }
            // try to get google 广告ID
            // https://stackoverflow.com/questions/27632736/how-to-get-advertising-id-using-adb
            // https://www.jianshu.com/p/33cfb817a077
            case "com/google/android/gms/ads/identifier/AdvertisingIdClient$Info->getId()Ljava/lang/String;":{
                return null;
            }
            case "java/security/KeyFactory->generatePrivate(Ljava/security/spec/KeySpec;)Ljava/security/PrivateKey;":{
                KeyFactory keyFactory = (KeyFactory) dvmObject.getValue();
                try {
                    return vm.resolveClass("java/security/PrivateKey").newObject(keyFactory.generatePrivate((KeySpec) vaList.getObjectArg(0).getValue()));
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
            case "javax/crypto/Cipher->doFinal([B)[B":{
                Cipher cipher = (Cipher) dvmObject.getValue();
                try {
                    return new ByteArray(vm, cipher.doFinal((byte[]) vaList.getObjectArg(0).getValue()));
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    return new ByteArray(vm, new byte[]{0});
                }
            }

            case "org/json/JSONObject->optString(Ljava/lang/String;)Ljava/lang/String;":{
                JSONObject jsonObject = (JSONObject) dvmObject.getValue();
                return new StringObject(vm, jsonObject.optString(vaList.getObjectArg(0).getValue().toString()));
            }

        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            // 想要读取或者向SD卡写入，这时就必须先要判断一个SD卡的状态,mounted为可读写、可创建目录和文件
            // ref https://blog.csdn.net/YUZHIBOYI/article/details/8645730
            case "android/os/Environment->getExternalStorageState()Ljava/lang/String;":{
                return new StringObject(vm, "mounted");
            }
            case "android/os/Environment->getExternalStorageDirectory()Ljava/io/File;":{
                return vm.resolveClass("java/io/File").newObject(signature);
            }
            case "java/util/UUID->randomUUID()Ljava/util/UUID;":{
                return dvmClass.newObject(UUID.randomUUID());
            }
            case "java/lang/ClassLoader->getSystemClassLoader()Ljava/lang/ClassLoader;":{
                return new ClassLoader(vm, signature);
            }
            case "java/lang/Class->forName(Ljava/lang/String;)Ljava/lang/Class;":{
                String args0 = vaList.getObjectArg(0).getValue().toString();
                System.out.println("className:"+args0);
                return vm.resolveClass(args0);
            }
            case "android/bluetooth/BluetoothAdapter->getDefaultAdapter()Landroid/bluetooth/BluetoothAdapter;":{
                return dvmClass.newObject(signature);
            }
            case "android/telephony/SubscriptionManager->from(Landroid/content/Context;)Landroid/telephony/SubscriptionManager;":{
                return dvmClass.newObject(signature);
            }
            case "android/net/Uri->parse(Ljava/lang/String;)Landroid/net/Uri;":{
                String key = vaList.getObjectArg(0).getValue().toString();
                if(key.equals("content://telephony/siminfo")){
                    return dvmClass.newObject(key);
                }
            }
            case "java/net/NetworkInterface->getByName(Ljava/lang/String;)Ljava/net/NetworkInterface;":{
                return dvmClass.newObject(signature);
            }
            // 检测代理
            case "java/lang/System->getProperty(Ljava/lang/String;)Ljava/lang/String;": {
                String key = vaList.getObjectArg(0).getValue().toString();
                System.out.println(key);
                switch (key) {
                    case "http.proxyHost": {
                        return null;
                    }
                    case "http.proxyPort": {
                        return null;
                    }
                }
            }
            case "java/net/NetworkInterface->getNetworkInterfaces()Ljava/util/Enumeration;":{
                // 真实情况这个数组要长很多
                String[] NetworkInterfaceNameList = new String[]{"dummy0","r_rmnet_data2","r_rmnet_data3","ip_vti0","wlan0","wlan1"};
                int length = NetworkInterfaceNameList.length;
                List<DvmObject<?>> NetworkInterfacelist = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    NetworkInterfacelist.add(vm.resolveClass("java/net/NetworkInterface").newObject(NetworkInterfaceNameList[i]));
                }
                return new Enumeration(vm,  NetworkInterfacelist);
            }
            case "java/util/Collections->list(Ljava/util/Enumeration;)Ljava/util/ArrayList;":
                return new ArrayListObject(vm, (List<? extends DvmObject<?>>) vaList.getObjectArg(0).getValue());

            // 用adb shell 看很方便
            case "android/provider/Settings$Secure->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;":{
                String tag = vaList.getObjectArg(1).getValue().toString();
                System.out.println("Settings$Secure->getString:"+tag);
                switch (tag){
                    // 获取蓝牙mac地址
                    // 无需权限，在一些低版本系统中可用
                    case "bluetooth_address":{
                        return null;
                    }
                    // Android ID目前是Android系统提供给应用容易访问的设备ID，也叫SSAID（Settings.Secure.ANDROID_ID缩写），这个ID主要与应用/设备相关
                    // https://www.jianshu.com/p/076fef09c399
                    // String secureId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    case "android_id":{
                        return new StringObject(vm, "adbc76a04c12b712");
                    }
                    // http://www.ccbu.cc/index.php/framework/modify-default-inputmethod.html
                    case "default_input_method":{
                        return new StringObject(vm, "com.android.inputmethod.pinyin/.InputService");
                    }
                }
            }
            case "android/os/Environment->getDataDirectory()Ljava/io/File;":{
                return vm.resolveClass("java/io/File").newObject(signature);
            }

            case "com/google/android/gms/ads/identifier/AdvertisingIdClient->getAdvertisingIdInfo(Landroid/content/Context;)Lcom/google/android/gms/ads/identifier/AdvertisingIdClient$Info;":{
                return vm.resolveClass("com/google/android/gms/ads/identifier/AdvertisingIdClient$Info").newObject(signature);
            }

            case "cn/tongdun/android/shell/common/CollectorError->getErrorMsg()Ljava/lang/String;":{
                return new StringObject(vm, "");
            }

            //    public static final int DEFAULT = 0;
            //    public static final int NO_PADDING = 1;
            //    public static final int NO_WRAP = 2;
            //    public static final int CRLF = 4;
            //    public static final int URL_SAFE = 8;
            //    public static final int NO_CLOSE = 16;
            case "android/util/Base64->decode(Ljava/lang/String;I)[B":{
                if(vaList.getIntArg(1) == 0){
                    return new ByteArray(vm, Base64.decodeBase64(vaList.getObjectArg(0).getValue().toString()));
                }
            }

            case "java/security/KeyFactory->getInstance(Ljava/lang/String;)Ljava/security/KeyFactory;":{
                try {
                    return dvmClass.newObject(KeyFactory.getInstance(vaList.getObjectArg(0).getValue().toString()));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            case "javax/crypto/Cipher->getInstance(Ljava/lang/String;)Ljavax/crypto/Cipher;":{
                try {
                    return dvmClass.newObject(Cipher.getInstance(vaList.getObjectArg(0).getValue().toString()));
                } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                    e.printStackTrace();
                }
            }

        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature){
            case "android/content/Context->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":{
                StringObject serviceName = varArg.getObjectArg(0);
                assert serviceName != null;
                if(serviceName.getValue().equals("window")){
                    return vm.resolveClass("android/view/WindowManager").newObject(signature);
                }
                if(serviceName.getValue().equals("location")){
                    return vm.resolveClass("android/location/LocationManager").newObject(signature);
                }
                if(serviceName.getValue().equals("uimode")){
                    return vm.resolveClass("android/app/UiModeManager").newObject(signature);
                }
                return new SystemService(vm, serviceName.getValue());
            }
            case "java/lang/Class->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;":{
                String methodName = varArg.getObjectArg(0).getValue().toString();
                System.out.println("getDeclaredMethod:"+methodName);
                return vm.resolveClass("java/lang/reflect/Method").newObject(methodName);
            }
            // 换一个类加载器，再次尝试加载de.robv.android.xposed.XposedBridge类。如果这次检测到，那么访问XposedBridge类中
            // disableHooks字段，它用于标记对于当前应用是否要进行hook。样本将其设置为true使得Xposed在当前应用失效。
            case "dalvik/system/PathClassLoader->loadClass(Ljava/lang/String;)Ljava/lang/Class;": {
                String clazzName = varArg.getObjectArg(0).getValue().toString();
                System.out.println("try load class:" + clazzName);
                switch (clazzName){
                    case "de.robv.android.xposed.XposedBridge":{
                        emulator.getDalvikVM().throwException(vm.resolveClass("java/lang/NoClassDefFoundError").newObject(clazzName));
                        return null;
                    }
                    default:{
                        emulator.getDalvikVM().throwException(vm.resolveClass("java/lang/NoClassDefFoundError").newObject(clazzName));
                        return null;
                    }
                }
            }
        }
        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            case "android/accessibilityservice/AccessibilityServiceInfo->packageNames:[Ljava/lang/String;":{
                AccessibilityServiceInfo accessibilityServiceInfo = (AccessibilityServiceInfo) dvmObject.getValue();
                String[] packageNames = accessibilityServiceInfo.packageNames;
                if(packageNames == null){
                    return null;
                }else {
                    DvmObject[] packageObjects = new DvmObject[packageNames.length];
                    for (int i = 0; i < packageNames.length; i++) {
                        packageObjects[i] = new StringObject(vm, packageNames[i]);
                    }
                    return new ArrayObject(packageObjects);
                }
            }
            case "android/content/pm/ResolveInfo->serviceInfo:Landroid/content/pm/ServiceInfo;":{
                return vm.resolveClass("android/content/pm/ServiceInfo").newObject(dvmObject.getValue());
            }
            case "android/content/pm/ServiceInfo->packageName:Ljava/lang/String;":{
                AccessibilityServiceInfo accessibilityServiceInfo = (AccessibilityServiceInfo) dvmObject.getValue();
                return new StringObject(vm, accessibilityServiceInfo.packageName);
            }
            case "android/content/pm/ServiceInfo->name:Ljava/lang/String;":{
                AccessibilityServiceInfo accessibilityServiceInfo = (AccessibilityServiceInfo) dvmObject.getValue();
                return new StringObject(vm, accessibilityServiceInfo.name);
            }
            // 检测安装软件列表中是否有包名为 com.bly.dkplat的app，这是一款叫多开分身的知名软件，即此处在检测多开。
            // 除此之外还有dkmodel、dkplugin等
            case "android/content/pm/PackageInfo->packageName:Ljava/lang/String;":{
                return new StringObject(vm, "test.apk");
            }

            case "android/content/pm/PackageInfo->applicationInfo:Landroid/content/pm/ApplicationInfo;":{
                return new ApplicationInfo(vm);
            }
            // 这边处理的不太好，应该根据每个app返回对应的值，但懒得做了，这个环境补的我都倦了
            case "android/content/pm/ApplicationInfo->packageName:Ljava/lang/String;":{
                return new StringObject(vm, "test.apk");
            }
        }
        return super.getObjectField(vm, dvmObject, signature);
    }

    @Override
    public int callIntMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            // 检测样本用于获取设备信息的几十个JAVA API是否被Hook
            // 原理在于，一部分JAVA Hook框架的原理（比如Xposed/Frida）是将JAVA方法变成Native方法
            // 值得一提的是，一般情况下，代码会通过Modifier.isNative()检测目标方法是否是Native方法
            // 但可以Hook Modifier.isNative()进行反制
            // 而Modifier.isNative() 方法的自实现很简单
            // public static boolean isNative(int mod) {
            //    return (mod & 0x100) != 0;
            // }
            // 所以样本自实现了这个API，并且检测Modifier.isNative()是否被Hook
            // 很有趣呢
            // https://cloud.tencent.com/developer/article/1604715
            case "java/lang/reflect/Method->getModifiers()I":{
                return 1;
            }
            case "android/content/Context->checkCallingOrSelfPermission(Ljava/lang/String;)I": {
                String key = vaList.getObjectArg(0).getValue().toString();
                System.out.println("Context->checkCallingOrSelfPermission:" + key);
                switch (key) {
                    case "android.permission.READ_PHONE_STATE": {
                        return 0;
                    }
                    case "android.permission.READ_EXTERNAL_STORAG": {
                        return -1;
                    }
                    case "android.permission.ACCESS_FINE_LOCATION": {
                        return -1;
                    }
                    case "android.permission.READ_EXTERNAL_STORAGE": {
                        return -1;
                    }
                    case "android.permission.WRITE_EXTERNAL_STORAGE": {
                        return -1;
                    }
                    case "android.permission.INTERNET": {
                        return 0;
                    }
                    case "android.permission.ACCESS_WIFI_STATE": {
                        return 0;
                    }
                    case "android.permission.INSTALL_PACKAGES": {
                        return -1;
                    }
                    case "android.permission.BLUETOOTH": {
                        return -1;
                    }
                    case "android.permission.ACCESS_COARSE_LOCATION": {
                        return -1;
                    }
                }
            }
            // api 30中弃用
            // 返回可用电话数。如果不支持语音、短信和数据，则返回0。对于单待机模式（单SIM卡功能），则返回1。双待机模式（双SIM卡功能）返回2。
            // 对于三卡待机模式（三卡功能），返回3。
            case "android/telephony/TelephonyManager->getPhoneCount()I":{
                return 2;
            }
            // 获取当前sim卡数量
            // SubscriptionManager mSubscriptionManager = SubscriptionManager.from(mContext);
            // int simNumberCard = mSubscriptionManager.getActiveSubscriptionInfoCount()；
            case "android/telephony/SubscriptionManager->getActiveSubscriptionInfoCount()I":{
                return 0;
            }
            // 返回一个常量，指示插槽 i 中设备SIM卡的状态
            // 我的测试机上，两个卡槽都没装卡，对应状态SIM_STATE_ABSENT，值为1
            case "android/telephony/TelephonyManager->getSimState(I)I":{
                return 1;
            }
            // 返回指示设备电话类型的常量
            // 1是GSM
            case "com/mediatek/telephony/TelephonyManagerEx->getPhoneType(I)I":{
                return 1;
            }
            /**
             * 当前使用的网络类型：
             * 例如：NETWORK_TYPE_UNKNOWN  网络类型未知  0
             NETWORK_TYPE_GPRS    GPRS网络  1
             NETWORK_TYPE_EDGE    EDGE网络  2
             NETWORK_TYPE_UMTS    UMTS网络  3
             NETWORK_TYPE_HSDPA    HSDPA网络  8
             NETWORK_TYPE_HSUPA    HSUPA网络  9
             NETWORK_TYPE_HSPA    HSPA网络  10
             NETWORK_TYPE_CDMA    CDMA网络,IS95A 或 IS95B.  4
             NETWORK_TYPE_EVDO_0   EVDO网络, revision 0.  5
             NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6
             NETWORK_TYPE_1xRTT   1xRTT网络  7
             */
            case "com/mediatek/telephony/TelephonyManagerEx->getNetworkType(I)I":{
                return 0;
            }
            case "android/net/wifi/WifiInfo->getIpAddress()I":{
                return 100772032;
            }
            case "android/net/NetworkInfo->getType()I":{
                return 1;
            }
            // https://www.cnblogs.com/zjqlogs/p/5488798.html
            // 通过广播监听电池信息
            // ADB可以方便的查看这些信息
            // $ adb shell dumpsys battery
            //Current Battery Service state:
            //  AC powered: false　　　　　　　　//false表示没使用AC电源
            //  USB powered: true　　　　　　　　//true表示使用USB电源
            //  Wireless powered: false　　　　 //false表示没使用无线电源
            //  status: 2　　　　　　　　　　　　　//2表示电池正在充电，1表示没充电
            //  health: 2　　　　　　　　　　　　　//2表示电池状态优秀
            //  present: true　　　　　　　　　　 //true表示已安装电池
            //  level: 63　　　　　　　　　　　　　//电池百分比
            //  scale: 100　　　　　　　　　　　　　//满电量时电池百分比为100%（不确定是否正确）
            //  voltage: 3781　　　　　　　　　　　//电池电压3.781V
            //  temperature: 250　　　　　　　　　//电池温度为25摄氏度
            //  technology: Li-ion　　　　　　　　//电池类型为锂电池
            case "android/content/Intent->getIntExtra(Ljava/lang/String;I)I":{
                String tag = vaList.getObjectArg(0).getValue().toString();
                switch (tag){
                    case "status":{
                        return 2;
                    }
                    case "level":{
                        return 63;
                    }
                    case "temperature":{
                        return 250;
                    }
                    // 充电方式
                    case "plugged":{
                        return 0;
                    }
                }

                return 1;
            }
            case "android/view/Display->getHeight()I":{
                return 2160;
            }
            case "android/view/Display->getWidth()I":{
                return 1080;
            }
            case "org/json/JSONObject->length()I":{
                JSONObject jsonObject = (JSONObject) dvmObject.getValue();
                return jsonObject.length();
            }
            case "android/hardware/Sensor->getType()I":{
               // return (int) dvmObject.getValue(); //todo 我注释了
            }
            case "java/util/zip/ZipFile$ZipFileInflaterInputStream->read([B)I":{
                InflaterInputStream inflaterInputStream = (InflaterInputStream) dvmObject.getValue();
                try {
                    return inflaterInputStream.read((byte[]) vaList.getObjectArg(0).getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case "android/content/SharedPreferences->getInt(Ljava/lang/String;I)I":{
                String tag = vaList.getObjectArg(0).getValue().toString();
                System.out.println("SharedPreferences->getInt:"+tag);
                switch (tag){
                    case "reqc":{
                        return 2;
                    }
                    case "rspc":{
                        return 2;
                    }
                }
                System.out.println("debug");
            }
            //  UiModeManager.getCurrentModeType() 方法来检查该设备在什么模式下运行，比如电视模式、车载模式等等
            //  public static final int UI_MODE_TYPE_NORMAL = 0x01;
            //  public static final int UI_MODE_TYPE_DESK = 0x02;
            //  public static final int UI_MODE_TYPE_CAR = 0x03;
            //  public static final int UI_MODE_TYPE_TELEVISION = 0x04;
            //  public static final int UI_MODE_TYPE_APPLIANCE = 0x05;
            //  public static final int UI_MODE_TYPE_WATCH = 0x06;
            //  public static final int UI_MODE_TYPE_VR_HEADSET = 0x07;
            case "android/app/UiModeManager->getCurrentModeType()I":{
                return 1;
            }
        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature){
            case "java/lang/ClassLoader->getSystemClassLoader()Ljava/lang/ClassLoader;":{
                return new ClassLoader(vm, signature);
            }
            // 这里应该Hook确认一下真实返回值
            case "cn/tongdun/android/shell/common/D->b()Lorg/json/JSONObject;":{
                return ProxyDvmObject.createObject(vm, new JSONObject());
            }

            // todo
            case "cn/tongdun/android/shell/common/HttpHelper->connect(Ljava/net/URL;[BLjava/lang/String;I)Ljava/lang/String;":{
                return new StringObject(vm, "");
            }
        }
        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }

    @Override
    public DvmObject<?> newObject(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature){
            case "java/util/HashMap-><init>()V":{
                return dvmClass.newObject(new HashMap<>());
            }
            case "org/json/JSONObject-><init>()V":{
                return dvmClass.newObject(new JSONObject());
            }
            case "org/json/JSONArray-><init>()V":{
                return dvmClass.newObject(new JSONArray());
            }
        }
        return super.newObject(vm, dvmClass, signature, varArg);
    }

    @Override
    public int callStaticIntMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "android/provider/Settings$Global->getInt(Landroid/content/ContentResolver;Ljava/lang/String;)I":{
                String key = vaList.getObjectArg(1).getValue().toString();
                System.out.println("fuck Settings$Global->getInt: "+key);
                // 检测adb
                if(key.equals("adb_enabled")){
                    return 1;
                }
            }
            case "android/provider/Settings$System->getInt(Landroid/content/ContentResolver;Ljava/lang/String;)I": {
                String tag = vaList.getObjectArg(1).getValue().toString();
                System.out.println("fuck tag:" + tag);
                switch (tag) {
                    // https://developer.android.com/reference/android/provider/Settings.System#SCREEN_BRIGHTNESS
                    case "screen_brightness": {
                        return 45;
                    }
                }
            }
            case "android/provider/Settings$Secure->getInt(Landroid/content/ContentResolver;Ljava/lang/String;)I":{
                String tag = vaList.getObjectArg(1).getValue().toString();
                System.out.println("Secure->getInt:"+tag);
                switch (tag){
                    // 判断手机是否打开了模拟位置，打开为1，不开为0.
                    // boolean isOpen = Settings.Secure.getInt(context.getContentResolver(),Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
                    case "mock_location":{
                        return 0;
                    }
                }
            }
        }
        return super.callStaticIntMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "android/bluetooth/BluetoothAdapter->isEnabled()Z":{
                return false;
            }
            case "android/net/wifi/WifiManager->isWifiEnabled()Z":{
                return true;
            }
            case "java/net/NetworkInterface->isLoopback()Z":{
                return true;
            }
            // 返回设备是否使用PIN，图案或密码进行保护。
            case "android/app/KeyguardManager->isDeviceSecure()Z":{
                return true;
            }
            case "java/util/zip/ZipFile$ZipEntryIterator->hasMoreElements()Z": {
                java.util.Enumeration<?> zipentryiterator = (java.util.Enumeration<?>) dvmObject.getValue();
                return zipentryiterator.hasMoreElements();
            }
            case "java/lang/String->endsWith(Ljava/lang/String;)Z":{
                String tag = vaList.getObjectArg(0).getValue().toString();
                System.out.println("endWith:"+tag);
                return dvmObject.getValue().toString().endsWith(tag);
            }
            // 判断设备是否有特定的功能模块
            // https://developer.android.com/guide/topics/manifest/uses-feature-element?hl=zh-cn
            // https://android.stackexchange.com/questions/82169/howto-get-devices-features-with-adb
            case "android/content/pm/PackageManager->hasSystemFeature(Ljava/lang/String;)Z":{
                String featureName = vaList.getObjectArg(0).getValue().toString();
                System.out.println("PackageManager->hasSystemFeature:"+featureName);
                switch (featureName){
                    // 触摸屏
                    case "android.hardware.touchscreen":{
                        return true;
                    }
                    // 设备可模拟触摸屏（“假触摸”界面）或实际具有触摸屏时
                    case "android.hardware.faketouch":{
                        return true;
                    }
                    // 电话功能
                    case "android.hardware.telephony":{
                        return true;
                    }
                    // 相机
                    case "android.hardware.camera":{
                        return true;
                    }
                    // NFC
                    case "android.hardware.nfc":{
                        return true;
                    }
                    // GPS
                    case "android.hardware.location.gps":{
                        return true;
                    }
                    // 麦克风
                    case "android.hardware.microphone":{
                        return true;
                    }
                    // 传感器
                    case "android.hardware.sensor":{
                        return true;
                    }
                    // 纵向屏幕
                    case "android.hardware.screen.portrait":{
                        return true;
                    }
                }
            }
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public boolean callBooleanMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature){
            case "android/net/ConnectivityManager->getMobileDataEnabled()Z":{
                return true;
            }
        }
        return super.callBooleanMethod(vm, dvmObject, signature, varArg);
    }

    @Override
    public DvmObject<?> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "org/json/JSONObject-><init>(Ljava/util/Map;)V":{
                System.out.println("debug");
                return dvmClass.newObject(new JSONObject(vaList.getObjectArg(0).getValue()));
            }
            case "com/mediatek/telephony/TelephonyManagerEx-><init>(Landroid/content/Context;)V":{
                return dvmClass.newObject(signature);
            }
            case "android/content/IntentFilter-><init>(Ljava/lang/String;)V":{
                System.out.println("IntentFilter:"+vaList.getObjectArg(0).getValue().toString());
                return dvmClass.newObject(vaList.getObjectArg(0).getValue());
            }
            case "android/graphics/Point-><init>()V":{
                return dvmClass.newObject(signature);
            }
            case "java/util/zip/ZipFile-><init>(Ljava/lang/String;)V":{
                String apkName = vaList.getObjectArg(0).getValue().toString();
                try {
                    return dvmClass.newObject(new ZipFile("unidbg-android/src/test/resources/tongdun/runtime/dhgate.apk"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case "java/util/Hashtable-><init>()V":{
                return dvmClass.newObject(new Hashtable<>());
            }

            case "java/util/UUID-><init>(JJ)V":{
                return dvmClass.newObject(new UUID(vaList.getLongArg(0), vaList.getLongArg(1)));
            }
            case "android/media/MediaDrm-><init>(Ljava/util/UUID;)V":{
                return dvmClass.newObject(signature);
            }

            case "java/security/spec/PKCS8EncodedKeySpec-><init>([B)V":{
                return dvmClass.newObject(new PKCS8EncodedKeySpec((byte[]) vaList.getObjectArg(0).getValue()));
            }

            case "java/net/URL-><init>(Ljava/lang/String;)V":{
                try {
                    return dvmClass.newObject(new URL(vaList.getObjectArg(0).getValue().toString()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            case "org/json/JSONObject-><init>(Ljava/lang/String;)V":{
                return dvmClass.newObject(new JSONObject());
            }
        }
        return super.newObjectV(vm, dvmClass, signature, vaList);
    }

    /* s must be an even-length string. */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Override
    public int getIntField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            case "android/net/DhcpInfo->gateway:I":{
                return 16885952;
            }
            case "android/net/DhcpInfo->netmask:I":{
                return 16777215;
            }
            case "android/net/DhcpInfo->dns1:I":{
                return 1920103026;
            }
            case "android/net/DhcpInfo->dns2:I":{
                return 84215263;
            }
            case "android/graphics/Point->y:I":{
                return 2160;
            }
            // The kind of keyboard attached to the device. One of: KEYBOARD_NOKEYS, KEYBOARD_QWERTY, KEYBOARD_12KEY.
            case "android/content/res/Configuration->keyboard:I":{
                return 1;
            }
            case "android/content/res/Configuration->hardKeyboardHidden:I":{
                return 1;
            }
            case "android/content/res/Configuration->keyboardHidden:I":{
                return 1;
            }
            // 检查当前app是否是debug状态
            case "android/content/pm/ApplicationInfo->flags:I":{
                return 951598660;
            }

            // 获取设备旋转方向，横屏还是竖屏
            // https://benzblog.site/2017-06-19-all-about-rotations/
            case "android/content/res/Configuration->orientation:I":{
                return 2;
            }
        }
        return super.getIntField(vm, dvmObject, signature);
    }

    // 这里直接copy了 wl同学的代码
    @Override
    public long callStaticLongMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "android/net/TrafficStats->getMobileRxBytes()J":
                /** 获取手机通过 2G/3G 接收的字节流量总数 */
                return 819194;
            case "android/net/TrafficStats->getMobileTxBytes()J":
                /** 获取手机通过 2G/3G 发出的字节流量总数 */
                return 541445;
            case "android/net/TrafficStats->getTotalTxBytes()J":
                /** 获取手机通过所有网络方式发送的字节流量总数(包括 wifi) */
                return 645004;
            case "android/net/TrafficStats->getTotalRxBytes()J":
                /** 获取手机通过所有网络方式接收的字节流量总数(包括 wifi) */
                return 1019194;
            case "android/os/SystemClock->elapsedRealtime()J":
                /*自开机后，经过的时间，包括深度休眠的时间*/
                return 89866853;
            case "android/os/SystemClock->uptimeMillis()J":
                /* 自开机后，经过的时间，不包括深度休眠的时间*/
                return 59816853;
        }
        return super.callStaticLongMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            // 获取屏幕宽高
            // https://blog.csdn.net/joye123/article/details/80675328
            case "android/view/Display->getSize(Landroid/graphics/Point;)V":{
                return;
            }
            case "android/view/Display->getRealSize(Landroid/graphics/Point;)V":{
                return;
            }
            case "javax/crypto/Cipher->init(ILjava/security/Key;)V":{
                Cipher cipher = (Cipher) dvmObject.getValue();
                try {
                    cipher.init(vaList.getIntArg(0), (Key) vaList.getObjectArg(1).getValue());
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public boolean callStaticBooleanMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            // 系统自带的检测debug
            case "android/os/Debug->isDebuggerConnected()Z":{
                return false;
            }
            // isCryptoSchemeSupported(UUID)方法能够用于查询一个给定的DRM方案是否被设备所支持。
            case "android/media/MediaDrm->isCryptoSchemeSupported(Ljava/util/UUID;)Z":{
                return false;
            }
        }
        return super.callStaticBooleanMethodV(vm, dvmClass, signature, vaList);
    }

    // 获取每个app安装时间、更新时间，这里偷懒了
    @Override
    public long getLongField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            case "android/content/pm/PackageInfo->firstInstallTime:J":{
                return 1628930489657L;
            }
            case "android/content/pm/PackageInfo->lastUpdateTime:J":{
                return 1628930489657L;
            }
        }
        return super.getLongField(vm, dvmObject, signature);
    }

    @Override
    public void setStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature, DvmObject<?> value) {
        switch (signature){
            case "cn/tongdun/android/shell/FMAgent->mStatus:Ljava/lang/String;":{
                return;
            }
        }
        super.setStaticObjectField(vm, dvmClass, signature, value);
    }

}
