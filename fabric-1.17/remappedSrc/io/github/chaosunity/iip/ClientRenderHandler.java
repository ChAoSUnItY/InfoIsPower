package io.github.chaosunity.iip;

import com.google.common.collect.Lists;
import io.github.chaosunity.iip.util.*;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.util.List;

public class ClientRenderHandler {
    public static final ClientRenderHandler INSTANCE = new ClientRenderHandler();
    public final int color = Formatting.WHITE.getColorValue();

    public void render(final MatrixStack matrix) {
        if (!InfoIsPower.config.enableRender)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
//      World world = mc.getNetworkHandler().getWorld();
        TextRenderer tr = mc.textRenderer;
        GUIElementLocator locator = GUIElementLocator.getInstance();
        PlayerEntity player = mc.player;
        BlockPos pos = player.getBlockPos();

        // Main Render
        if (!mc.options.debugEnabled) {
            // FPS and biome
            if (shouldRender(0))
                drawString(matrix, tr, new Translatable("fps", null, MinecraftClient.currentFps).getString(), 2, locator.begin(GUIElementLocator.LocatorTypes.LEFT_UP, mc).getCurrent(), color);

//            if (shouldRender("Biome"))
//                drawString(matrix, tr, new Translatable("biome", null, new TranslatableText(Util.createTranslationKey("biome", Registry.BIOME_KEY.getValue())).getString(), 2, locator
//                        .getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), color);

            // Survival mode only stats, like health, hunger, and armor
            if (!player.isCreative()) {
                if (shouldRender(2)) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    drawString(matrix, tr, new Translatable("health", null, df.format(player.getHealth())).append(" / " + player.getMaxHealth())
                            .getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), (player.getHealth() > 10.0 ? Formatting.GREEN : (player.getHealth() > 5.0 ? Formatting.YELLOW : Formatting.RED)).getColorValue());
                }

                if (shouldRender(3)) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    drawString(matrix, tr, new Translatable("hunger", null, player.getHungerManager().getFoodLevel(), df.format(player.getHungerManager().getSaturationLevel()))
                            .getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), Formatting.GOLD.getColorValue());
                }

                if (shouldRender(4))
                    drawString(matrix, tr, new Translatable("armor", null, player.getArmor()).getString(), 2, locator
                            .getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), Formatting.GRAY.getColorValue());
            }

            if (shouldRender(5))
                drawString(matrix, tr, new Translatable("pos", null, pos.getX(), pos.getY(), pos.getZ()).getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), color);

            // Held items and equipments
            locator.begin(GUIElementLocator.LocatorTypes.LEFT_CENTER, mc);
            switch (InfoIsPower.config.displayMode) {
                case TOOLS -> {
                    if (shouldRender(6)) {
                        (Lists.reverse((List<ItemStack>) player.getArmorItems())).forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfo(matrix, mc, stack, locator, true);
                        });
                        locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                    }
                    if (shouldRender(7))
                        player.getItemsHand().forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfo(matrix, mc, stack, locator, false);
                            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                        });
                }
                case ARMOR -> {
                    if (shouldRender(6)) {
                        (Lists.reverse((List<ItemStack>) player.getArmorItems())).forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfo(matrix, mc, stack, locator, false);
                            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                        });
                        locator.returnCounter(1, GUIElementLocator.LocatorGapTypes.ITEM);
                    }
                    if (shouldRender(7))
                        player.getItemsHand().forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfo(matrix, mc, stack, locator, true);
                        });
                }
                case SIMPLIFIED -> {
                    if (shouldRender(6))
                        (Lists.reverse((List<ItemStack>) player.getArmorItems())).forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfo(matrix, mc, stack, locator, true);
                        });
                    if (shouldRender(7))
                        player.getItemsHand().forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfo(matrix, mc, stack, locator, true);
                        });
                }
            }
        }
        locator.end();
    }

    private void drawString(MatrixStack matrix, TextRenderer tr, String text, int x, int y, int color) {
        tr.drawWithShadow(
                matrix,
                text,
                getComputedXbyString(tr, x, text),
                y,
                color
        );
    }

    private void drawItem(TextRenderer tr, ItemRenderer renderer, ItemStack stack, int x, int y) {
        renderer.renderInGui(
                stack,
                getComputedXbyItem(x),
                y);
        renderer.renderGuiItemOverlay(
                tr,
                stack,
                getComputedXbyItem(x),
                y,
                null);
    }

    private void getAllInfo(MatrixStack matrix, MinecraftClient mc, ItemStack stack, GUIElementLocator locator, boolean simple) {
        ItemRenderer renderer = mc.getItemRenderer();
        TextRenderer font = mc.textRenderer;
        String s = stack.getName().getString().replaceAll("[\\[\\]]", "");
        List<Text> texts = Lists.newArrayList();
        Item item = stack.getItem();

        if (simple) {
            drawItem(font, renderer, stack, 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM));
            drawString(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().formatting.getColorValue());
            return;
        }

        int times = 0;

        if (stack.isDamageable())
            texts.add(new Translatable("durability", null, (stack.getMaxDamage() - stack.getDamage()), stack.getMaxDamage()).formatted(getDurabilityFormat(stack)));

        if (item.isFood()) {
            FoodComponent food = item.getFoodComponent();
            food.getStatusEffects().forEach(p -> {
                Formatting formatting;
                switch (p.getFirst().getEffectType().getType()) {
                    case BENEFICIAL:
                        formatting = Formatting.BLUE;
                        break;
                    case NEUTRAL:
                        formatting = Formatting.GRAY;
                        break;
                    case HARMFUL:
                    default:
                        formatting = Formatting.RED;
                }
                texts.add(new TranslatableText(p.getFirst().getTranslationKey())
                        .append(
                                String.format(
                                        "(%d:%02d)",
                                        p.getFirst().getDuration() / 20 / 60,
                                        p.getFirst().getDuration() / 20 % 60
                                ))
                        .formatted(formatting));
            });
            texts.add(DisplayContentHelper.subContents[0].shallowCopy().append(" : +" + food.getHunger()).formatted(Formatting.GOLD));
            texts.add(DisplayContentHelper.subContents[1].shallowCopy().append(" : +" + food.getSaturationModifier()).formatted(Formatting.GOLD));
        }

        if (stack.hasTag()) {
            NbtCompound tag = stack.getTag();
            Block blk = Block.getBlockFromItem(item);
            if (stack.getEnchantments().size() > 0) {
                EnchantmentHelper.get(stack).forEach((key, value) ->
                        texts.add(key.getName(value)
                                .shallowCopy()
                                .formatted(
                                        key.isCursed() ? Formatting.RED :
                                                (key.getMaxLevel() == value ? Formatting.GOLD : Formatting.GREEN))));
            } else if (blk instanceof BeehiveBlock) {
                int bees = tag.getCompound("BlockEntityTag").getList("Bees", 10).size();
                // texts.add(new Translatable("honeyLevel", null, infos[0]).mergeStyle(infos[0] == 5 ? TextFormatting.GOLD : TextFormatting.GREEN)); <- NO LONGER SAVES BLOCKSTATE TAG IN AN ITEM, SO HONEY LEVEL IS NO LONGER AVAILABLE TO SCRAP.
                texts.add(new Translatable("bees", null, bees).formatted(bees == 3 ? Formatting.GOLD : Formatting.GREEN));
            } else if (blk instanceof ShulkerBoxBlock || blk instanceof ChestBlock || blk instanceof BarrelBlock || blk instanceof FurnaceBlock || blk instanceof SmokerBlock) {
                NbtList list = tag.getCompound("BlockEntityTag").getList("Items", 10);
                drawItem(font, renderer, stack, 2, locator.getCurrent());
                drawString(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().formatting.getColorValue());
                for (int i = 0, p = locator.getCurrent() + 20; i < list.size(); i++) {
                    if (i % 9 == 0 && i != 0) {
                        p += 20;
                        times++;
                    }
                    drawItem(font, renderer, ItemStack.fromNbt((NbtCompound) list.get(i)), InfoIsPower.config.renderType == RenderType.LEFT ? 2 + (i % 9) * 20 : 165 - (2 + (i % 9) * 20), p - 3);
                }
                for (int j = 0; j < times + 1; j++)
                    locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                return;
            } else if (item instanceof FilledMapItem) {
                int type = -1, target = -1;
                NbtList list = tag.getList("Decorations", 10);
                for (int i = 0; i < list.size(); i++) {
                    byte testType = list.getCompound(i).getByte("type");
                    if (testType == 8 || testType == 9 || testType == 26) {
                        type = testType;
                        target = i;
                    }
                }
                if (type == -1) {
                    texts.add(new Translatable("normalTotal", null, list.size()).formatted(list.size() > 1 ? Formatting.GREEN : Formatting.GRAY));
                } else {
                    NbtCompound nbt = list.getCompound(target);
                    texts.add(new Translatable("explorerPos", null, nbt.getInt("x"), nbt.getInt("z"))
                            .formatted(type == 8 ? Formatting.GREEN : (type == 9 ? Formatting.AQUA : Formatting.GOLD)));
                }
            } else if (item instanceof EnchantedBookItem) {
                EnchantmentHelper.get(stack).forEach((key, value) -> texts.add(key.getName(value)
                        .shallowCopy()
                        .formatted(key.getMaxLevel() == value ? Formatting.GOLD : Formatting.GREEN)));
            } else if (item instanceof PotionItem || item instanceof SpectralArrowItem || item instanceof TippedArrowItem) {
//                List<ITextComponent> tooltip = stack.getTooltip(mc.player, TooltipFlags.NORMAL);
//                tooltip.remove(2);
//                tooltip.remove(1);
//                texts.addAll(tooltip);
            }
        }

        drawItem(font, renderer, stack, 2, locator.getCurrent());
        drawString(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().formatting.getColorValue());
        if (texts.isEmpty())
            return;
        locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT);
        mc.currentScreen.renderTooltip(matrix, texts, InfoIsPower.config.renderType == RenderType.LEFT ? -7 : mc.getWindow().getScaledWidth() + 11, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM) + 3);
        for (int i = 0; i < texts.size() - 2; i++)
            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TOOLTIP_TEXT);
    }

    private Formatting getDurabilityFormat(ItemStack stack) {
        float max = (float) stack.getMaxDamage(), cur = (float) stack.getDamage();
        float percentage = (max - cur) / max;
        if (percentage >= 0.75F)
            return Formatting.GREEN;
        else if (percentage < 0.75F && percentage >= 0.5F)
            return Formatting.DARK_GREEN;
        else if (percentage < 0.5F && percentage >= 0.25F)
            return Formatting.YELLOW;
        else if (percentage < 0.25F && percentage >= 0.1F)
            return Formatting.RED;
        else if (percentage < 0.1F)
            return Formatting.DARK_RED;
        else
            return Formatting.GRAY;
    }

    private boolean shouldRender(int index) {
        boolean b = false;
        try {
            b = IIPConfig.RenderFilter.class.getDeclaredFields()[index].getBoolean(InfoIsPower.config.filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private RenderType getRenderType() {
        return InfoIsPower.config.renderType;
    }

    private int getComputedXbyString(TextRenderer tr, int x, String text) {
        return getRenderType() == RenderType.LEFT ? x : MinecraftClient.getInstance().getWindow().getScaledWidth() - x - tr.getWidth(text);
    }

    private int getComputedXbyItem(int x) {
        return getRenderType() == RenderType.LEFT ? x : MinecraftClient.getInstance().getWindow().getScaledWidth() - x - 15;
    }
}
