package dev.diona.southside.util.render;

import dev.diona.southside.util.misc.MathUtil;

import java.awt.*;
import java.util.ArrayList;

public class ChromaJS {
    public static class Scale {
        public final Color firstColor, lastColor;
        public final int numColors;

        public final java.util.List<Color> colors;

        public Scale(Color firstColor, Color lastColor, int numColors) {
            this.firstColor = firstColor;
            this.lastColor = lastColor;
            this.numColors = numColors;
            this.colors = new ArrayList<Color>();

            this.initialize();
        }

        public void initialize() {
            int dm = 0, dd = 1;
            for (int i = 0; i < numColors; i++) {
                this.colors.add(this.getColor(dm + (((double) i / (numColors - 1)) * dd)));
            }
        }


        private Color getColor(double val) {
            final int _min = 0, _max = 1;
            double t = (val - _min) / (_max - _min);

            t = Math.min(1, Math.max(0, t));

            int k = (int) Math.floor(t * 10000);
            Color col = null;

            for (int p = 0; p <= 1; p++) {
                if (t <= p) {
                    col = p == 0 ? firstColor : lastColor;
                    break;
                }
                if ((t >= p) && (p == 1)) {
                    col = p == 0 ? firstColor : lastColor;
                    break;
                }
                if (t > p && t < p + 1) {
                    t = (t-p)/(p + 1 - p);
                    col = this.interpolate(firstColor, lastColor, (float) t);
                    break;
                }
            }

            return col;
        }

        public static float[] convertToHCL(Color color) {
            // 将 RGB 转换为 Lab
            float[] lab = new float[3];
            RGBtoLab(color.getRed(), color.getGreen(), color.getBlue(), lab);

            // 将 Lab 转换为 HCL
            float[] hcl = new float[3];
            LabtoHCL(lab[0], lab[1], lab[2], hcl);

            return hcl;
        }

        // RGB 转换为 Lab
        private static void RGBtoLab(int r, int g, int b, float[] lab) {
            float[] xyz = new float[3];
            float[] scaledRGB = {r / 255f, g / 255f, b / 255f};

            for (int i = 0; i < 3; i++) {
                scaledRGB[i] = (scaledRGB[i] > 0.04045f) ?
                        (float) Math.pow((scaledRGB[i] + 0.055) / 1.055, 2.4) :
                        (scaledRGB[i] / 12.92f);
                scaledRGB[i] *= 100.0;
            }

            xyz[0] = scaledRGB[0] * 0.4124564f + scaledRGB[1] * 0.3575761f + scaledRGB[2] * 0.1804375f;
            xyz[1] = scaledRGB[0] * 0.2126729f + scaledRGB[1] * 0.7151522f + scaledRGB[2] * 0.0721750f;
            xyz[2] = scaledRGB[0] * 0.0193339f + scaledRGB[1] * 0.1191920f + scaledRGB[2] * 0.9503041f;

            xyz[0] /= 95.047f;
            xyz[1] /= 100.0f;
            xyz[2] /= 108.883f;

            for (int i = 0; i < 3; i++) {
                xyz[i] = (xyz[i] > 0.008856f) ?
                        (float) Math.pow(xyz[i], 1.0 / 3.0) :
                        (7.787f * xyz[i] + 16.0f / 116.0f);
            }

            lab[0] = 116.0f * xyz[1] - 16.0f;
            lab[1] = 500.0f * (xyz[0] - xyz[1]);
            lab[2] = 200.0f * (xyz[1] - xyz[2]);
        }

        // Lab 转换为 HCL
        private static void LabtoHCL(float L, float a, float b, float[] hcl) {
            float C = (float) Math.sqrt(a * a + b * b);
            float H = (float) Math.toDegrees(Math.atan2(b, a));
            if (H < 0) {
                H += 360;
            }
            hcl[0] = H;
            hcl[1] = C;
            hcl[2] = L;
        }



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

        public int[] lch2rgb(double l, double c, double h) {
            double[] lab = lch2lab(l, c, h);
            return lab2rgb(lab[0], lab[1], lab[2]);
        }

        private double[] lch2lab(double l, double c, double h) {
            if (Double.isNaN(h)) h = 0;
            h = Math.toRadians(h);
            return new double[]{l, Math.cos(h) * c, Math.sin(h) * c};
        }

        private int[] lab2rgb(double l, double a, double b) {
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

        private double xyz_rgb(double r) {
            return 255 * (r <= 0.00304 ? 12.92 * r : Math.pow(r, 1 / 2.4) * 1.055 - 0.055);
        }

        private double lab_xyz(double t) {
            return t > t1 ? t * t * t : t2 * (t - t0);
        }

        private Color hclToColor(float[] hcl) {
            int[] rgb = lch2rgb(hcl[2], hcl[1], hcl[0]);
            for (int i = 0; i < 3; i++) {
                rgb[i] = MathUtil.clamp(rgb[i], 0, 255);
            }
            return new Color(rgb[0], rgb[1], rgb[2]);
        }

        private Color interpolate(Color col1, Color col2, float f) {
            float[] xyz0 = convertToHCL(col1);
            float[] xyz1 = convertToHCL(col2);
            float hue0, hue1, sat0, sat1, lbv0, lbv1;
            hue0 = xyz0[0];
            hue1 = xyz1[0];
            sat0 = xyz0[1];
            sat1 = xyz1[1];
            lbv0 = xyz0[2];
            lbv1 = xyz1[2];
            float sat = -1, hue, lbv, dh;

            if (!Float.isNaN(hue0) && !Float.isNaN(hue1)) {
                // both colors have hue
                if (hue1 > hue0 && hue1 - hue0 > 180) {
                    dh = hue1 - (hue0 + 360);
                } else if (hue1 < hue0 && hue0 - hue1 > 180) {
                    dh = hue1 + 360 - hue0;
                } else {
                    dh = hue1 - hue0;
                }
                hue = hue0 + f * dh;
            } else if (!Float.isNaN(hue0)) {
                hue = hue0;
                if (lbv1 == 1 || lbv1 == 0) sat = sat0;
            } else if (!Float.isNaN(hue1)) {
                hue = hue1;
                if (lbv0 == 1 || lbv0 == 0) sat = sat1;
            } else {
                hue = Float.NaN;
            }
            if (sat == -1) {
                sat = sat0 + f * (sat1 - sat0);
            }
            lbv = lbv0 + f * (lbv1 - lbv0);
            return hclToColor(new float[] { hue, sat, lbv });
        }

        public int getColorRGB(double rate) {
            rate = MathUtil.clamp(rate, 0, 1);
            int count = (int) Math.floor(Math.max(0, rate * (numColors - 1) - 0.001));
            return ColorUtil.interpolateColor(colors.get(count), colors.get(count + 1), (float) ((rate - (double) count / (numColors - 1)) * (numColors - 1)));
        }
    }
}
