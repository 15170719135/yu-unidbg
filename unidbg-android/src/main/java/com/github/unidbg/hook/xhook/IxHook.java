package com.github.unidbg.hook.xhook;

import com.github.unidbg.hook.IHook;
import com.github.unidbg.hook.ReplaceCallback;

/**
 * Only support android
 */
//爱奇艺开源的PLT hook 库，受制于原理，无法Hook内部函数（IDA里的Sub_xxx），但在它能Hook的函数上，稳定性比较好
public interface IxHook extends IHook {

    int RET_SUCCESS = 0;

    void register(String pathname_regex_str, String symbol, ReplaceCallback callback);
    void register(String pathname_regex_str, String symbol, ReplaceCallback callback, boolean enablePostCall);

    void refresh();

}
