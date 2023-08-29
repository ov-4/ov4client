package dev.ov4client.addon.utils.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;

import static dev.ov4client.addon.utils.render.RenderUtils.RenderMode;

public class RenderInfo {
    public Render3DEvent event;
    public RenderMode renderMode;
    public ShapeMode shapeMode;

    public RenderInfo(Render3DEvent event, RenderMode renderMode, ShapeMode shapeMode) {
        this.event = event;
        this.renderMode = renderMode;
        this.shapeMode = shapeMode;
    }

    public RenderInfo(Render3DEvent event, RenderMode renderMode) {
        this.event = event;
        this.renderMode = renderMode;
    }
}
