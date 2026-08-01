package studio.paperwing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

public class TestMath {
    @Test
    void passingDotProductTest() {
        Vector3f first = new Vector3f(5, 3, 2);
        Vector3f second = new Vector3f(-4, 1, 2);
        float dot = first.dot(second);
        assertTrue(dot == -13.0f);
    }
}
