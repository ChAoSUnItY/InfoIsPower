package com.chaos.iip;

import com.chaos.iip.utils.GUIElementLocator;
import com.chaos.iip.utils.Translatable;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
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
import java.util.*;

@Mod.EventBusSubscriber(modid = InfoIsPower.MODID, value = Dist.CLIENT)
public class EventHandler {
    private static final int color = 16777215;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        MatrixStack matrix = event.getMatrixStack();
        Minecraft mc = Minecraft.getInstance();
        World world = mc.getConnection().getWorld();
        FontRenderer font = mc.fontRenderer;
        GUIElementLocator locator = GUIElementLocator.getInstance();
        DecimalFormat df = new DecimalFormat("0.00");
        int fps = 0;
        try {
            fps = ObfuscationReflectionHelper.findField(Minecraft.class, "field_71470_ab").getInt(mc);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        PlayerEntity player = mc.player;
        BlockPos pos = player.getPosition();
        if (!mc.gameSettings.showDebugInfo) {
            // ============================BASE MESSAGES PART============================//
            simpleStringDraw(matrix, font, new Translatable("fps", null, fps).getString(), 2, locator.begin(GUIElementLocator.LocatorTypes.LEFT_UP, mc).getCurrent(), color);
            simpleStringDraw(matrix, font, new Translatable("biome", null, new TranslationTextComponent(Util.makeTranslationKey("biome", world.getBiome(pos).getRegistryName())).getString()).getString(), 2, locator
                    .getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), color);
            if (!player.isCreative()) {
                simpleStringDraw(matrix, font, new Translatable("health", null, df.format(player.getHealth()))
                        .getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), (player.getHealth() > 10.0 ? TextFormatting.GREEN : (player.getHealth() > 5.0 ? TextFormatting.YELLOW : TextFormatting.RED)).getColor());
                simpleStringDraw(matrix, font, new Translatable("hunger", null, player.getFoodStats().getFoodLevel(), df.format(player.getFoodStats().getSaturationLevel()))
                        .getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), TextFormatting.GOLD.getColor());
                simpleStringDraw(matrix, font, new Translatable("armor", null, player.getTotalArmorValue()).getString(), 2, locator
                        .getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), TextFormatting.GRAY.getColor());
            }
            // ============================POS & PLAYERS PART============================//
            simpleStringDraw(matrix, font, new Translatable("pos", null, pos.getX(), pos.getY(), pos.getZ()).getString(), 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT), color);
            // ============================HELD ITEM PART============================//
            if (mc.isChatEnabled()) {
                locator.begin(GUIElementLocator.LocatorTypes.LEFT_CENTER, mc);
                EquipmentSlotType[] types = EquipmentSlotType.values();
                Iterator<EquipmentSlotType> handType = Arrays.asList(new EquipmentSlotType[]{types[0], types[1]}).iterator();
                Iterator<EquipmentSlotType> armorType = Arrays.asList(new EquipmentSlotType[]{types[5], types[4], types[3], types[2]}).iterator();
                switch (Config.CLIENT.mode.get()) {
                    case 1:
                        (Lists.reverse((List<ItemStack>) player.getArmorInventoryList())).forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfos(matrix, mc, stack, locator, true);
                        });
                        locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                        player.getHeldEquipment().forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfos(matrix, mc, stack, locator, false);
                            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                        });
                        break;
                    case 2:
                        (Lists.reverse((List<ItemStack>) player.getArmorInventoryList())).forEach(stack -> {
                            if (stack.isEmpty())
                                return;
                            getAllInfos(matrix, mc, stack, locator, false);
                            locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM);
                        });
                        locator.returnCounter(1, GUIElementLocator.LocatorGapTypes.ITEM);
                        player.getHeldEquipment().forEach(stack -> {
                            if (!stack.isEmpty())
                                getAllInfos(matrix, mc, stack, locator, true);
                        });
                        break;
                    default:
                        break;
                }
            }
            locator.end();
        }
    }

    private static void simpleStringDraw(MatrixStack matrix, FontRenderer font, String text, int x, int y, int color) {
        font.drawStringWithShadow(matrix, text, x, y, color);
    }

    private static void simpleItemDraw(FontRenderer font, ItemRenderer renderer, ItemStack stack, int x, int y) {
        renderer.renderItemAndEffectIntoGUI(stack, x, y);
        renderer.renderItemOverlayIntoGUI(font, stack, x, y, null);
    }

    private static void getAllInfos(MatrixStack matrix, Minecraft mc, ItemStack stack, GUIElementLocator locator, boolean simple) {
        ItemRenderer renderer = mc.getItemRenderer();
        FontRenderer font = mc.fontRenderer;
        String s = stack.getTextComponent().getString().replaceAll("[\\[\\]]", "");
        List<ITextComponent> texts = new ArrayList<>();
        Item item = stack.getItem();
        if (simple) {
            simpleItemDraw(font, renderer, stack, 2, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM));
            simpleStringDraw(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().color.getColor());
            return;
        }

        int times = 0;
        if (stack.isDamageable())
            texts.add(new Translatable("durability", null, (stack.getMaxDamage() - stack.getDamage()), stack.getMaxDamage()).mergeStyle(getDurabilityFormat(stack)));
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            Block blk = Block.getBlockFromItem(item);
            if (stack.getEnchantmentTagList().size() > 0) {
                Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
                for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet())
                    texts.add(ench.getKey().getDisplayName(ench.getValue()).deepCopy().mergeStyle(ench.getKey().isCurse() ? TextFormatting.RED : (ench.getKey().getMaxLevel() == ench.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN)));
            } else if (blk instanceof BeehiveBlock) {
                int infos[] = {tag.getCompound("BlockStateTag").getInt("honey_level"), tag.getCompound("BlockEntityTag").getList("Bees", 10).size()};
                texts.add(new Translatable("honeyLevel", null, infos[0]).mergeStyle(infos[0] == 5 ? TextFormatting.GOLD : TextFormatting.GREEN));
                texts.add(new Translatable("bees", null, infos[1]).mergeStyle(infos[1] == 3 ? TextFormatting.GOLD : TextFormatting.GREEN));
            } else if (blk instanceof ShulkerBoxBlock) {
                ListNBT list = tag.getCompound("BlockEntityTag").getList("Items", 10);
                simpleItemDraw(font, renderer, stack, 2, locator.getCurrent());
                simpleStringDraw(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().color.getColor());
                for (int i = 0, p = locator.getCurrent() + 20; i < list.size(); i++) {
                    if (i % 9 == 0 && i != 0) {
                        p += 20;
                        times++;
                    }
                    simpleItemDraw(font, renderer, ItemStack.read((CompoundNBT) list.get(i)), 2 + (i % 9) * 20, p - 3);
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
                Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
                for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet())
                    texts.add(ench.getKey().getDisplayName(ench.getValue()).deepCopy().mergeStyle(ench.getKey().getMaxLevel() == ench.getValue() ? TextFormatting.GOLD : TextFormatting.GREEN));
            } else if (item instanceof PotionItem || item instanceof SpectralArrowItem || item instanceof TippedArrowItem) {
                List<ITextComponent> tooltip = stack.getTooltip(mc.player, TooltipFlags.NORMAL);
                tooltip.remove(2);
                tooltip.remove(1);
                texts.addAll(tooltip);
            }
        }
        simpleItemDraw(font, renderer, stack, 2, locator.getCurrent());
        simpleStringDraw(matrix, font, s, 20, locator.getCurrent(), stack.getRarity().color.getColor());
        if (texts.isEmpty())
            return;
        locator.getNextLocation(GUIElementLocator.LocatorGapTypes.TEXT);
        GuiUtils.drawHoveringText(matrix, texts, -7, locator.getNextLocation(GUIElementLocator.LocatorGapTypes.ITEM) + 3, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), -1, font);
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
}
