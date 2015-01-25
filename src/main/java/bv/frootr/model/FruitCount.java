package bv.frootr.model;

import java.util.Collections;
import java.util.Map;

public class FruitCount {

    private final Map<String, Integer> fruitBowl;

    public FruitCount(Map<String, Integer> fruitBowl) {
        this.fruitBowl = Collections.unmodifiableMap(fruitBowl);
    }

    public Map<String, Integer> getFruitBowl() {
        return fruitBowl;
    }
}
