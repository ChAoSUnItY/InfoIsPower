package com.chaos.iip;

import com.chaos.iip.utils.RenderLocationType;
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

        public final ForgeConfigSpec.BooleanValue enableRender;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> renderModes;
        public final ForgeConfigSpec.EnumValue<RenderLocationType> renderType;
        public final ForgeConfigSpec.IntValue mode;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Info is Power Config").push(InfoIsPower.MODID);

            mode = builder.comment("This sets the detail mode. 1 is held items only, 2 is armor only, 3 is nondetailed mode.")
                    .defineInRange("Display Mode", 1, 1, 3);

            renderModes = builder.comment("This collection declares what info will be rendered on left.\nAcceptable values: " + acceptableRenderValues.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")))
                    .defineList("Display Filter", acceptableRenderValues, acceptableRenderValues::contains);

            renderType = builder.comment("This enumeration defines where the info will be render.\nAcceptable Values: LEFT, RIGHT")
                    .defineEnum("Render Type", RenderLocationType.LEFT);

            enableRender = builder.comment("This decides should enable this mode's feature. You should disable this while playing on highly game-experience-modified server such as Hypixel or you'll experience random crash.")
                    .define("Enable Render", true);

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
