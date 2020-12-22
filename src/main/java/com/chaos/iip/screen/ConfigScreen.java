package com.chaos.iip.screen;

import com.chaos.iip.Config;
import com.chaos.iip.utils.DisplayContentHelper;
import com.chaos.iip.utils.RenderLocationType;
import com.chaos.iip.utils.Translatable;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class ConfigScreen extends Screen {
    private CheckboxButton[] renderModesCheckBoxes = new CheckboxButton[Config.Client.acceptableRenderValues.size()];
    private int displayMode = Config.CLIENT.mode.get();
    private RenderLocationType type = Config.CLIENT.renderType.get();


    public ConfigScreen() {
        super(StringTextComponent.EMPTY);
    }

    @Override
    protected void init() {
        int x = width / 2, y = height / 2;
        addButton(new Button(
                x - 105,
                y - 75,
                100,
                20,
                new Translatable("displayMode.screen", Translatable.TranslateType.CONFIG)
                        .appendString(String.valueOf(displayMode)),
                v -> displayMode = (displayMode == 3 ? 1 : ++displayMode)
        ) {
            @Override
            public ITextComponent getMessage() {
                return new Translatable("displayMode.screen", Translatable.TranslateType.CONFIG)
                        .appendString(String.valueOf(displayMode));
            }
        });

        addButton(new Button(
                x + 5,
                y - 75,
                100,
                20,
                new Translatable("renderMode.screen", Translatable.TranslateType.CONFIG)
                        .appendString(type.name()),
                v -> type = type.next()
        ) {
            @Override
            public ITextComponent getMessage() {
                return new Translatable("renderMode.screen", Translatable.TranslateType.CONFIG)
                        .appendString(type.name());
            }
        });

        for (int i = 0; i < Config.Client.acceptableRenderValues.size(); i++)
            renderModesCheckBoxes[i] = addButton(new CheckboxButton(
                    i < 4 ? x - 105 : x + 5,
                    y - 50 + (i < 4 ? i : i - 4) * 25,
                    50,
                    20,
                    DisplayContentHelper.contents[i],
                    Config.CLIENT.renderModes.get().contains(Config.Client.acceptableRenderValues.get(i))
            ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        List<String> s = Lists.newArrayList();
        for (int i = 0; i < renderModesCheckBoxes.length; i++)
            if (renderModesCheckBoxes[i].isChecked())
                s.add(Config.Client.acceptableRenderValues.get(i));
        Config.CLIENT.renderModes.set(s);
        Config.CLIENT.mode.set(displayMode);
        Config.CLIENT.renderType.set(type);
        Config.CLIENT_SPEC.save();
    }
}
