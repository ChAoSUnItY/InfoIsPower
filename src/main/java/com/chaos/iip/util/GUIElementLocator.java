package com.chaos.iip.util;

import net.minecraft.client.MinecraftClient;

public class GUIElementLocator {
    private static final GUIElementLocator INSTANCE = new GUIElementLocator();
    private int counter = 0;

    private GUIElementLocator() {
    }

    public static GUIElementLocator getInstance() {
        return INSTANCE;
    }

    public GUIElementLocator begin(LocatorTypes type, MinecraftClient mc) {
        this.reset();
        this.setPos(type, mc);
        return this;
    }

    public void end() {
        this.reset();
    }

    private void setPos(LocatorTypes type, MinecraftClient mc) {
        int h = mc.getWindow().getHeight();
        switch (type) {
            case LEFT_UP:
                this.counter = 4;
                break;
            case LEFT_CENTER:
                this.counter = h / 8;
                break;
            case LEFT_DOWN:
                this.counter = h / 2 - 50;
            default:
                break;
        }
    }

    private void reset() {
        this.counter = 0;
    }

    public int getNextLocation(LocatorGapTypes type) {
        this.counter += type.size;
        return this.counter;
    }

    public int getCurrent() {
        return this.counter;
    }

    public void returnCounter(int times, LocatorGapTypes type) {
        this.counter -= times * type.size;
    }

    /***
     * LEFT_DOWN IS USELESS.
     * @author ChAoS_UnItY
     *
     */
    public enum LocatorTypes {
        LEFT_UP, LEFT_CENTER, LEFT_DOWN
    }

    public enum LocatorGapTypes {
        ITEM(20), TEXT(9), TOOLTIP_TEXT(10);

        int size;

        LocatorGapTypes(int size) {
            this.size = size;
        }
    }
}