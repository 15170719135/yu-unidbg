package com.github.unidbg.arm.context;

import com.github.unidbg.pointer.UnidbgPointer;

public interface RegisterContext {

    /**
     * @param index 0 based
     */
    int getIntArg(int index);

    /**
     * @param index 0 based
     */
    long getLongArg(int index);

    /**
     * @param index 0 based
     */
    UnidbgPointer getPointerArg(int index);// 猜想: 获取该指针对象的 第几个属性?

    long getLR();

    UnidbgPointer getLRPointer();

    UnidbgPointer getPCPointer();

    /**
     * sp
     */
    UnidbgPointer getStackPointer();

    int getIntByReg(int regId);
    long getLongByReg(int regId);

}
