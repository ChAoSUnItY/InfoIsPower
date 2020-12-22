package com.chaos.iip;

import com.chaos.iip.utils.DisplayContentHelper;
import com.chaos.iip.utils.GUIElementLocator;
import com.chaos.iip.utils.RenderLocationType;
import com.chaos.iip.utils.Translatable;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.text.DecimalFormat;
import java.util.List;

@Mod.EventBusSubscriber(modid = InfoIsPower.MODID, value = Dist.CLIENT)
public class ClientRenderEvent {
    private static final int color = 16777215;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (!Config.CLIENT.enableRender.get())
            return;

        MatrixStack matrix = event.getMatrixStack();
        Minecraft mc = Minecraft.getInstance();
        World world = mc.getConnection().getWorld();
        FontRenderer font = mc.fontRenderer;
        GUIElementLocator locator = GUIElementLocator.getInstance();
        PlayerEntity player = mc.player;
        BlockPos pos = player.getPosition();

        // Main Render
        if (!mc.gameSettings.showDebugInfo) {
            // FPS and biome
            if (shouldRender("FPS")) {
                int fps = 0;
                try {
                    fps = ObfuscationReflectionHelper.findField(Minecraft.class, "field_71470_ab").getInt(mc);
                } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                drawString(matrix, font, new Translatable("fps", null, fps).getString(), 2, locator.begin(GUIElementLocator.LocatorTypes.LEFT_UP, mc).getCurrent(), color);
            }

            if (shouldRender("Biome"))
                drawString(matrix, font, new Translatable("biome", null, new TranslationTextComponent(Util.makeTranslationKey("biome", world.getBiome(pos).getRegistryName())).getString()).getString(), 2, locator
                        .getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), color);

            // Survival mode only stats, like health, hunger, and armor
            if (!player.isCreative()) {
                if (shouldRender("Health")) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    drawString(matrix, font, new Translatable("health", null, df.format(player.getHealth())).appendString(" / " + player.getMaxHealth())
                            .getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), (player.getHealth() > 10.0 ? TextFormatting.GREEN : (player.getHealth() > 5.0 ? TextFormatting.YELLOW : TextFormatting.RED)).getColor());
                }

                if (shouldRender("Hunger")) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    drawString(matrix, font, new Translatable("hunger", null, player.getFoodStats().getFoodLevel(), df.format(player.getFoodStats().getSaturationLevel()))
                            .getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), TextFormatting.GOLD.getColor());
                }

                if (shouldRender("Armor"))
                    drawString(matrix, font, new Translatable("armor", null, player.getTotalArmorValue()).getString(), 2, locator
                            .getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), TextFormatting.GRAY.getColor());
            }

            if (shouldRender("Position"))
                drawString(matrix, font, new Translatable("pos", null, pos.getX(), pos.getY(), pos.getZ()).getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), color);

            // Held items and equipments
            locator.begin(GUIElementLocator.LocatorTypes.LEFT_CENTER, mc);
            switch (Config.CLIENT.mode.get()) {
                case 1:
                    if (shouldRender("Equipment")) {
                        (Lists.reverse((List<ItemStack>) player.getArmorInventoryList())).forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfo(matrix, mc, stack, locator, true);
                        });
                        locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                    }
                    if (shouldRender("HeldItems"))
                        player.getHeldEquipment().forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfo(matrix, mc, stack, locator, false);
                            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                        });
                    break;
                case 2:
                    if (shouldRender("Equipment")) {
                        (Lists.reverse((List<ItemStack>) player.getArmorInventoryList())).forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfo(matrix, mc, stack, locator, false);
                            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                        });
                        locator.returnCounter(1, GUIElementLocator.LocatorGapTypes.ITEM);
                    }
                    if (shouldRender("HeldItems"))
                        player.getHeldEquipment().forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfo(matrix, mc, stack, locator, true);
                        });
                    break;
                default:
                    if (shouldRender("Equipment"))
                        (Lists.reverse((List<ItemStack>) player.getArmorInventoryList())).forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfo(matrix, mc, stack, locator, true);
                        });

                    if (shouldRender("HeldItems"))
                        player.getHeldEquipment().forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfo(matrix, mc, stack, locator, true);
                        });
            }
        }
        locator.end();
    }

    private static void drawString(MatrixStack matrix, FontRenderer font, String text, int x, int y, int color) {
        font.drawStringWithShadow(
                matrix,
                text,
                getComputedXbyString(font, x, text),
                y,
                color
        );
    }

    private static void drawItem(FontRenderer font, ItemRenderer renderer, ItemStack stack, int x, int y) {
        renderer.renderItemAndEffectIntoGUI(
                stack,
                getComputedXbyItem(x),
                y);
        renderer.renderItemOverlayIntoGUI(
                font,
                stack,
                getComputedXbyItem(x),
                y,
                null);
    }

    private static void getAllInfo(MatrixStack matrix, Minecraft mc, ItemStack stack, GUIElementLocator locator, boolean simple) {
        ItemRenderer renderer = mc.getItemRenderer();
        FontRenderer font = mc.fontRenderer;
        String s = stack.getTextComponent().getString().replaceAll("[\\[\\]]", "");
        List<ITextComponent> texts = Lists.newArrayList();
        Item item = stack.getItem();

        if (simple) {
            drawItem(font, renderer, stack, 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM));
            drawString(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().color.getColor());
            return;
        }

        int times = 0;

        if (stack.isDamageable())
            texts.add(new Translatable("durability", null, (stack.getMaxDamage() - stack.getDamage()), stack.getMaxDamage()).mergeStyle(getDurabilityFormat(stack)));

        if (item.isFood()) {
            Food food = item.getFood();
            food.getEffects().forEach(p -> {
                TextFormatting formatting;
                switch (p.getFirst().getPotion().getEffectType()) {
                    case BENEFICIAL:
                        formatting = TextFormatting.BLUE;
                        break;
                    case NEUTRAL:
                        formatting = TextFormatting.GRAY;
                        break;
                    case HARMFUL:
                    default:
                        formatting = TextFormatting.RED;
                }
                texts.add(new TranslationTextComponent(p.getFirst().getEffectName())
                        .appendString(
                                String.format(
                                        "(%d:%02d)",
                                        p.getFirst().getDuration() / 20 / 60,
                                        p.getFirst().getDuration() / 20 % 60
                                ))
                        .mergeStyle(formatting));
            });
            texts.add(DisplayContentHelper.subContents[0].deepCopy().appendString(" : +" + food.getHealing()).mergeStyle(TextFormatting.GOLD));
            texts.add(DisplayContentHelper.subContents[1].deepCopy().appendString(" : +" + food.getSaturation()).mergeStyle(TextFormatting.GOLD));
        }

        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            Block blk = Block.getBlockFromItem(item);
            if (stack.getEnchantmentTagList().size() > 0) {
                EnchantmentHelper.getEnchantments(stack).entrySet().forEach(enchantment ->
                        texts.add(enchantment.getKey().getDisplayName(enchantment.getValue())
                                .deepCopy()
                                .mergeStyle(
                                        enchantment.getKey().isCurse() ? TextFormatting.RED :
                                                (enchantment.getKey().getMaxLevel() == enchantment.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN))));
            } else if (blk instanceof BeehiveBlock) {
                int bees = tag.getCompound("BlockEntityTag").getList("Bees", 10).size();
                // texts.add(new Translatable("honeyLevel", null, infos[0]).mergeStyle(infos[0] == 5 ? TextFormatting.GOLD : TextFormatting.GREEN)); <- NO LONGER SAVES BLOCKSTATE TAG IN AN ITEM, SO HONEY LEVEL IS NO LONGER AVAILABLE TO SCRAP.
                texts.add(new Translatable("bees", null, bees).mergeStyle(bees == 3 ? TextFormatting.GOLD : TextFormatting.GREEN));
            } else if (blk instanceof ShulkerBoxBlock || blk instanceof ChestBlock || blk instanceof BarrelBlock || blk instanceof FurnaceBlock || blk instanceof SmokerBlock) {
                ListNBT list = tag.getCompound("BlockEntityTag").getList("Items", 10);
                drawItem(font, renderer, stack, 2, locator.getCurrent());
                drawString(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().color.getColor());
                for (int i = 0, p = locator.getCurrent() + 20; i < list.size(); i++) {
                    if (i % 9 == 0 && i != 0) {
                        p += 20;
                        times++;
                    }
                    drawItem(font, renderer, ItemStack.read((CompoundNBT) list.get(i)), Config.CLIENT.renderType.get() == RenderLocationType.LEFT ? 2 + (i % 9) * 20 : 165 - (2 + (i % 9) * 20), p - 3);
                }
                for (int j = 0; j < times + 1; j++)
                    locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                return;
            } else if (item instanceof FilledMapItem) {
                int type = -1, target = -1;
                ListNBT list = tag.getList("Decorations", 10);
                for (int i = 0; i < list.size(); i++) {
                    byte testType = list.getCompound(i).getByte("type");
                    if (testType == 8 || testType == 9 || testType == 26) {
                        type = testType;
                        target = i;
                    }
                }
                if (type == -1) {
                    texts.add(new Translatable("normalTotal", null, list.size()).mergeStyle(list.size() > 1 ? TextFormatting.GREEN : TextFormatting.GRAY));
                } else {
                    CompoundNBT nbt = list.getCompound(target);
                    texts.add(new Translatable("explorerPos", null, nbt.getInt("x"), nbt.getInt("z"))
                            .mergeStyle(type == 8 ? TextFormatting.GREEN : (type == 9 ? TextFormatting.AQUA : TextFormatting.GOLD)));
                }
            } else if (item instanceof EnchantedBookItem) {
                EnchantmentHelper.getEnchantments(stack).entrySet().forEach(enchantment ->
                        texts.add(enchantment.getKey().getDisplayName(enchantment.getValue())
                                .deepCopy()
                                .mergeStyle(enchantment.getKey().getMaxLevel() == enchantment.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN)));
            } else if (item instanceof PotionItem || item instanceof SpectralArrowItem || item instanceof TippedArrowItem) {
//                List<ITextComponent> tooltip = stack.getTooltip(mc.player, TooltipFlags.NORMAL);
//                tooltip.remove(2);
//                tooltip.remove(1);
//                texts.addAll(tooltip);
            }
        }

        drawItem(font, renderer, stack, 2, locator.getCurrent());
        drawString(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().color.getColor());
        if (texts.isEmpty())
            return;
        locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT);
        GuiUtils.drawHoveringText(matrix, texts, Config.CLIENT.renderType.get() == RenderLocationType.LEFT ? -7 : Minecraft.getInstance().getMainWindow().getScaledWidth() + 11, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM) + 3, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), -1, font);
        for (int i = 0; i < texts.size() - 2; i++)
            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TOOLTIP_TEXT);
    }

    private static TextFormatting getDurabilityFormat(ItemStack stack) {
        float max = (float) stack.getMaxDamage(), cur = (float) stack.getDamage();
        float percentage = (max - cur) / max;
        if (percentage >= 0.75F)
            return TextFormatting.GREEN;
        else if (percentage < 0.75F && percentage >= 0.5F)
            return TextFormatting.DARK_GREEN;
        else if (percentage < 0.5F && percentage >= 0.25F)
            return TextFormatting.YELLOW;
        else if (percentage < 0.25F && percentage >= 0.1F)
            return TextFormatting.RED;
        else if (percentage < 0.1F)
            return TextFormatting.DARK_RED;
        else
            return TextFormatting.GRAY;
    }

    private static boolean shouldRender(String s) {
        return Config.shouldRender(s);
    }

    private static RenderLocationType getRenderType() {
        return Config.CLIENT.renderType.get();
    }

    private static int getComputedXbyString(FontRenderer font, int x, String text) {
        return getRenderType() == RenderLocationType.LEFT ? x : Minecraft.getInstance().getMainWindow().getScaledWidth() - x - font.getStringWidth(text);
    }

    private static int getComputedXbyItem(int x) {
        return getRenderType() == RenderLocationType.LEFT ? x : Minecraft.getInstance().getMainWindow().getScaledWidth() - x - 15;
    }
}
