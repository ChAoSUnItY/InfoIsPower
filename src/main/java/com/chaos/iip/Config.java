package com.chaos.iip;

import com.chaos.iip.utils.Translatable;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = InfoIsPower.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static class Client {
        public static final List<String> acceptableRenderValues = Lists.newArrayList("FPS", "Biome", "Health", "Hunger", "Armor", "Position", "Equipment", "HeldItems");

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> renderModes;
        public final ForgeConfigSpec.IntValue mode;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Info is Power Config").push(InfoIsPower.MODID);

            mode = builder.comment("This sets the detail mode. 1 is held items only, 2 is armor only, 3 is nondetailed mode.")
                    .translation(new Translatable("mode", Translatable.TranslateType.CONFIG).getKey())
                    .defineInRange("Mode", 1, 1, 3);

            renderModes = builder.comment("This collection declares what info to render on left.\nAcceptable values: " + acceptableRenderValues.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")))
                    .defineList("RenderList", acceptableRenderValues, acceptableRenderValues::contains);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static boolean shouldRender(String s) {
        return CLIENT.renderModes.get().contains(s);
    }
}
