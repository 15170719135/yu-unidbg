package com.muyang._8_23;

public class AccessibilityServiceInfo {
    public String[] packageNames;
    public String name;
    public String packageName;
    public String tag;

    public AccessibilityServiceInfo(String name, String packageName, String tag) {
        this.name = name;
        this.packageName = packageName;
        this.tag = tag;
    }
}
