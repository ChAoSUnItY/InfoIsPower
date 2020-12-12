package com.chaos.iip.utils;

import net.minecraft.client.Minecraft;

/***
 * This class is a mess, I've warned you.
 * @author ChAoS
 *
 */
public class GUIElementLocator {
    private static final GUIElementLocator INSTANCE = new GUIElementLocator();
    private int counter = 0;

    private GUIElementLocator() {
    }

    public static GUIElementLocator getInstance() {
        return INSTANCE;
    }

    public GUIElementLocator begin(LocatorTypes type, Minecraft mc) {
        this.reset();
        this.setPos(type, mc);
        return this;
    }

    public void end() {
        this.reset();
    }

    private void setPos(LocatorTypes type, Minecraft mc) {
        int h = mc.getMainWindow().getHeight();
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
        switch (type) {
            case TEXT:
                this.counter += 9;
                break;
            case ITEM:
                this.counter += 20;
                break;
            default:
                break;
        }
        return this.counter;
    }

    public int getCurrent() {
        return this.counter;
    }

    public void returnCounter(int times, LocatorGapTypes type) {
        switch (type) {
            case TEXT:
                this.counter -= times * 9;
                break;
            case ITEM:
                this.counter -= times * 20;
            default:
                break;
        }
    }

    /***
     * LEFT_DOWN IS USELESS.
     * @author ChAoS_UnItY
     *
     */
    public enum LocatorTypes {
        LEFT_UP, LEFT_CENTER, LEFT_DOWN;
    }

    public enum LocatorGapTypes {
        ITEM, TEXT;
    }
}