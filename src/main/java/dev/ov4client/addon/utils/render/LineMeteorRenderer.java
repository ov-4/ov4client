package dev.ov4client.addon.utils.render;

import dev.ov4client.addon.utils.timers.TTimerUtils;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.joml.Vector2d;

import java.util.Random;

public class LineMeteorRenderer {
    public Vector2d pos;
    public Vector2d pos2;
    public static Random random = new Random();
    public float LineWidth;
    public float LineLength;
    public float alpha;
    public long speedMS;
    private final TTimerUtils timerUtils = new TTimerUtils();
    public Color randomColor;
    private final FadeUtils fadeUtils = new FadeUtils(170L);

    public LineMeteorRenderer(long speedMS, double x, double y, float LineWidth, float LineLength) {
        this.speedMS = speedMS;
        this.pos = new Vector2d(x, y);
        this.pos2 = new Vector2d(x, y);
        this.LineWidth = LineWidth;
        this.LineLength = LineLength;
        this.randomColor = new Color(java.awt.Color.HSBtoRGB(random.nextInt(360), 0.4f, 1.0f));
        this.fadeUtils.setLength(speedMS / 5L * 2L);
    }

    public static LineMeteorRenderer generateMeteor() {
        long speedMS = 3000 + random.nextInt(1200);
        float x = random.nextInt(Utils.getWindowWidth());
        float y = random.nextInt(Utils.getWindowHeight());
        float lineLength = 50 + random.nextInt(300);
        float lineWidth = (float)(Math.random() * 2.0) + 1.0f;
        return new LineMeteorRenderer(speedMS, x, y, lineWidth, lineLength);
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getLineWidth() {
        return this.LineWidth;
    }

    public double getX() {
        return this.pos.x;
    }

    public double getY() {
        return this.pos.y;
    }

    public double getX2() {
        return this.pos2.x;
    }

    public double getY2() {
        return this.pos2.y;
    }

    public void setLineWidth(float f) {
        this.LineWidth = f;
    }

    public void tick() {
        double speedMoves;
        if (this.timerUtils.passed(this.speedMS)) {
            this.timerUtils.reset();
            this.pos.x = random.nextInt(Utils.getWindowWidth());
            this.pos.y = random.nextInt(Utils.getWindowHeight());
            this.LineLength = 70 + random.nextInt(300);
            this.randomColor = new Color(Rainbow.getRainbow(random.nextInt(360), 0.4f, 1.0f));
            this.fadeUtils.reset();
            this.alpha = 0.0f;
        }
        if (this.timerUtils.passed((speedMoves = (double)(this.speedMS / 5L)) * 3.0)) {
            this.pos.x = (float)((double)this.pos2.x + (double)this.LineLength * this.fadeUtils.getFade(FadeUtils.FadeMode.FADE_OUT));
            this.pos.y = (float)((double)this.pos2.y - (double)this.LineLength * this.fadeUtils.getFade(FadeUtils.FadeMode.FADE_OUT));
        } else if (this.timerUtils.passed(speedMoves * 2.0)) {
            this.fadeUtils.reset();
        } else {
            this.pos2.x = (float)((double)this.pos.x - (double)this.LineLength * this.fadeUtils.getFade(FadeUtils.FadeMode.FADE_IN));
            this.pos2.y = (float)((double)this.pos.y + (double)this.LineLength * this.fadeUtils.getFade(FadeUtils.FadeMode.FADE_IN));
        }
        if (this.alpha < 255.0f) {
            this.alpha += 15.0f;
        }
    }
}

