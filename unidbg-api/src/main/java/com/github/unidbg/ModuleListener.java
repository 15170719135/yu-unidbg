package com.github.unidbg;

public interface ModuleListener {

    void onLoaded(Emulator<?> emulator, Module module); // 监控so文件加载完成后 会调用的方法 (loadLibrary)

}
