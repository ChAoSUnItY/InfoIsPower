package com.chaos.iip;

import com.chaos.iip.utils.Translatable;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = InfoIsPower.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static class Client {
        public final ForgeConfigSpec.IntValue mode;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Info is Power Config").push(InfoIsPower.MODID);

            mode = builder.comment("This sets the detail mode. 1 is held items only, 2 is armor only, 3 is nondetailed mode. Default value is 1.")
                    .translation(new Translatable("mode", Translatable.TranslateType.CONFIG).getKey()).defineInRange("Modes", 1, 1, 3);

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
}
