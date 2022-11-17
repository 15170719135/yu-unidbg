package com.github.unidbg.file.linux;

import com.github.unidbg.Emulator;
import com.github.unidbg.file.BaseFileIO;
import com.github.unidbg.linux.struct.StatFS;
import com.sun.jna.Pointer;
/*
    常用:
    1. SimpleFileIO
    return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/resources/dewu/cpu/boot_id"), pathname));
    2.ByteArrayFileIO
    return FileResult.success(new ByteArrayFileIO(oflags, pathname, "文件内容xxx".getBytes(StandardCharsets.UTF_8)));
    3. 接着是对文件夹或者说目录的处理，也很简单，使用 DirectoryFileIO
     return FileResult.success(new DirectoryFileIO(oflags, pathname, new File("unidbg-android/src/test/resources/meituan/data")));

     简而言之，从基本规则来说，遇到文件访问那么从真机直接拷贝出来，使用SimpleFileIO / ByteArrayFileIO / DirectoryFileIO 或虚拟文件系统予以返回即可

 */

public abstract class BaseAndroidFileIO extends BaseFileIO implements AndroidFileIO {

    public BaseAndroidFileIO(int oflags) {
        super(oflags);
    }

    @Override
    public int fstat(Emulator<?> emulator, StatStructure stat) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public int getdents64(Pointer dirp, int size) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public AndroidFileIO accept(Pointer addr, Pointer addrlen) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public int statfs(StatFS statFS) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    protected void setFlags(long arg) {
        if ((IOConstants.O_APPEND & arg) != 0) {
            oflags |= IOConstants.O_APPEND;
        }
        if ((IOConstants.O_RDWR & arg) != 0) {
            oflags |= IOConstants.O_RDWR;
        }
        if ((IOConstants.O_NONBLOCK & arg) != 0) {
            oflags |= IOConstants.O_NONBLOCK;
        }
    }
}
