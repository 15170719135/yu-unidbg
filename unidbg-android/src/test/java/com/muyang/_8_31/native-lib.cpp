#include <jni.h>
#include <string>
#include <android/log.h>
#include <string>
#include <locale>
#include <codecvt>
#include <iostream>
#include <unistd.h>

#include <sys/types.h>

#include <sys/time.h>

#include <sys/resource.h>

#define   RUSAGE_SELF     0

#define   RUSAGE_CHILDREN     -1

//定义TAG之后，我们可以在LogCat通过TAG过滤出NDK打印的日志
#define TAG "muyang"
// 定义info信息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)


// java中的jstring, 转化为c的一个字符数组
char *Jstring2CStr(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
//获得java.lang.String类的一个实例
    jclass clsstring = (env)->FindClass("java/lang/String");
//指定编码方式
    jstring strencode = (env)->NewStringUTF("utf-8");//utf-16,GB2312
//获得方法 getBytes
    jmethodID mid = (env)->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
//通过回调java中的getBytes方法将字符串jstr转换成uft-8编码的字节数组
    jbyteArray barr = (jbyteArray) (env)->CallObjectMethod(jstr, mid, strencode);
// String .getByte("GB2312");
//获得字节数组的长度
    jsize alen = (env)->GetArrayLength(barr);
//获得字节数组的首地址
    jbyte *ba = (env)->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
//分配内存空间
        rtn = (char *) malloc(alen + 1); //new char[alen+1]; "\0"
//将字符串ba复制到 rtn
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (env)->ReleaseByteArrayElements(barr, ba, 0); //释放内存
    return rtn;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_muyang_lesson31_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    //获取系统属性的第一种
    jclass androidBuildClass = env->FindClass("android/os/Build");
    jfieldID SERIAL = env->GetStaticFieldID(androidBuildClass, "MODEL",
                                            "Ljava/lang/String;");
    jstring serialNum = (jstring) env->GetStaticObjectField(androidBuildClass,
                                                            SERIAL);
    LOGI("第一种方法 :%s", Jstring2CStr(env,serialNum));
    /*
     *     @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "android/os/Build->SERIAL:Ljava/lang/String;":
                // serial 的值
                return new StringObject(vm, "xxxx");
        }
        return super.getStaticObjectField(vm, dvmClass, signature);
    }

     */

    //第二个常见方式是通过 system_property_get 函数获取系统属性也是常见做法
    //https://blog.csdn.net/q610098308/article/details/104812279/
    char *key = "ro.build.id";
    char value[256] = {0};
    __system_property_get(key, value);
    LOGI("第2种方法 :%s", value);

    /*
     *  SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                switch (key){
                    case "ro.build.id":
                        return "12345";
                }
                return "";
            }
        });
        memory.addHookListener(systemPropertyHook);
     */

    //第三种就是文件访问   比如取/proc/pid/maps

    //第四种就是 popen()
    value[256] = {0};
    std::string cmd = "getprop ro.build.id";
    FILE* file = popen(cmd.c_str(), "r");
    fread(value, 256, 1, file);
    pclose(file);
    LOGI("第4种方法 :%s", value);

    //第五种就是我们之前所讲的过getenv


    //第六个常见方式是使用系统调用获取相关属性，不管是通过syscall函数还是内联汇编，都属此类。
    //常见的比如useage系统调用
    //https://chromium.googlesource.com/chromiumos/docs/+/master/constants/syscalls.md#arm-32_bit_EABI
    //https://blog.csdn.net/u011192270/article/details/118155237
    struct rusage useage;
    getrusage(0,&useage);
    LOGI("第6种方法 :%d", useage.ru_utime.tv_sec);

    return env->NewStringUTF(hello.c_str());
}