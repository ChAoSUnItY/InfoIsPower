package com.chaos.iip.util;

public enum RenderType {
    LEFT, RIGHT;

    public static RenderType[] vals = values();

    public RenderType next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
