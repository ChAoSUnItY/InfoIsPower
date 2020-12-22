package com.chaos.iip.utils;

public enum RenderLocationType {
    LEFT, RIGHT;

    public static RenderLocationType[] vals = values();

    public RenderLocationType next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
