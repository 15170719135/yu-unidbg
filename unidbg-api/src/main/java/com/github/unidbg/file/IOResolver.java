package com.github.unidbg.file;

import com.github.unidbg.Emulator;

// 补环境: 需要 解析外部文件
public interface IOResolver<T extends NewFileIO> {

    FileResult<T> resolve(Emulator<T> emulator, String pathname, int oflags);

}
