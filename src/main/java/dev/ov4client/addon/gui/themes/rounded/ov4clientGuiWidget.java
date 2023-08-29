package dev.ov4client.addon.gui.themes.rounded;

import dev.ov4client.addon.utils.gui.GuiUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface ov4clientGuiWidget extends BaseWidget {
    default ov4clientGuiTheme theme() {
        return (ov4clientGuiTheme) getTheme();
    }

    default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
        ov4clientGuiTheme theme = theme();
        int r = theme.roundAmount();
        double s = theme.scale(2);
        Color outlineColor = theme.outlineColor.get(pressed, mouseOver);

        GuiUtils.quadRounded(renderer, widget.x + s, widget.y + s, widget.width - s * 2, widget.height - s * 2, theme.backgroundColor.get(pressed, mouseOver), r - s);
        GuiUtils.quadOutlineRounded(renderer, widget, outlineColor, r, s);
    }
}
