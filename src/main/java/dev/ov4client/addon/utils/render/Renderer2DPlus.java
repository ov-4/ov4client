package dev.ov4client.addon.utils.render;

import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class Renderer2DPlus {
    public static final double circleNone = 0;
    public static final double circleQuarter = Math.PI / 2;
    public static final double circleHalf = circleQuarter * 2;
    public static final double circleThreeQuarter = circleQuarter * 3;

    public static void quadRoundedOutline(double x, double y, double width, double height, Color color, double r, double s) {
        r = getR(r, width, height);
        if (r <= 0) {
            Renderer2D.COLOR.quad(x, y, width, s, color);
            Renderer2D.COLOR.quad(x, y + height - s, width, s, color);
            Renderer2D.COLOR.quad(x, y + s, s, height - s * 2, color);
            Renderer2D.COLOR.quad(x + width - s, y + s, s, height - s * 2, color);
        }
        else {
            //top
            circlePartOutline(x + r, y + r, r, circleThreeQuarter, circleQuarter, color, s);
            Renderer2D.COLOR.quad(x + r, y, width - r * 2, s, color);
            circlePartOutline(x + width - r, y + r, r, circleNone, circleQuarter, color, s);
            //middle
            Renderer2D.COLOR.quad(x, y + r, s, height - r * 2, color);
            Renderer2D.COLOR.quad(x + width - s, y + r, s, height - r * 2, color);
            //bottom
            circlePartOutline(x + width - r, y + height - r, r, circleQuarter, circleQuarter, color, s);
            Renderer2D.COLOR.quad(x + r, y + height - s, width - r * 2, s, color);
            circlePartOutline(x + r, y + height - r, r, circleHalf, circleQuarter, color, s);
        }
    }

    public static void quadRounded(double x, double y, double width, double height,double r, Color color) {
        quadRounded(x, y, width, height, color,r,true);
    }

    public static void quadRounded(double x, double y, double width, double height, Color color, double r, boolean roundTop) {
        r = getR(r, width, height);
        if (r <= 0)
            Renderer2D.COLOR.quad(x, y, width, height, color);
        else {
            if (roundTop) {
                //top
                circlePart(x + r, y + r, r, circleThreeQuarter, circleQuarter, color);
                Renderer2D.COLOR.quad(x + r, y, width - 2 * r, r, color);
                circlePart(x + width - r, y + r, r, circleNone, circleQuarter, color);
                //middle
                Renderer2D.COLOR.quad(x, y + r, width, height - 2 * r, color);
            }
            else {
                //middle
                Renderer2D.COLOR.quad(x, y, width, height - r, color);
            }
            //bottom
            circlePart(x + width - r, y + height - r, r, circleQuarter, circleQuarter, color);
            Renderer2D.COLOR.quad(x + r, y + height - r, width - 2 * r, r, color);
            circlePart(x + r, y + height - r, r, circleHalf, circleQuarter, color);
        }
    }

    public static void quadRoundedSide(double x, double y, double width, double height, Color color, double r, boolean right) {
        r = getR(r, width, height);
        if (r <= 0)
            Renderer2D.COLOR.quad(x, y, width, height, color);
        else {
            if (right) {
                circlePart(x + width - r, y + r, r, circleNone, circleQuarter, color);
                circlePart(x + width - r, y + height - r, r, circleQuarter, circleQuarter, color);
                Renderer2D.COLOR.quad(x, y, width - r, height, color);
                Renderer2D.COLOR.quad(x + width - r, y + r, r, height - r * 2, color);
            }
            else {
                circlePart(x + r, y + r, r, circleThreeQuarter, circleQuarter, color);
                circlePart(x + r, y + height - r, r, circleHalf, circleQuarter, color);
                Renderer2D.COLOR.quad(x + r, y, width - r, height, color);
                Renderer2D.COLOR.quad(x, y + r, r, height - r * 2, color);
            }
        }
    }

    private static double getR(double r, double w, double h) {
        if (r * 2 > h) {
            r = h / 2;
        }
        if (r * 2 > w) {
            r = w / 2;
        }
        return r;
    }

    private static int getCirDepth(double r, double angle) {
        return Math.max(1, (int)(angle * r / circleQuarter));
    }

    public static void circlePart(double x, double y, double r, double startAngle, double angle, Color color) {
        int cirDepth = getCirDepth(r, angle);
        double cirPart = angle / cirDepth;
        int center = Renderer2D.COLOR.triangles.vec2(x, y).color(color).next();
        int prev = vecOnCircle(x, y, r, startAngle, color);
        for (int i = 1; i < cirDepth + 1; i++) {
            int next = vecOnCircle(x, y, r, startAngle + cirPart * i, color);
            Renderer2D.COLOR.triangles.triangle(prev, center, next);
            prev = next;
        }
    }


    public static void circlePartOutline(double x, double y, double r, double startAngle, double angle, Color color, double outlineWidth) {
        if (outlineWidth >= r) {
            circlePart(x, y, r, startAngle, angle, color);
            return;
        }
        int cirDepth = getCirDepth(r, angle);
        double cirPart = angle / cirDepth;
        int innerPrev = vecOnCircle(x, y, r - outlineWidth, startAngle, color);
        int outerPrev = vecOnCircle(x, y, r, startAngle, color);
        for (int i = 1; i < cirDepth + 1; i++) {
            int inner = vecOnCircle(x, y, r - outlineWidth, startAngle + cirPart * i, color);
            int outer = vecOnCircle(x, y, r, startAngle + cirPart * i, color);
            Renderer2D.COLOR.triangles.quad(inner, innerPrev, outerPrev, outer);
            innerPrev = inner;
            outerPrev = outer;
        }
    }

    private static int vecOnCircle(double x, double y, double r, double angle, Color color) {
        return Renderer2D.COLOR.triangles.vec2(x + Math.sin(angle) * r, y - Math.cos(angle) * r).color(color).next();
    }
}
