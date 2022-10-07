package com.xingqiu_test.tongdun;

import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.ModuleListener;
import com.github.unidbg.Symbol;
import com.github.unidbg.arm.backend.Backend;
import com.github.unidbg.arm.backend.CodeHook;
import com.github.unidbg.arm.backend.EventMemHook;
import com.github.unidbg.arm.backend.UnHook;
import com.github.unidbg.arm.backend.unicorn.Unicorn;
import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.linux.android.SystemPropertyHook;
import com.github.unidbg.linux.android.SystemPropertyProvider;
import com.github.unidbg.pointer.UnidbgPointer;
import unicorn.ArmConst;
import unicorn.UnicornConst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class hookImports implements ModuleListener {
    protected final Emulator<?> emulator;
    protected String functionsPath;
    protected String soName;
    protected RegisterContext registerContext;
    protected ArrayList<String> callFunctions = new ArrayList<String>();;

    @Override
    public void onLoaded(Emulator<?> emulator, Module module) {
        // 在目标函数中Hook
        if(module.name.equals(this.soName)){
//            hookAll();
            hookMy();
            patch();
        }
    }

    public hookImports(Emulator<?> emulator, String soName, String pathName){
        this.emulator = emulator;
        this.functionsPath = pathName;
        this.soName = soName;
        this.registerContext = emulator.getContext();
    }

    private void hookMy(){
        Module mymodule = emulator.getMemory().findModule(soName);

        long popenAddress = mymodule.findSymbolByName("popen").getAddress();
        // 函数原型：FILE *popen(const char *command, const char *type);
        emulator.attach().addBreakPoint(popenAddress, new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                String command = registerContext.getPointerArg(0).getString(0);
                emulator.set("command", command);
                return true;
            }
        });


        // 获取uid
        // https://blog.csdn.net/huilin9960/article/details/81530568
        // unidbg输出是0，所以要改
        long getuidAddress = mymodule.findSymbolByName("getuid").getAddress();
        emulator.attach().addBreakPoint(getuidAddress, new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                int uid = 10256;
                System.out.println("getuid:"+uid);
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_PC, registerContext.getLRPointer().peer);
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, uid);
                return true;
            }
        });

        // int strcmp(const char *str1, const char *str2)
        Symbol strcmpSymbol = mymodule.findSymbolByName("strcmp");
        emulator.attach().addBreakPoint(strcmpSymbol.getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                UnidbgPointer str1Address = registerContext.getPointerArg(0);
                UnidbgPointer str2Address = registerContext.getPointerArg(1);
//                System.out.println("call strcmp");
//                System.out.println("str1: "+str1Address.getString(0));
//                System.out.println("str2: "+str2Address.getString(0));
                return true;
            }
        });

        // 这里可能有门道，补不动了，随便弄弄吧
        // int ioctl(int d,int request, ...)
        Symbol ioctlSymbol = mymodule.findSymbolByName("ioctl");
        emulator.attach().addBreakPoint(ioctlSymbol.getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_PC, registerContext.getLRPointer().peer);
                // 顺利则返回0
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, 0);
//                emulator.attach().debug();
                return true;
            }
        });

        // 这里Unidbg还不够完善，建议有志之士直接修复，而不是patch阻止使用，我这里偷懒了嗷
        long bindAddress = mymodule.findSymbolByName("bind").getAddress();
        emulator.attach().addBreakPoint(bindAddress, new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_PC, registerContext.getLRPointer().peer);
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, -1);
                return true;
            }
        });

        // getppid的意图，我没分析
        emulator.attach().addBreakPoint(mymodule.findSymbolByName("getppid").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                return true;
            }
        });

    }

    private void hookAll(){
        List<String[]> functionList = new ArrayList<String[]>();
        try {
            functionList = getFunctionList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (final String[] s :functionList) {
            if(!s[0].equals("Address")){
                Symbol symbol = emulator.getMemory().findModule(soName).findSymbolByName(s[1].split("@")[0]);
                if(symbol != null){
                    emulator.attach().addBreakPoint(symbol.getAddress(), new BreakPointCallback() {
                        @Override
                        public boolean onHit(Emulator<?> emulator, long address) {
                            Module module = emulator.getMemory().findModuleByAddress(registerContext.getLRPointer().peer);
                            if(module != null){
                                if(module.name.equals(soName)){
//                                System.out.println("fuck ori:"+s[1]+" LR:0x"+Long.toHexString(registerContext.getLRPointer().peer-moduleBase));
                                    if(!callFunctions.contains(s[1])){
                                        System.out.println("fuck libc call:"+s[1]);
                                        callFunctions.add(s[1]);
                                    }

                                };
                            }

                            return true;
                        }
                    });
                }

            }
        }

    }


    private List<String[]> getFunctionList() throws IOException {
        File file = new File(this.functionsPath);
        if (!file.exists())
            throw new RuntimeException("Not File!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        List<String[]> functionlist = new ArrayList<>();
        while ((str = br.readLine()) != null) {
            String[] splitStr = str.split("\\t");
            String address = splitStr[0];
            String addrName = splitStr[2];
            functionlist.add(new String[]{address,addrName});
        };
        return functionlist;
    }

    // 大概是多线程造成的问题，随便过一下咯，毕竟只是个demo嘛
    public void patch(){
        emulator.getBackend().hook_add_new(new EventMemHook() {
            @Override
            public boolean hook(Backend backend, long address, int size, long value, Object user, UnmappedType unmappedType) {
                System.out.println("fuck patch ");
                System.out.println(Long.toHexString(address));
                emulator.getBackend().mem_map(address / 0x1000 * 0x1000,0x1000, UnicornConst.UC_PROT_ALL);
                emulator.getBackend().mem_write(address, new byte[4]);
                return true;
            }
            @Override
            public void onAttach(UnHook unHook) {
            }
            @Override
            public void detach() {
                throw new UnsupportedOperationException();
            }
        }, UnicornConst.UC_HOOK_MEM_READ_UNMAPPED | UnicornConst.UC_HOOK_MEM_WRITE_UNMAPPED | UnicornConst.UC_HOOK_MEM_FETCH_UNMAPPED, null);
    }

}
