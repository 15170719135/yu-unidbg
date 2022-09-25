package com.muyang._8_31;

import com.github.unidbg.Emulator;
import com.github.unidbg.arm.context.EditableArm32RegisterContext;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.DumpFileIO;
import com.github.unidbg.memory.SvcMemory;
import com.sun.jna.Pointer;

import java.util.concurrent.ThreadLocalRandom;

public class MyARMSyscallHandler extends com.github.unidbg.linux.ARM32SyscallHandler {
    // 对系统指令 扩展
    public MyARMSyscallHandler(SvcMemory svcMemory) {
        super(svcMemory);
    }
    @Override
    protected boolean handleUnknownSyscall(Emulator emulator, int NR) { // todo 解决 第4种情况
        switch (NR) {
            case 190: //进程号
                this.vfork(emulator);
                return true;
            case 359:
                this.pipe2(emulator);
                return true;
        }

        return super.handleUnknownSyscall(emulator, NR);
    }

    private void vfork(Emulator<?> emulator) {
        //emulator.getUnwinder().unwind(); // 打印调用栈
        EditableArm32RegisterContext context = (EditableArm32RegisterContext) emulator.getContext();
        int childPid = emulator.getPid() + ThreadLocalRandom.current().nextInt(256);
        int r0 = 0;
        r0 = childPid;
        System.out.println("vfork pid=" + r0);
        context.setR0(r0);
    }

    public int pipe2(Emulator<?> emulator) {
        EditableArm32RegisterContext context = (EditableArm32RegisterContext) emulator.getContext();
        Pointer pipefd = context.getPointerArg(0);
        int flags = context.getIntArg(1);
        int write = getMinFd();
        this.fdMap.put(write, new DumpFileIO(write));
        int read = getMinFd(); //上面先理解为固定写法吧..

        String stdout = "OPM4.171019.021.P1\n"; // getprop ro.build.id todo 这里是输出的结果
        this.fdMap.put(read, new ByteArrayFileIO(0, "pipe2_read_side", stdout.getBytes()));
        pipefd.setInt(0, read);
        pipefd.setInt(4, write);
        System.out.println("pipe2 pipefd=" + pipefd + ", flags=0x" + flags + ", read=" + read + ", write=" + write + ", stdout=" + stdout);
        context.setR0(0);
        return flags;
    }
}
