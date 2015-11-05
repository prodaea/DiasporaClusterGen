package net.etherealnation.diaspora.clustergen.app.util;

import java.util.Random;

public class Fate {

    private static Random random = new Random();

    public static int roll(final int modifier) {
        int total = modifier;

        for (int i = 0; i < 4; i++) {
            total += (random.nextInt(3) - 1);
        }

        return total;
    }

}
