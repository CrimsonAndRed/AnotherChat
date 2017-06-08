package model.pools;

import model.Origin;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class SecretsPool {
    private static final Map<Origin, Pair<String, String>> pool = new HashMap<>();

    private SecretsPool(){}

    public static Pair<String, String> getSecret(Origin origin) {
        return pool.get(origin);
    }


    public static boolean setSecret(Origin key, Pair<String, String> loginSecret) {
        return pool.putIfAbsent(key, loginSecret) == null;
    }
}
