package net.etherealnation.diaspora.clustergen.test.util;

import net.etherealnation.diaspora.clustergen.app.util.Fate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FateTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRoll() throws Exception {
        int roll = Fate.roll(0);
        assertTrue("roll is in bounds", (roll > -5) && (roll < 5));
        int positiveRoll = Fate.roll(3);
        assertTrue("roll modified by +3 is in bounds", (positiveRoll > -2) && (positiveRoll < 8));
        int negativeRoll = Fate.roll(-3);
        assertTrue("roll modified by -3 is in bounds", (negativeRoll > -8) && (negativeRoll < 2));
    }
}