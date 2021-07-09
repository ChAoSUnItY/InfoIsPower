package io.github.chaosunity.iip.util;

import io.github.chaosunity.iip.InfoIsPower;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringIdentifiable;

public class Translatable extends TranslatableText {
    /**
     * Lazy translate, default type is OVERLAY.
     *
     * @param translationKey
     * @param type
     * @param args
     */
    public Translatable(String translationKey, TranslateType type, Object... args) {
        super(InfoIsPower.MODID + (type == null ? TranslateType.OVERLAY.asString() : type.asString()) + translationKey, args);
    }

    public enum TranslateType implements StringIdentifiable {
        OVERLAY(".overlay."), COMMAND(".command."), MISC(".misc."), CONFIG(".config."), NONE(".");

        private final String s;

        TranslateType(String s) {
            this.s = s;
        }

        @Override
        public String asString() {
            return s;
        }
    }
}
