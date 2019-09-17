package com.zonar.zonarapp.utils;

import android.content.Context;

import com.xround_app_sdk.Controller;

public class ZonarUtils {

    private static Context context;
    private static Controller controller;

    private static double[] sZonarEQ;
    private static int sNumero;
    private static int sMode;

    private static boolean flagWriting = false;

    public static void init(Context ctx) {
        context = ctx;
        controller = new Controller(context);
    }

    public static double[] getEQData(boolean isWrite, int numero, int mode) {
        double[] eq_double = EQ_CTRLtable(numero);

        if (isWrite) {
            write_ZonarEQ(eq_double, numero, mode);
        }

        double max = 0;
        double min = 0;
        for (int i = 0; i < eq_double.length; i++) {
            if (i == 0) {
                max = eq_double[i];
                min = eq_double[i];
            } else {
                if (max < eq_double[i]) {
                    max = eq_double[i];
                }
                if (min > eq_double[i]) {
                    min = eq_double[i];
                }
            }
        }

        double[] eq = new double[eq_double.length];
        for (int i = 0; i < eq_double.length; i++) {
            eq[i] = (eq_double[i] - min) / (max - min) * 10;
        }

        return eq;
    }

    private static double[] EQ_CTRLtable(int numero) {
        return controller.EQ_table(numero);
    }

    private synchronized static void write_ZonarEQ(final double[] ZonarEQ, final int numero, final int mode) {
        sZonarEQ = ZonarEQ;
        sNumero = numero;
        sMode = mode;

        if (flagWriting) {
            return;
        }
        flagWriting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    controller.write_ZonarEQ(ZonarEQ, numero, mode);
                    if (sNumero != numero || sMode != mode) {
                        controller.write_ZonarEQ(sZonarEQ, sNumero, sMode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                flagWriting = false;
            }
        }).start();
    }

}
