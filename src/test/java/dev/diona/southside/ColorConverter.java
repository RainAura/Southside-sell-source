package dev.diona.southside;

import static java.lang.Math.*;

public class ColorConverter {
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

    public static double[] lch2lab(double l, double c, double h) {
        if (Double.isNaN(h)) h = 0;
        h = Math.toRadians(h);
        return new double[]{l, cos(h) * c, sin(h) * c};
    }

    public static double[] lab2xyz(double[] lab) {
        double l = lab[0];
        double a = lab[1];
        double b = lab[2];

        double y = (l + 16) / 116;
        double x = Double.isNaN(a) ? y : y + a / 500;
        double z = Double.isNaN(b) ? y : y - b / 200;

        y = Yn * lab_xyz(y);
        x = Xn * lab_xyz(x);
        z = Zn * lab_xyz(z);

        return new double[]{x, y, z};
    }

    public static double[] lab2rgb(double[] lab) {
        double[] xyz = lab2xyz(lab);

        double x = xyz[0];
        double y = xyz[1];
        double z = xyz[2];

        double r = xyz_rgb(3.2404542 * x - 1.5371385 * y - 0.4985314 * z);  // D65 -> sRGB
        double g = xyz_rgb(-0.9692660 * x + 1.8760108 * y + 0.0415560 * z);
        double b_ = xyz_rgb(0.0556434 * x - 0.2040259 * y + 1.0572252 * z);

        return new double[]{r, g, b_};
    }

    private static double xyz_rgb(double r) {
        return 255 * (r <= 0.00304 ? 12.92 * r : 1.055 * pow(r, 1 / 2.4) - 0.055);
    }

    private static double lab_xyz(double t) {
        return t > t1 ? t * t * t : t2 * (t - t0);
    }

    public static void main(String[] args) {
        // Example usage
        double[] lch = {50, 100, 180}; // LCH color
        double[] lab = lch2lab(lch[0], lch[1], lch[2]); // Convert LCH to Lab
        double[] rgb = lab2rgb(lab); // Convert Lab to RGB

        // Output the RGB values
        System.out.println("RGB values:");
        for (double val : rgb) {
            System.out.println(val);
        }
    }
}