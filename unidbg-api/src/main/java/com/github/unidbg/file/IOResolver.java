package com.github.unidbg.file;

import com.github.unidbg.Emulator;

// 补环境: 需要 解析外部文件
public interface IOResolver<T extends NewFileIO> {

    FileResult<T> resolve(Emulator<T> emulator, String pathname, int oflags);
    /*
        /proc/net/ 下的文件一律不需要补 Google禁止普通进程访问该目录，这一规定生效于android 10以及更高版本上

        /proc/self(pid)/maps maps是补文件访问时最重要的文件，务必重视

        /proc/self(pid)/fd/ 就是一个好例子。FD目录包含了当前(pid)进程打开的每一个文件，目录中每一个条目都是符号链接，指向实际打开的地址。如:readlinkat() 函数读取fd

        当fd 链接一个文件时, 要先 createDalvikVM 这个fd指定的文件, 再 return SimpleFileIO 这个文件
        emulator.getSyscallHandler().open(emulator, "/data/app/com.example.fddemo-ZNS4KZftr0zbBcMSmmYZHw==/base.apk", 0);
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/fddemo/app-debug.apk"));

        if(pathname.equals("/data/app/com.example.fddemo-ZNS4KZftr0zbBcMSmmYZHw==/base.apk")){
    return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/fddemo/app-debug.apk"), pathname));
}

     */

}
