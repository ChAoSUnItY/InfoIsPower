package com.chaos.iip.util;

import com.chaos.iip.InfoIsPower;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

import java.lang.reflect.Field;

@Config(name = InfoIsPower.MODID)
public class IIPConfig implements ConfigData {
    public boolean enableRender = true;
    public int displayMode = 1;
    public RenderType renderType = RenderType.LEFT;
    @ConfigEntry.Gui.CollapsibleObject
    public RenderFilter filter = new RenderFilter();

    public static class RenderFilter {
        public boolean fps = true;
        public boolean biome = true;
        public boolean health = true;
        public boolean hungerSaturation = true;
        public boolean armor = true;
        public boolean pos = true;
        public boolean equipment = true;
        public boolean heldItem = true;

        public void setBool(boolean bool, int index) {
            try {
                RenderFilter.class.getDeclaredFields()[index].setBoolean(this, bool);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}