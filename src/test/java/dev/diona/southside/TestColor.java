package dev.diona.southside;

import dev.diona.southside.util.render.ChromaJS;

import java.awt.Color;

public class TestColor {

    // Corresponds roughly to RGB brighter/darker
    private static final double Kn = 18;

    // D65 standard referent
    private static final double Xn = 0.950470;
    private static final double Yn = 1;
    private static final double Zn = 1.088830;

    private static final double t0 = 0.137931034;  // 4 / 29
    private static final double t1 = 0.206896552;  // 6 / 29
    private static final double t2 = 0.12841855;   // 3 * t1 * t1
    private static final double t3 = 0.008856452;  // t1 * t1 * t1

    public static int[] lch2rgb(double l, double c, double h) {
        double[] lab = lch2lab(l, c, h);
        return lab2rgb(lab[0], lab[1], lab[2]);
    }

    private static double[] lch2lab(double l, double c, double h) {
        if (Double.isNaN(h)) h = 0;
        h = Math.toRadians(h);
        return new double[]{l, Math.cos(h) * c, Math.sin(h) * c};
    }

    private static int[] lab2rgb(double l, double a, double b) {
        double x, y, z, r, g, b_;

        y = (l + 16) / 116;
        x = Double.isNaN(a) ? y : y + a / 500;
        z = Double.isNaN(b) ? y : y - b / 200;

        y = Yn * lab_xyz(y);
        x = Xn * lab_xyz(x);
        z = Zn * lab_xyz(z);

        r = xyz_rgb(3.2404542 * x - 1.5371385 * y - 0.4985314 * z);  // D65 -> sRGB
        g = xyz_rgb(-0.9692660 * x + 1.8760108 * y + 0.0415560 * z);
        b_ = xyz_rgb(0.0556434 * x - 0.2040259 * y + 1.0572252 * z);

        return new int[]{(int) Math.round(r), (int) Math.round(g), (int) Math.round(b_), 255};
    }

    private static double xyz_rgb(double r) {
        return 255 * (r <= 0.00304 ? 12.92 * r : Math.pow(r, 1 / 2.4) * 1.055 - 0.055);
    }

    private static double lab_xyz(double t) {
        return t > t1 ? t * t * t : t2 * (t - t0);
    }

    private static Color hclToColor(float[] hcl) {
        int[] rgb = lch2rgb(hcl[2], hcl[1], hcl[0]);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    public static void main(String[] args) {
        System.out.println(hclToColor(ChromaJS.Scale.convertToHCL(new Color(92, 0, 96))));
    }
}