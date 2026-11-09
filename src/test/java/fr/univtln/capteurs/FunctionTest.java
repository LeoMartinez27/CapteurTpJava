package fr.univtln.capteurs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class FunctionTest {
    @Test
    void fonctionLineaire() {
        Function lineaire = x -> 20 + x;   // lambda
        assertEquals(20.0, lineaire.apply(0));
        assertEquals(25.0, lineaire.apply(5));
    }
}
