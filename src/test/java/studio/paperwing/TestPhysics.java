package studio.paperwing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

public class TestPhysics {
    static DWorld world = null;

    @BeforeAll
    static void init() {
        OdeHelper.initODE2(0);
        world = OdeHelper.createWorld();
    }


    @Test
    void passingPhysicsTest() {
        world.setQuickStepNumIterations(60);
        assertTrue(world.quickStep(1.0 / 60.0));
    }

    @AfterAll
    static void close() {
        world.destroy();
        OdeHelper.closeODE();
    }
}
