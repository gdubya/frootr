package bv.frootr.service;

import bv.frootr.model.FruitCount;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class FrootService {

    private final ConcurrentHashMap<String, Integer> fruitBowl = new ConcurrentHashMap<String, Integer>();

    public void updateBowl(String fruitName, Integer count) {
        fruitBowl.put(fruitName, count);
    }

    public FruitCount getFruitCount() {
        return new FruitCount(fruitBowl);
    }

}
