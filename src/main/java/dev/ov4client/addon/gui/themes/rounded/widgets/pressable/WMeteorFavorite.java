package dev.ov4client.addon.gui.themes.rounded.widgets.pressable;

import dev.ov4client.addon.gui.themes.rounded.ov4clientGuiWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorFavorite extends WFavorite implements ov4clientGuiWidget {
    public WMeteorFavorite(boolean checked) {
        super(checked);
    }

    @Override
    protected Color getColor() {
        return theme().favoriteColor.get();
    }
}
