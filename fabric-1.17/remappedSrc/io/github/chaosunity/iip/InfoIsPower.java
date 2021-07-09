package io.github.chaosunity.iip;

import io.github.chaosunity.iip.util.DisplayMode;
import io.github.chaosunity.iip.util.IIPConfig;
import io.github.chaosunity.iip.util.RenderType;
import io.github.chaosunity.iip.util.Translatable;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class InfoIsPower implements ClientModInitializer {
    public static final String MODID = "info_is_power";
    public static IIPConfig config;
    public static KeyBinding configKeyBinding;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(IIPConfig.class, GsonConfigSerializer::new);

        config = AutoConfig.getConfigHolder(IIPConfig.class).getConfig();

        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(new Translatable("openConfig", Translatable.TranslateType.CONFIG).getKey(), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_SEMICOLON, new Translatable("name", Translatable.TranslateType.NONE).getString()));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeyBinding.isPressed())
                MinecraftClient.getInstance().openScreen(createConfigScreen(client));
        });
    }

    public static Screen createConfigScreen(MinecraftClient client) {
        return createConfigScreen(client.currentScreen);
    }

    public static Screen createConfigScreen(Screen parent) {
        IIPConfig config = InfoIsPower.config;
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new Translatable("name", Translatable.TranslateType.NONE));
        ConfigCategory cat = builder.getOrCreateCategory(new LiteralText("Info Is Power!"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        cat.addEntry(entryBuilder
                .startBooleanToggle(new LiteralText("Enable Render"), config.enableRender)
                .setDefaultValue(true)
                .setSaveConsumer(b -> InfoIsPower.config.enableRender = b)
                .build());
        cat.addEntry(entryBuilder
                .startEnumSelector(new LiteralText("Display Mode"), DisplayMode.class, config.displayMode)
                .setDefaultValue(DisplayMode.TOOLS)
                .setSaveConsumer(e -> InfoIsPower.config.displayMode = e)
                .build());
        cat.addEntry(entryBuilder
                .startEnumSelector(new LiteralText("Render Type"), RenderType.class, config.renderType)
                .setDefaultValue(RenderType.LEFT)
                .setSaveConsumer(e -> InfoIsPower.config.renderType = e)
                .build());

        Field[] f = IIPConfig.RenderFilter.class.getDeclaredFields();
        for (int i = 0; i < f.length; i++)
            try {
                int finalI = i;
                cat.addEntry(entryBuilder
                        .startBooleanToggle(new Translatable(f[i].getName(), Translatable.TranslateType.CONFIG), f[i].getBoolean(config.filter))
                        .setDefaultValue(true)
                        .setSaveConsumer(b -> InfoIsPower.config.filter.setBool(b, finalI))
                        .build());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        builder.setSavingRunnable(() -> AutoConfig.getConfigHolder(IIPConfig.class).save());

        return builder.build();
    }
}
