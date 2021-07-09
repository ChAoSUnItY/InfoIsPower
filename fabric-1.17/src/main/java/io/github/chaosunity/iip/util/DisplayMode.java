package io.github.chaosunity.iip.util;

public enum DisplayMode {
    TOOLS, ARMOR, SIMPLIFIED;

    public static DisplayMode[] vals = values();

    public DisplayMode next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
