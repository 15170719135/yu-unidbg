package com.muyang._8_32;

import com.github.unidbg.Emulator;
import com.github.unidbg.arm.context.EditableArm32RegisterContext;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.DumpFileIO;
import com.github.unidbg.memory.SvcMemory;
import com.sun.jna.Pointer;

import java.util.concurrent.ThreadLocalRandom;

public class MyARMSyscallHandler extends com.github.unidbg.linux.ARM32SyscallHandler {
    public MyARMSyscallHandler(SvcMemory svcMemory) {
        super(svcMemory);
    }
    @Override
    protected boolean handleUnknownSyscall(Emulator emulator, int NR) {
        switch (NR) {
            case 190:
                vfork(emulator);
                return true;
            case 359:
                pipe2(emulator);
                return true;
        }

        return super.handleUnknownSyscall(emulator, NR);
    }

    private void vfork(Emulator<?> emulator) {
        EditableArm32RegisterContext context = (EditableArm32RegisterContext) emulator.getContext();
        int childPid = emulator.getPid() + ThreadLocalRandom.current().nextInt(256);
        int r0 = 0;
        r0 = childPid;
        System.out.println("补的 vfork pid=" + r0);
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
    public int pipe2(Emulator<?> emulator) {
        EditableArm32RegisterContext context = (EditableArm32RegisterContext) emulator.getContext();
        Pointer pipefd = context.getPointerArg(0);
        int flags = context.getIntArg(1);
        int write = getMinFd();
        this.fdMap.put(write, new DumpFileIO(write));
        int read = getMinFd();
        String stdout = "";
        String shuxin = emulator.get("shuxin");
        switch (shuxin){
            case "uname -a":
                stdout = "Linux localhost 3.18.70-g91a2acf #1 SMP PREEMPT Fri May 11 01:06:35 UTC 2018 aarch64\n"; // getprop ro.build.id
                break;
            case "cd /system/bin && ls -l":
                stdout = "total 18368\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 acpi -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell    1702856 2009-01-01 16:00 adbd\n" +
                        "-rwxr-xr-x 1 root   shell        207 2009-01-01 16:00 am\n" +
                        "lrwxr-xr-x 1 root   shell         13 2009-01-01 16:00 app_process -> app_process64\n" +
                        "-rwxr-xr-x 1 root   shell     147212 2022-09-04 16:33 app_process32\n" +
                        "-rwxr-xr-x 1 root   shell     234272 2022-09-04 16:33 app_process64\n" +
                        "-rwxr-xr-x 1 root   shell     260240 2009-01-01 16:00 applypatch\n" +
                        "-rwxr-xr-x 1 root   shell         33 2009-01-01 16:00 appops\n" +
                        "-rwxr-xr-x 1 root   shell        215 2009-01-01 16:00 appwidget\n" +
                        "-rwxr-xr-x 1 root   shell     104824 2009-01-01 16:00 atrace\n" +
                        "-rwxr-xr-x 1 root   shell      20536 2009-01-01 16:00 audioserver\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 base64 -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 basename -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      49320 2009-01-01 16:00 bcc\n" +
                        "-rwxr-xr-x 1 root   shell      15224 2009-01-01 16:00 blkid\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 blockdev -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        199 2009-01-01 16:00 bmgr\n" +
                        "-rwxr-xr-x 1 root   shell      19816 2009-01-01 16:00 bootanimation\n" +
                        "-rwxr-xr-x 1 root   shell      40424 2009-01-01 16:00 bootstat\n" +
                        "-rwxr-xr-x 1 root   shell        156 2009-01-01 16:00 bu\n" +
                        "-rwxr-xr-x 1 root   shell     244680 2009-01-01 16:00 bufferhubd\n" +
                        "-rwxr-xr-x 1 root   shell      11032 2009-01-01 16:00 bugreport\n" +
                        "-rwxr-xr-x 1 root   shell      11040 2009-01-01 16:00 bugreportz\n" +
                        "lrwxr-xr-x 1 root   shell          5 2009-01-01 16:00 bunzip2 -> bzip2\n" +
                        "lrwxr-xr-x 1 root   shell          5 2009-01-01 16:00 bzcat -> bzip2\n" +
                        "-rwxr-xr-x 1 root   shell      96864 2009-01-01 16:00 bzip2\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 cal -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      16256 2009-01-01 16:00 cameraserver\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 cat -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 chcon -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 chgrp -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 chmod -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 chown -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 chroot -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 chrt -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 cksum -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      57968 2009-01-01 16:00 clatd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 clear -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      52864 2009-01-01 16:00 cmd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 cmp -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 comm -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        207 2009-01-01 16:00 content\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 cp -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 cpio -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell       2471 2009-01-01 16:00 cppreopts.sh\n" +
                        "-rwxr-xr-x 1 root   shell      97908 2009-01-01 16:00 crash_dump32\n" +
                        "-rwxr-xr-x 1 root   shell     119216 2009-01-01 16:00 crash_dump64\n" +
                        "-rwxr-xr-x 1 root   shell     510112 2009-01-01 16:00 curl\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 cut -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell         10 2009-01-01 16:00 dalvikvm -> dalvikvm64\n" +
                        "-rwxr-xr-x 1 root   shell      24680 2009-01-01 16:00 dalvikvm32\n" +
                        "-rwxr-xr-x 1 root   shell      19496 2009-01-01 16:00 dalvikvm64\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 date -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          7 2009-01-01 16:00 dd -> toolbox\n" +
                        "-rwxr-xr-x 1 root   shell      11184 2009-01-01 16:00 debuggerd\n" +
                        "-rwxr-xr-x 1 root   shell     137792 2009-01-01 16:00 dex2oat\n" +
                        "-rwxr-xr-x 1 root   shell      27784 2009-01-01 16:00 dexdiag\n" +
                        "-rwxr-xr-x 1 root   shell     100260 2009-01-01 16:00 dexdump\n" +
                        "-rwxr-xr-x 1 root   shell      15416 2009-01-01 16:00 dexlist\n" +
                        "-rwxr-xr-x 1 root   shell      29112 2009-01-01 16:00 dexoptanalyzer\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 df -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 diff -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 dirname -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 dmesg -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     180232 2009-01-01 16:00 dnsmasq\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 dos2unix -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        156 2009-01-01 16:00 dpm\n" +
                        "-rwxr-xr-x 1 root   shell      66508 2009-01-01 16:00 drmserver\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 du -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     284512 2009-01-01 16:00 dumpstate\n" +
                        "-rwxr-xr-x 1 root   shell      36240 2009-01-01 16:00 dumpsys\n" +
                        "-rwxr-xr-x 1 root   shell     243032 2009-01-01 16:00 e2fsck\n" +
                        "-rwxr-xr-x 1 root   shell      24032 2009-01-01 16:00 e2fsdroid\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 echo -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          4 2009-01-01 16:00 egrep -> grep\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 env -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 expand -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 expr -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 fallocate -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 false -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          4 2009-01-01 16:00 fgrep -> grep\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 file -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 find -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 flock -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 free -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      94424 2009-01-01 16:00 fsck.f2fs\n" +
                        "-rwxr-xr-x 1 root   shell      36008 2009-01-01 16:00 fsck_msdos\n" +
                        "-rwxr-xr-x 1 root   shell      45448 2009-01-01 16:00 gatekeeperd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 getenforce -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          7 2009-01-01 16:00 getevent -> toolbox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 getprop -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      29424 2009-01-01 16:00 grep\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 groups -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 gunzip -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 gzip -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 head -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      62512 2009-01-01 16:00 healthd\n" +
                        "-rwxr-xr-x 1 root   shell        213 2009-01-01 16:00 hid\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 hostname -> toybox\n" +
                        "drwxr-xr-x 2 root   shell       4096 2009-01-01 16:00 hw\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 hwclock -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      87496 2009-01-01 16:00 hwservicemanager\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 id -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      36192 2009-01-01 16:00 idmap\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 ifconfig -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        194 2009-01-01 16:00 ime\n" +
                        "-rwxr-xr-x 1 root   shell      28040 2009-01-01 16:00 incident\n" +
                        "-rwxr-xr-x 1 root   shell      61944 2009-01-01 16:00 incidentd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 inotifyd -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        203 2009-01-01 16:00 input\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 insmod -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     286632 2009-01-01 16:00 installd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 ionice -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 iorenice -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     258960 2009-01-01 16:00 ip\n" +
                        "lrwxr-xr-x 1 root   shell         20 2009-01-01 16:00 ip-wrapper-1.0 -> netutils-wrapper-1.0\n" +
                        "-rwxr-xr-x 1 root   shell     443176 2009-01-01 16:00 ip6tables\n" +
                        "lrwxr-xr-x 1 root   shell          9 2009-01-01 16:00 ip6tables-restore -> ip6tables\n" +
                        "lrwxr-xr-x 1 root   shell          9 2009-01-01 16:00 ip6tables-save -> ip6tables\n" +
                        "lrwxr-xr-x 1 root   shell         20 2009-01-01 16:00 ip6tables-wrapper-1.0 -> netutils-wrapper-1.0\n" +
                        "-rwxr-xr-x 1 root   shell     429824 2009-01-01 16:00 iptables\n" +
                        "lrwxr-xr-x 1 root   shell          8 2009-01-01 16:00 iptables-restore -> iptables\n" +
                        "lrwxr-xr-x 1 root   shell          8 2009-01-01 16:00 iptables-save -> iptables\n" +
                        "lrwxr-xr-x 1 root   shell         20 2009-01-01 16:00 iptables-wrapper-1.0 -> netutils-wrapper-1.0\n" +
                        "-rwxr-xr-x 1 root   shell     222560 2009-01-01 16:00 keystore\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 kill -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 killall -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     781568 2009-01-01 16:00 ld.mc\n" +
                        "-rwxr-xr-x 1 root   shell     846548 2009-01-01 16:00 linker\n" +
                        "-rwxr-xr-x 1 root   shell    1342472 2009-01-01 16:00 linker64\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 linker_asan -> linker\n" +
                        "lrwxr-xr-x 1 root   shell          8 2009-01-01 16:00 linker_asan64 -> linker64\n" +
                        "-rwxr-xr-x 1 root   shell      19520 2009-01-01 16:00 lmkd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 ln -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 load_policy -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        194 2009-01-01 16:00 locksettings\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 log -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell       6840 2009-01-01 16:00 logcat\n" +
                        "-r-xr-x--- 1 logd   logd      149600 2009-01-01 16:00 logd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 logname -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      70720 2009-01-01 16:00 logwrapper\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 losetup -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 ls -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell       6840 2009-01-01 16:00 lshal\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 lsmod -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 lsof -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 lspci -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 lsusb -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      11136 2009-01-01 16:00 make_ext4fs\n" +
                        "-rwxr-xr-x 1 root   shell      32208 2009-01-01 16:00 make_f2fs\n" +
                        "-rwxr-xr-x 1 root   shell      42264 2009-01-01 16:00 mct-unit-test-app\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 md5sum -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell    1088744 2009-01-01 16:00 mdnsd\n" +
                        "-rwxr-xr-x 1 root   shell        210 2009-01-01 16:00 media\n" +
                        "-rwxr-xr-x 1 root   shell      24860 2009-01-01 16:00 mediadrmserver\n" +
                        "-rwxr-xr-x 1 root   shell      11152 2009-01-01 16:00 mediaextractor\n" +
                        "-rwxr-xr-x 1 root   shell      49424 2009-01-01 16:00 mediametrics\n" +
                        "-rwxr-xr-x 1 root   shell      16332 2009-01-01 16:00 mediaserver\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 microcom -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mkdir -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      68984 2009-01-01 16:00 mke2fs\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mkfifo -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mknod -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mkswap -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mktemp -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      89080 2009-01-01 16:00 mm-qcamera-app\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 modinfo -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 modprobe -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        268 2009-01-01 16:00 monkey\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 more -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mount -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mountpoint -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      23888 2009-01-01 16:00 mtpd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 mv -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      96488 2009-01-01 16:00 nanotool\n" +
                        "-rwxr-xr-x 1 root   shell      11064 2009-01-01 16:00 ndc\n" +
                        "lrwxr-xr-x 1 root   shell         20 2009-01-01 16:00 ndc-wrapper-1.0 -> netutils-wrapper-1.0\n" +
                        "-rwxr-xr-x 1 root   shell     445864 2009-01-01 16:00 netd\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 netstat -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      65240 2009-01-01 16:00 netutils-wrapper-1.0\n" +
                        "lrwxr-xr-x 1 root   shell          7 2009-01-01 16:00 newfs_msdos -> toolbox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 nice -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 nl -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 nohup -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     240424 2009-01-01 16:00 oatdump\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 od -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     178160 2009-01-01 16:00 otapreopt\n" +
                        "-rwxr-xr-x 1 root   shell      81504 2009-01-01 16:00 otapreopt_chroot\n" +
                        "-rwxr-xr-x 1 root   shell       2416 2009-01-01 16:00 otapreopt_script\n" +
                        "-rwxr-xr-x 1 root   shell       1420 2009-01-01 16:00 otapreopt_slot\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 paste -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 patch -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      54668 2009-01-01 16:00 patchoat\n" +
                        "-rwxr-xr-x 1 root   shell     159976 2009-01-01 16:00 performanced\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 pgrep -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 pidof -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      40568 2009-01-01 16:00 ping\n" +
                        "-rwxr-xr-x 1 root   shell      41128 2009-01-01 16:00 ping6\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 pkill -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      15200 2009-01-01 16:00 pktlogconf\n" +
                        "-rwxr-xr-x 1 root   shell        191 2009-01-01 16:00 pm\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 pmap -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     256424 2009-01-01 16:00 pppd\n" +
                        "-rwxr-xr-x 1 root   shell       1313 2009-01-01 16:00 preloads_copy.sh\n" +
                        "-rwxr-xr-x 1 root   shell      15240 2009-01-01 16:00 preopt2cachename\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 printenv -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 printf -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      79900 2009-01-01 16:00 profman\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 ps -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 pwd -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     137224 2009-01-01 16:00 qmi_simple_ril_test\n" +
                        "-rwxr-xr-x 1 root   shell     277408 2009-01-01 16:00 racoon\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 readlink -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 realpath -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell       6976 2009-01-01 16:00 reboot\n" +
                        "-rwxr-xr-x 1 root   shell      19584 2009-01-01 16:00 recovery-persist\n" +
                        "-rwxr-xr-x 1 root   shell      15432 2009-01-01 16:00 recovery-refresh\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 renice -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        188 2009-01-01 16:00 requestsync\n" +
                        "-rwxr-xr-x 1 root   shell      52624 2009-01-01 16:00 resize2fs\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 restorecon -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 rm -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 rmdir -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 rmmod -> toybox\n" +
                        "-rwxr-x--- 1 root   shell      11072 2009-01-01 16:00 run-as\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 runcon -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell       6920 2009-01-01 16:00 schedtest\n" +
                        "-rwxr-xr-x 1 root   shell      15352 2009-01-01 16:00 screencap\n" +
                        "-rwxr-xr-x 1 root   shell     115000 2009-01-01 16:00 screenrecord\n" +
                        "-rwxr-xr-x 1 root   shell      48608 2009-01-01 16:00 sdcard\n" +
                        "-rwxr-xr-x 1 root   shell      23736 2009-01-01 16:00 secdiscard\n" +
                        "-rwx------ 1 root   root      311264 2009-01-01 16:00 secilc\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sed -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sendevent -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      11096 2009-01-01 16:00 sensorservice\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 seq -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      36056 2009-01-01 16:00 service\n" +
                        "-rwxr-xr-x 1 root   shell      19688 2009-01-01 16:00 servicemanager\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 setenforce -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 setprop -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 setsid -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell         35 2009-01-01 16:00 settings\n" +
                        "-rwxr-xr-x 1 root   shell      11096 2009-01-01 16:00 setup_fs\n" +
                        "-rwxr-xr-x 1 root   shell     162184 2009-01-01 16:00 sgdisk\n" +
                        "-rwxr-xr-x 1 root   shell     302936 2009-01-01 16:00 sh\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sha1sum -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sha224sum -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sha256sum -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sha384sum -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sha512sum -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sleep -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        190 2009-01-01 16:00 sm\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sort -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 split -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 start -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 stat -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 stop -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     128272 2009-01-01 16:00 storaged\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 strings -> toybox\n" +
                        "-rwxr-xr-x 1 system graphics   37088 2009-01-01 16:00 surfaceflinger\n" +
                        "-rwxr-xr-x 1 root   shell        192 2009-01-01 16:00 svc\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 swapoff -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 swapon -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sync -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 sysctl -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 tac -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 tail -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 tar -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 taskset -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      98864 2009-01-01 16:00 tc\n" +
                        "lrwxr-xr-x 1 root   shell         20 2009-01-01 16:00 tc-wrapper-1.0 -> netutils-wrapper-1.0\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 tee -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell        172 2009-01-01 16:00 telecom\n" +
                        "-rwxr-xr-x 1 root   shell     126132 2009-01-01 16:00 test_bet_8996\n" +
                        "-rwxr-xr-x 1 root   shell      28656 2009-01-01 16:00 test_module_pproc\n" +
                        "-rwxr-xr-x 1 root   shell      32608 2009-01-01 16:00 thermalserviced\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 time -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 timeout -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      11088 2009-01-01 16:00 tinycap\n" +
                        "-rwxr-xr-x 1 root   shell      15432 2009-01-01 16:00 tinymix\n" +
                        "-rwxr-xr-x 1 root   shell      11120 2009-01-01 16:00 tinypcminfo\n" +
                        "-rwxr-xr-x 1 root   shell      11176 2009-01-01 16:00 tinyplay\n" +
                        "-rwxr-xr-x 1 root   shell     146528 2009-01-01 16:00 tombstoned\n" +
                        "-rwxr-xr-x 1 root   shell      92872 2009-01-01 16:00 toolbox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 top -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 touch -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     403888 2009-01-01 16:00 toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 tr -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 true -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 truncate -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 tty -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      56544 2009-01-01 16:00 tune2fs\n" +
                        "-rwxr-xr-x 1 root   shell      27704 2009-01-01 16:00 tzdatacheck\n" +
                        "-rwxr-xr-x 1 root   shell       4156 2009-01-01 16:00 uiautomator\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 ulimit -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 umount -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 uname -> toybox\n" +
                        "-rwxr-x--- 1 root   root      130128 2009-01-01 16:00 uncrypt\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 uniq -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 unix2dos -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     531648 2009-01-01 16:00 update_engine\n" +
                        "-rwxr-xr-x 1 root   shell      32192 2009-01-01 16:00 update_verifier\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 uptime -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 usleep -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 uudecode -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 uuencode -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell      19568 2009-01-01 16:00 vdc\n" +
                        "-rwxr-xr-x 1 root   shell     257184 2009-01-01 16:00 virtual_touchpad\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 vmstat -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     710688 2009-01-01 16:00 vold\n" +
                        "-rwxr-xr-x 1 root   shell        152 2009-01-01 16:00 vr\n" +
                        "-rwxr-xr-x 1 root   shell      49528 2009-01-01 16:00 vr_hwc\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 wc -> toybox\n" +
                        "-rwxr-x--- 1 root   root       20820 2009-01-01 16:00 webview_zygote32\n" +
                        "-rwxr-x--- 1 root   root       19832 2009-01-01 16:00 webview_zygote64\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 which -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 whoami -> toybox\n" +
                        "-rwxr-xr-x 1 root   shell     299216 2009-01-01 16:00 wificond\n" +
                        "-rwxr-xr-x 1 root   shell        190 2009-01-01 16:00 wm\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 xargs -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 xxd -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 yes -> toybox\n" +
                        "lrwxr-xr-x 1 root   shell          6 2009-01-01 16:00 zcat -> toybox\n";
                break;
            case "stat /root":
                stdout = "  File: `/root'\n" +
                        "  Size: 4096     Blocks: 8       IO Blocks: 512 directory\n" +
                        "Device: 10311h/66321d    Inode: 2560     Links: 2\n" +
                        "Access: (755/drwxr-xr-x)        Uid: (    0/    root)   Gid: (    0/    root)\n" +
                        "Access: 2009-01-01 16:00:00.000000000\n" +
                        "Modify: 2009-01-01 16:00:00.000000000\n" +
                        "Change: 2009-01-01 16:00:00.000000000\n";
                break;

        }

//        String stdout = "Linux localhost 3.18.70-g91a2acf #1 SMP PREEMPT Fri May 11 01:06:35 UTC 2018 aarch64\n"; // getprop ro.build.id
        this.fdMap.put(read, new ByteArrayFileIO(0, "pipe2_read_side", stdout.getBytes()));
        pipefd.setInt(0, read);
        pipefd.setInt(4, write);
        System.out.println("pipe2 pipefd=" + pipefd + ", flags=0x" + flags + ", read=" + read + ", write=" + write + ", stdout=" + stdout);
        context.setR0(0);
        return flags;
    }
}
