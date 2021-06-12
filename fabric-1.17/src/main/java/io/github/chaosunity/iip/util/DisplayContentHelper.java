package io.github.chaosunity.iip.util;

public class DisplayContentHelper {
    public static final Translatable[] contents = new Translatable[] {
            createTranslatable("fps"),
            createTranslatable("biome"),
            createTranslatable("health"),
            createTranslatable("hungerSaturation"),
            createTranslatable("armor"),
            createTranslatable("pos"),
            createTranslatable("equipment"),
            createTranslatable("heldItem")
    };

    public static final Translatable[] subContents = new Translatable[] {
            createTranslatable("hunger"),
            createTranslatable("saturation")
    };

    private static Translatable createTranslatable(String key) {
        return new Translatable(key, Translatable.TranslateType.CONFIG);
    }
}
