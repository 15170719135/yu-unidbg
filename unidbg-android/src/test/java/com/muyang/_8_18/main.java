package com.muyang._8_18;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.AndroidElfLoader;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.MemoryBlock;
import com.github.unidbg.pointer.UnidbgPointer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

    public main() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .build();

        memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/muyang/_8_18/demo.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dalvikModule = vm.loadLibrary(new File("unidbg-android/src/test/java/com/muyang/_8_18/libcheck.so"), true);
        module = dalvikModule.getModule();
        vm.callJNI_OnLoad(emulator, module);
//        emulator.attach().addBreakPoint(module, 0XB1A);
    }

    static {
        Logger.getLogger(AndroidElfLoader.class).setLevel(Level.INFO);
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        main mainActivity = new main();
        System.out.println("load the moudle" + (System.currentTimeMillis() - start) + "ms");

        // 研究 public native check (String str1, String str2) ,  地址 87FC
        // 从 JNI_OnLoad 方法看
        mainActivity.call_sub85E0();
    }

    //85E0
    private void call_sub85E0() {
//        emulator.traceCode();
        List<Object> args = new ArrayList<>();
        UnidbgPointer ptr_arg0 = UnidbgPointer.pointer(emulator,module.base+0XF1B0);
        args.add(ptr_arg0); // 第一个参数 (so 文件写死的 , 不知道是什么)
        args.add(622);

        MemoryBlock malloc = memory.malloc(32,true);
        UnidbgPointer pointer_md5 = malloc.getPointer();
        String md5_value = "f8c49056e4ccf9a11e090eaf471f418d";
        pointer_md5.write(md5_value.getBytes(StandardCharsets.UTF_8)); //往指针写东西
        args.add(pointer_md5.toIntPeer());

        Number number = module.callFunction(emulator, 0X85E0 + 1, args.toArray());
        System.out.println(number.longValue()); // 得到 方法的地址   ((v8 + 1))  是一个方法

        execcode(number.longValue());
    }

    public void execcode(long addr){
        List<Object> args = new ArrayList<>();

        String arg1 = "qqqqqqq";
        //v9[0]
        MemoryBlock malloc = memory.malloc(arg1.length(), true);
        UnidbgPointer arg1_pointer = malloc.getPointer();

        //v9[1] = pipedes[2]
        UnidbgPointer pipedes_pointer = memory.allocateStack(8);
        pipedes_pointer.setInt(0,0); //没检测 , 啥都行
        pipedes_pointer.setInt(4,1); //

        UnidbgPointer pointer_v9 = memory.allocateStack(8); //v9
        pointer_v9.setPointer(0,arg1_pointer); // 我们传的 qqq
        pointer_v9.setPointer(4,pipedes_pointer);

        args.add(pointer_v9.toIntPeer());
        Number number = module.callFunction(emulator, addr - module.base + 1, args.toArray());
        System.out.println("result---->"+number.longValue());

    }
    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        if(signature.equals("com/a/sample/loopcrypto/Decode->a([BI)Ljava/lang/String;")){ //补方法
            byte[] value = (byte[]) varArg.getObjectArg(0).getValue();
            int i = varArg.getIntArg(1);
            String a = Decode.a(value,i);
            return new StringObject(vm,a);
        }
        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }
}
