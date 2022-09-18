package com.github.unidbg.debugger;

import com.github.unidbg.Emulator;

public interface BreakPointCallback {

    /**
     * 当断点被触发时回调, 返回true表示不暂停，继续进行 (打印日志,修改值啥的)
     */
    boolean onHit(Emulator<?> emulator, long address);

}
