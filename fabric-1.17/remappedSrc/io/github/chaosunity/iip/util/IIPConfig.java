package io.github.chaosunity.iip.util;

import io.github.chaosunity.iip.InfoIsPower;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = InfoIsPower.MODID)
public class IIPConfig implements ConfigData {
    public boolean enableRender = true;
    public DisplayMode displayMode = DisplayMode.TOOLS;
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
