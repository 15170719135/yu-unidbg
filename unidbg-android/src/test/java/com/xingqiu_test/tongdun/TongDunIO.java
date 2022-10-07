package com.xingqiu_test.tongdun;

import com.github.unidbg.Emulator;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.SimpleFileIO;

import java.io.File;

// 文件访问是重要的交互活动，Unidbg在使用上提供了补文件以及虚拟文件路径两个办法，使用上不可谓不方便，但文件属性访问以及文件访问是一个深坑，使用Unidbg
// 处理此项时，如果时间足够，建议逐个使用Frida Hook真机实际情况进行验证，可谓”如临深渊，如履薄冰“。
public class TongDunIO implements IOResolver {
    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("fuck path:"+pathname);
        // 这里是很无奈的一个点，maps在Unidbg中真的很尴尬，Unidbg要么返回一个很假的maps（但符合Unidbg虚拟内存布局），要么返回真实进程的maps
        // 但这与Unidbg虚拟内存布局相左，如果样本逻辑需要解析maps并做一些后续分析，就会出问题。
        // 这边构造了一个似乎可以的maps（添加了apk），因为分析发现，样本读取了maps中当前app。不确定是否有遗漏
        if ("/proc/self/maps".equals(pathname) || ("/proc/" + emulator.getPid() + "/maps").equals(pathname)) {
            String comm_path = "unidbg-android/src/test/resources/tongdun/proc/testmaps";
            return FileResult.success(new SimpleFileIO(oflags,new File(comm_path),comm_path));
        }

        // 查看存储器目录
        if(("/storage/emulated/0/Download").equals(pathname)) {
            return FileResult.success(emulator.getFileSystem().createDirectoryFileIO(
                    new File("unidbg-android/src/test/resources/tongdun/files/Download", pathname), oflags, pathname));
        }

        // 查看App目录
        if(("/data/data/com.dhgate.buyermob").equals(pathname)) {
            return FileResult.success(emulator.getFileSystem().createDirectoryFileIO(
                    new File("unidbg-android/src/test/resources/tongdun/files/com.dhgate.buyermob", pathname), oflags, pathname));
        }

        // 查看当前进程名
        if (("/proc/"+emulator.getPid()+"/cmdline").equals(pathname) || ("/proc/self/cmdline").equals(pathname)) {
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "om.dhgate.buyermob\0".getBytes()));
        }

        if("/proc/stat".equals(pathname)) {
            String stat = "cpu  1002987 235888 1507452 8562633 65509 168534 133761 0 0 0\n" +
                    "cpu0 219034 53263 507842 5848732 63587 66671 59783 0 0 0\n" +
                    "cpu1 202983 48517 343376 369233 437 35671 24115 0 0 0\n" +
                    "cpu2 236838 38306 280565 370174 743 29818 24464 0 0 0\n" +
                    "cpu3 110295 15225 217648 372748 212 30639 21688 0 0 0\n" +
                    "cpu4 53625 18817 37047 398304 245 1391 872 0 0 0\n" +
                    "cpu5 48080 19416 32243 401410 67 1231 755 0 0 0\n" +
                    "cpu6 70217 22405 32952 400331 114 1638 1164 0 0 0\n" +
                    "cpu7 61915 19939 55779 401696 101 1471 918 0 0 0\n" +
                    "intr 114927529 0 0 0 0 12143762 0 2680991 113103 0 0 0 0 1641960 0 77 0 136670 13379 30768 2726139 0 2 0 0 0 1563 566 11 11 0 0 0 0 0 0 0 0 0 0 0 0 0 734 0 84 0 0 0 0 0 0 376162 0 0 0 0 0 0 0 0 0 0 2 0 0 0 0 0 0 0 0 0 0 0 0 21 0 0 0 0 0 0 0 0 7 0 0 1 0 0 0 0 0 511 0 0 0 0 0 0 0 23 7492291 120263 17304 100552 651113 478 0 19 3816 0 24 418388 1316903 410860 0 0 0 0 0 232006 53992 1648671 0 0 0 0 0 13607 0 0 0 0 0 0 0 0 0 0 0 0 174108 0 5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 9 4 5 5 0 0 0 1395 0 1 0 2 0 2 0 2 1 0 0 2 218 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 32 0 0 0 19209 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 145524 15072 0 0 7 0 0 0 1 0 51 0 7 244 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1484 361 0 6 187 536 0 195 0 0 0 0 0 0 0 0 0 0 0 0 0 394 9 13 0 12 139 1587 16 0 0 0 0 0 0 0 0 886 0 816 1355 7036 0 21 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 827177 0 10 0 0 223 0 0 7 10 0 0 0 0 10 0 0 344383 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
                    "ctxt 209018694\n" +
                    "btime 1638246557\n" +
                    "processes 179999\n" +
                    "procs_running 1\n" +
                    "procs_blocked 0\n" +
                    "softirq 36096590 7868433 8283497 353788 1021834 2497468 0 3752124 6383970 0 5935476";

            return FileResult.success(new ByteArrayFileIO(oflags, pathname, stat.getBytes()));
        }

        // 获取系统字体库
        if(("/system/fonts/").equals(pathname)){
            return FileResult.success(emulator.getFileSystem().createDirectoryFileIO(
                    new File("unidbg-android/src/test/resources/tongdun/files/fonts", pathname), oflags, pathname));
        }
        // 尝试获取设备mac地址
        // https://www.jianshu.com/p/d0082e8e2c8f
        if("/sys/class/net/wlan0/address".equals(pathname)){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "f4:60:e2:96:db:64".getBytes()));
        }
        // arp在android 10及以上无法被app访问
        // 在搭载 Android 10 或更高版本的设备上，应用无法访问 /proc/net，其中包含与设备的网络状态相关的信息。
        // 需要访问这些信息的应用（如 VPN）应使用 NetworkStatsManager 或 ConnectivityManager 类。
        if(pathname.equals("/proc/net/arp")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/proc/arp"), pathname));
        };
        if(pathname.equals("/proc/net/tcp")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/proc/tcp"), pathname));
        };
        if("/proc/version".equals(pathname)){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/proc/version"), pathname));
        }
        if("/proc/cpuinfo".equals(pathname)){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/proc/cpuinfo"), pathname));
        }
        if(pathname.equals("/proc/net/unix")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/proc/unix"), pathname));
        };
        // 查看cpu最大频率
        // https://bbs.pediy.com/thread-229579-1.htm
        if("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq".equals(pathname)){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "1766400".getBytes()));
        }
        // 读两个status，不懂想干啥，可能是tracerpid
        if (("/proc/" + emulator.getPid() + "/status").equals(pathname) || "/proc/self/status".equals(pathname)) {
            System.out.println("Call status");
            String status = "Name:   dhgate.buyermob\n" +
                    "Umask:  0077\n" +
                    "State:  S (sleeping)\n" +
                    "Tgid:   "+emulator.getPid()+"\n" +
                    "Ngid:   0\n" +
                    "Pid:    "+emulator.getPid()+"\n" +
                    "PPid:   1682\n" +
                    "TracerPid:      0\n" +
                    "Uid:    10256   10256   10256   10256\n" +
                    "Gid:    10256   10256   10256   10256\n" +
                    "FDSize: 512\n" +
                    "Groups: 3001 3003 9997 20256 50256 99909997\n" +
                    "VmPeak:  2557156 kB\n" +
                    "VmSize:  2386280 kB\n" +
                    "VmLck:         0 kB\n" +
                    "VmPin:         0 kB\n" +
                    "VmHWM:    252796 kB\n" +
                    "VmRSS:    226608 kB\n" +
                    "RssAnon:           92552 kB\n" +
                    "RssFile:          130844 kB\n" +
                    "RssShmem:           3212 kB\n" +
                    "VmData:  1401328 kB\n" +
                    "VmStk:      8192 kB\n" +
                    "VmExe:        20 kB\n" +
                    "VmLib:    296792 kB\n" +
                    "VmPTE:      1900 kB\n" +
                    "VmPMD:        16 kB\n" +
                    "VmSwap:    14444 kB\n" +
                    "Threads:        81\n" +
                    "SigQ:   0/21555\n" +
                    "SigPnd: 0000000000000000\n" +
                    "ShdPnd: 0000000000000000\n" +
                    "SigBlk: 0000000080001200\n" +
                    "SigIgn: 0000000000000001\n" +
                    "SigCgt: 0000000e400086fc\n" +
                    "CapInh: 0000000000000000\n" +
                    "CapPrm: 0000000000000000\n" +
                    "CapEff: 0000000000000000\n" +
                    "CapBnd: 0000000000000000\n" +
                    "CapAmb: 0000000000000000\n" +
                    "Seccomp:        2\n" +
                    "Speculation_Store_Bypass:       unknown\n" +
                    "Cpus_allowed:   07\n" +
                    "Cpus_allowed_list:      0-2\n" +
                    "Mems_allowed:   1\n" +
                    "Mems_allowed_list:      0\n" +
                    "voluntary_ctxt_switches:        33007\n" +
                    "nonvoluntary_ctxt_switches:     5564";

            return FileResult.success(new ByteArrayFileIO(oflags, pathname, status.getBytes()));
        }
        if(("/proc/"+emulator.getPid()+"/task/"+emulator.getPid()+"/status").equals(pathname)){
            String taskStatus = "Name:   dhgate.buyermob\n" +
                    "Umask:  0077\n" +
                    "State:  S (sleeping)\n" +
                    "Tgid:   "+emulator.getPid()+"\n" +
                    "Ngid:   0\n" +
                    "Pid:    "+emulator.getPid()+"\n" +
                    "PPid:   1682\n" +
                    "TracerPid:      0\n" +
                    "Uid:    10256   10256   10256   10256\n" +
                    "Gid:    10256   10256   10256   10256\n" +
                    "FDSize: 512\n" +
                    "Groups: 3001 3003 9997 20256 50256 99909997\n" +
                    "VmPeak:  2557156 kB\n" +
                    "VmSize:  2368076 kB\n" +
                    "VmLck:         0 kB\n" +
                    "VmPin:         0 kB\n" +
                    "VmHWM:    213476 kB\n" +
                    "VmRSS:    211496 kB\n" +
                    "RssAnon:           90424 kB\n" +
                    "RssFile:          117640 kB\n" +
                    "RssShmem:           3432 kB\n" +
                    "VmData:  1394432 kB\n" +
                    "VmStk:      8192 kB\n" +
                    "VmExe:        20 kB\n" +
                    "VmLib:    296792 kB\n" +
                    "VmPTE:      1888 kB\n" +
                    "VmPMD:        16 kB\n" +
                    "VmSwap:    18780 kB\n" +
                    "Threads:        72\n" +
                    "SigQ:   0/21555\n" +
                    "SigPnd: 0000000000000000\n" +
                    "ShdPnd: 0000000000000000\n" +
                    "SigBlk: 0000000080001200\n" +
                    "SigIgn: 0000000000000001\n" +
                    "SigCgt: 0000000e400086fc\n" +
                    "CapInh: 0000000000000000\n" +
                    "CapPrm: 0000000000000000\n" +
                    "CapEff: 0000000000000000\n" +
                    "CapBnd: 0000000000000000\n" +
                    "CapAmb: 0000000000000000\n" +
                    "Seccomp:        2\n" +
                    "Speculation_Store_Bypass:       unknown\n" +
                    "Cpus_allowed:   07\n" +
                    "Cpus_allowed_list:      0-2\n" +
                    "Mems_allowed:   1\n" +
                    "Mems_allowed_list:      0\n" +
                    "voluntary_ctxt_switches:        33011\n" +
                    "nonvoluntary_ctxt_switches:     5564";
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, taskStatus.getBytes()));
        }

        // /dev/binder
        if(pathname.equals("/dev/binder")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/files/binder"), pathname));
        };

        // apk
        if(pathname.equals("/data/app/com.dhgate.buyermob-4j4PfNvJ27HiE6sPnXQ3gg==/base.apk")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/runtime/dhgate.apk"), pathname));
        }
        // SO
        if(pathname.equals("/data/app/com.dhgate.buyermob-1/lib/arm/libtongdun.so")){
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/tongdun/runtime/libtongdun.so"), pathname));
        }
        // 检测多开/模拟器/docker等等，除此之外还获取了一些系统文件/目录参与设备指纹的计算，感兴趣的自己细细补吧

        return null;
    }
}
