package com.chaos.iip;

import com.chaos.iip.screen.ConfigScreen;
import com.chaos.iip.utils.Translatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Mod(InfoIsPower.MODID)
public class InfoIsPower {
    public static final KeyBinding keyBindOpenConfig = new KeyBinding(
            new Translatable("openConfig", Translatable.TranslateType.CONFIG).getString(),
            GLFW.GLFW_KEY_SEMICOLON,
            new Translatable(".name", Translatable.TranslateType.NONE).getString());

    static {
        ClientRegistry.registerKeyBinding(keyBindOpenConfig);
    }

    public static final String MODID = "info_is_power";
    private static final Logger LOGGER = LogManager.getLogger();

    public InfoIsPower() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ClientRenderEvent());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (keyBindOpenConfig.isPressed())
            Minecraft.getInstance().displayGuiScreen(new ConfigScreen());
    }
}
