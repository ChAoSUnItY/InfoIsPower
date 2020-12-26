package com.chaos.iip;

import com.chaos.iip.util.IIPConfig;
import com.chaos.iip.util.RenderType;
import com.chaos.iip.util.Translatable;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class InfoIsPower implements ClientModInitializer {
    public static final String MODID = "info_is_power";
    public static final String NAME = "Info Is Power!";

    public static IIPConfig config;

    public static KeyBinding configKeyBinding;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(IIPConfig.class, GsonConfigSerializer::new);

        config = AutoConfig.getConfigHolder(IIPConfig.class).getConfig();

        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(new Translatable("openConfig", Translatable.TranslateType.CONFIG).getKey(), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_SEMICOLON, new Translatable("name", Translatable.TranslateType.NONE).getString()));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeyBinding.isPressed()) {
                IIPConfig config = InfoIsPower.config;
                ConfigBuilder builder = ConfigBuilder.create().setParentScreen(client.currentScreen).setTitle(new Translatable("name", Translatable.TranslateType.NONE));
                ConfigCategory cat = builder.getOrCreateCategory(new LiteralText("Info Is Power!"));
                ConfigEntryBuilder entryBuilder = builder.entryBuilder();
                cat.addEntry(entryBuilder
                        .startBooleanToggle(new LiteralText("Enable Render"), config.enableRender)
                        .setDefaultValue(true)
                        .setSaveConsumer(b -> InfoIsPower.config.enableRender = b)
                        .build());
                cat.addEntry(entryBuilder
                        .startIntField(new LiteralText("Display Mode"), 1)
                        .setDefaultValue(1)
                        .setMin(1)
                        .setMax(3)
                        .setSaveConsumer(i -> InfoIsPower.config.displayMode = i)
                        .build());
                cat.addEntry(entryBuilder
                        .startEnumSelector(new LiteralText("Render Type"), RenderType.class, RenderType.LEFT)
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

                MinecraftClient.getInstance().openScreen(builder.build());
            }
        });
    }
}
