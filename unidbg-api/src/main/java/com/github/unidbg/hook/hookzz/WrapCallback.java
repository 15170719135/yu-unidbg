package com.github.unidbg.hook.hookzz;

import com.github.unidbg.Emulator;
import com.github.unidbg.arm.context.RegisterContext;

public abstract class WrapCallback<T extends RegisterContext> {
    /*preCall：调用的时候 可以在此处修改参数等.
    postCall：离开的时候 可以在此处修改返回值*/

    public abstract void preCall(Emulator<?> emulator, T ctx, HookEntryInfo info);

    public void postCall(Emulator<?> emulator, T ctx, HookEntryInfo info) {}

}
