package com.chaos.iip.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

import java.util.List;

public class GuiUtils extends DrawableHelper {
    public static void renderTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = mc.getWindow();
        TextRenderer tr = mc.textRenderer;
        int width = window.getScaledWidth(), height = window.getScaledHeight();
        
        if (!lines.isEmpty()) {
            int i = 0;

            for (OrderedText orderedText : lines) {
                int j = tr.getWidth(orderedText);
                if (j > i)
                    i = j;
            }

            int k = x + 12;
            int l = y - 12;
            int n = 8;
            if (lines.size() > 1)
                n += 2 + (lines.size() - 1) * 10;

            if (k + i > width)
                k -= 28 + i;

            if (l + n + 6 > height)
                l = height - n - 6;

            matrices.push();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
            Matrix4f matrix4f = matrices.peek().getModel();
            fillGradient(matrix4f, bufferBuilder, k - 3, l - 4, k + i + 3, l - 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, k - 3, l + n + 3, k + i + 3, l + n + 4, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, k - 3, l - 3, k + i + 3, l + n + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, k - 4, l - 3, k - 3, l + n + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, k + i + 3, l - 3, k + i + 4, l + n + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, k - 3, l - 3 + 1, k - 3 + 1, l + n + 3 - 1, 400, 1347420415, 1344798847);
            fillGradient(matrix4f, bufferBuilder, k + i + 2, l - 3 + 1, k + i + 3, l + n + 3 - 1, 400, 1347420415, 1344798847);
            fillGradient(matrix4f, bufferBuilder, k - 3, l - 3, k + i + 3, l - 3 + 1, 400, 1347420415, 1347420415);
            fillGradient(matrix4f, bufferBuilder, k - 3, l + n + 2, k + i + 3, l + n + 3, 400, 1344798847, 1344798847);
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel(7425);
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.shadeModel(7424);
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrices.translate(0.0D, 0.0D, 400.0D);

            for(int s = 0; s < lines.size(); ++s) {
                OrderedText orderedText2 = lines.get(s);
                if (orderedText2 != null)
                    tr.draw(orderedText2, (float)k, (float)l, -1, true, matrix4f, immediate, false, 0, 15728880);

                if (s == 0)
                    l += 2;

                l += 10;
            }

            immediate.draw();
            matrices.pop();
        }
    }
}
