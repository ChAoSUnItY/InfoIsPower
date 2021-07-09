package io.github.chaosunity.iip.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.fillGradient;

public class HudHelper {
    public static void renderTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y) {
        var mc = MinecraftClient.getInstance();
        var window = mc.getWindow();
        var tr = mc.textRenderer;
        int width = window.getScaledWidth(), height = window.getScaledHeight();

        if (!lines.isEmpty()) {
            var i = 0;

            for (OrderedText orderedText : lines) {
                var j = tr.getWidth(orderedText);

                if (j > i)
                    i = j;
            }

            var k = x + 12;
            var l = y - 12;
            var n = 8;

            if (lines.size() > 1)
                n += 2 + (lines.size() - 1) * 10;

            if (k + i > width)
                k -= 28 + i;

            if (l + n + 6 > height)
                l = height - n - 6;

            matrices.push();
            var tessellator = Tessellator.getInstance();
            var bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            var matrix4f = matrices.peek().getModel();
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
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            var immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrices.translate(0.0D, 0.0D, 400.0D);

            for (int s = 0; s < lines.size(); ++s) {
                var orderedText2 = lines.get(s);

                if (orderedText2 != null)
                    tr.draw(orderedText2, (float) k, (float) l, -1, true, matrix4f, immediate, false, 0, 15728880);

                if (s == 0)
                    l += 2;

                l += 10;
            }

            immediate.draw();
            matrices.pop();
        }
    }
}
