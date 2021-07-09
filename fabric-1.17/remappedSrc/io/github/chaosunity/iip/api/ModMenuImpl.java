package io.github.chaosunity.iip.api;

import io.github.chaosunity.iip.InfoIsPower;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return InfoIsPower::createConfigScreen;
    }
}
