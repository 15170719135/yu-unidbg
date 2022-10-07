package com.xingqiu_test.tongdun;

import com.github.unidbg.Emulator;
import com.github.unidbg.arm.context.EditableArm32RegisterContext;
import com.github.unidbg.linux.ARM32SyscallHandler;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.DumpFileIO;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.unix.struct.TimeVal32;
import com.github.unidbg.unix.struct.TimeZone;
import com.sun.jna.Pointer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

public class TongdunARM32SyscallHandler extends ARM32SyscallHandler {
    public TongdunARM32SyscallHandler(SvcMemory svcMemory) {
        super(svcMemory);
    }

    @Override
    protected boolean handleUnknownSyscall(Emulator emulator, int NR) {
        switch (NR) {
            case 190:
                vfork(emulator);
                return true;
            case 114:
                wait4(emulator);
                return true;
            case 359:
                pipe2(emulator);
                return true;
        }
        return super.handleUnknownSyscall(emulator, NR);
    }

    private void vfork(Emulator<?> emulator) {
        EditableArm32RegisterContext context = (EditableArm32RegisterContext) emulator.getContext();
        int childPid = emulator.getPid() +
                ThreadLocalRandom.current().nextInt(256);
        int r0 = childPid;
        System.out.println("vfork pid=" + r0);
        context.setR0(r0);
    }

    private void wait4(Emulator emulator) {
        EditableArm32RegisterContext context = (EditableArm32RegisterContext) emulator.getContext();
        int pid = context.getR0Int();
        Pointer wstatus = context.getR1Pointer();
        int options = context.getR2Int();
        Pointer rusage = context.getR3Pointer();
        System.out.println("wait4 pid=" + pid + ", wstatus=" + wstatus + ", options=0x" + Integer.toHexString(options) + ", rusage=" + rusage);
    }

    // 处理popen
    protected int pipe2(Emulator<?> emulator) {
        EditableArm32RegisterContext context = (EditableArm32RegisterContext)
                emulator.getContext();
        Pointer pipefd = context.getPointerArg(0);
        int flags = context.getIntArg(1);
        int write = getMinFd();
        this.fdMap.put(write, new DumpFileIO(write));
        int read = getMinFd();
        String command = emulator.get("command");
        System.out.println("fuck cmd:"+command);
        // stdout中写入popen command 应该返回的结果
        String stdout = "\n";
        // 如下三条，检测MAGISK
        if(command.equals("ls /sbin | grep magisk")){
            // do nothing
            stdout = "\n";
        }
        if(command.equals("df  | grep /sbin/.magisk")){
            // do nothing
            stdout = "\n";
        }
        if(command.equals("mount  | grep /sbin/.magisk")){
            // do nothing
            stdout = "\n";
        }
        // 没想好怎么返回
        // 这里大概可能也许是检测多开的
        if(command.equals("id")){
            stdout = "\n";
        }
        // https://blog.csdn.net/qq_38741986/article/details/104467609
        if(command.equals("ps|grep com.android.commands.monkey")){
            stdout = "\n";
        }
        // 通过getuid+ps 获得当前进程所运行的app，然后做access查看文件
        if(command.equals("ps")){
            String psString = readToString("unidbg-android/src/test/resources/tongdun/proc/ps");
            stdout = psString + "\n";
        }
        // 检测自动化测试工具Monkey？
        // https://blog.csdn.net/qq_38741986/article/details/104467609
        if(command.equals("ps|grep com.android.commands.monkey")){
            stdout = "\n";
        }
        // 查看是否开启了SELinux
        if(command.equals("getenforce")){
            stdout = "Enforcing\n";
        }
        this.fdMap.put(read, new ByteArrayFileIO(0, "pipe2_read_side", stdout.getBytes()));
        pipefd.setInt(0, read);
        pipefd.setInt(4, write);
        System.out.println("pipe2 pipefd=" + pipefd + ", flags=0x" + flags + ", read=" + read + ", write=" + write + ", stdout=" + stdout);
        context.setR0(0);
        return 0;
    }


    // 处理Unidbg gettimeofday的小bug
    @Override
    protected int gettimeofday(Emulator<?> emulator, Pointer tv, Pointer tz) {
//        System.out.println("call mytime");
        long currentTimeMillis = System.currentTimeMillis();
        long tv_sec = currentTimeMillis / 1000;
        long tv_usec = (currentTimeMillis % 1000) * 1000;

        if (tv != null) {
            TimeVal32 timeVal = new TimeVal32(tv);
            timeVal.tv_sec = (int) tv_sec;
            timeVal.tv_usec = (int) tv_usec;
            timeVal.pack();
        }

        if (tz != null) {
            Calendar calendar = Calendar.getInstance();
            int tz_minuteswest = -(calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
            TimeZone timeZone = new TimeZone(tz);
            timeZone.tz_minuteswest = tz_minuteswest;
            timeZone.tz_dsttime = 0;
            timeZone.pack();
        }
        return 0;
    }

    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}
