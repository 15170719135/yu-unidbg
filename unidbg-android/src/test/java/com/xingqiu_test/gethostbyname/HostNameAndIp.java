package com.xingqiu_test.gethostbyname;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.HookStatus;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.hook.HookContext;
import com.github.unidbg.hook.ReplaceCallback;
import com.github.unidbg.hook.hookzz.HookEntryInfo;
import com.github.unidbg.hook.hookzz.HookZz;
import com.github.unidbg.hook.hookzz.IHookZz;
import com.github.unidbg.hook.hookzz.InstrumentCallback;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import unicorn.ArmConst;

import java.io.File;

public class HostNameAndIp{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    public static void main(String[] args) {
        HostNameAndIp demo2 = new HostNameAndIp();
        demo2.ReplaceGetrusage();
        demo2.call();
    }

    HostNameAndIp() {

        // 创建模拟器实例
        emulator = AndroidEmulatorBuilder.for32Bit().build();

        // 模拟器的内存操作接口
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));
        // 创建Android虚拟机
        vm = emulator.createDalvikVM(new File("C:\\D\\YiDong_Pan\\unidbg\\Unidbg补环境进阶三讲\\第三讲\\DemoAPK\\app-debug.apk"));


        // 加载so到虚拟内存
        DalvikModule dm = vm.loadLibrary("hostnameandip", true);
        // 加载好的 libhookinunidbg.so对应为一个模块
        module = dm.getModule();

        // 执行JNIOnLoad（如果有的话）
        dm.callJNI_OnLoad(emulator);

    }


    public void call(){
        DvmClass dvmClass = vm.resolveClass("com/example/hostnameandip/MainActivity");
        String methodSign = "hostnameandip()V";
        DvmObject<?> dvmObject = dvmClass.newObject(null);

        dvmObject.callJniMethodObject(emulator, methodSign);

    }



    // struct hostent *gethostbyname(const char *name);
    /*
    char *h_name 表示的是主机的规范名。例如 [www.google.com ](http://www.google.com/)的规范名其实是 [www.l.google.com ](http://www.l.google.com/)。
    char  h_aliases 表示的是主机的别名。 [www.google.com ](http://www.google.com/)就是google他自己的别名。有的时候，有的主机可能有好几个别名，这些，其实都是为了易于用户记忆而为自己的网站多取的名字。
    int  h_addrtype 表示的是主机ip地址的类型，到底是ipv4(AF_INET)，还是ipv6(AF_INET6)
    int  h_length 表示的是主机ip地址的长度
    int  h_addr_lisst 表示的是主机的ip地址，注意，这个是以网络字节序存储的，通过调用inet_ntop()展示这个IP地址。
    */
    public void ReplaceGetrusage() {
        final HookZz hook = HookZz.getInstance(emulator);
        hook.replace(module.findSymbolByName("gethostbyname"), new ReplaceCallback() {
            @Override
            public HookStatus onCall(Emulator<?> emulator, HookContext context, long originFunction) {
                MemoryBlock memoryBlock = emulator.getMemory().malloc(20, true);
                UnidbgPointer host = memoryBlock.getPointer();

                // h_name  4步
                String h_name = "media-router-fp74.prod.media.vip.tp2.yahoo.com ";
                int h_name_length = h_name.length();
                MemoryBlock h_name_block = emulator.getMemory().malloc(h_name_length+1, true);//获取内存块
                UnidbgPointer h_name_ptr=h_name_block.getPointer();//获取内存块的指针
                h_name_ptr.write(h_name.getBytes()); //往指针里写数据
                host.setPointer(0, h_name_ptr);// 把开始指针跟 数据的指针关联起来

                // h_aliases
                MemoryBlock h_aliases_block = emulator.getMemory().malloc(4, true);
                host.setPointer(4, h_aliases_block.getPointer());

                // h_addrtype
                host.setInt(8, 2);

                // h_length
                host.setInt(12, 4);

                // h_addr_list
                MemoryBlock h_addr_list_block = emulator.getMemory().malloc(4, true);
                host.setPointer(16, h_addr_list_block.getPointer());

                MemoryBlock h_addr_block = emulator.getMemory().malloc(4, true);
                h_addr_block.getPointer().setInt(0, 0xCA66DEB4);
                h_addr_list_block.getPointer().setPointer(0, h_addr_block.getPointer());

                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, host.peer); //把 结果(关联的指针)注册进 寄存器
                return HookStatus.RET(emulator, context.getLR());
            }

        }, false);
    }
}