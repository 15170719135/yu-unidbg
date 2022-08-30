package com.github.unidbg.hook;

import com.github.unidbg.Module;

/**
  都是它的实现
 序号	框架名称	是否支持单行hook	是否支持多行hook	其他	备注
 1	ConsoleDebugger	支持	支持		推荐使用
 2	HookZz	支持	支持	支持inline hook
 3	Whale	支持	支持	支持导入函数
 4	Dobby	支持	支持
 5	IxHook	支持	支持
 */
public interface IHook {

    Module getModule();

}
