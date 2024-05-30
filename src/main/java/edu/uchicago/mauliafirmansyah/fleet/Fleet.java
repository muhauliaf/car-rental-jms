package edu.uchicago.mauliafirmansyah.fleet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Fleet {
    public static Fleet getInstance() {
        if (instance == null) {
            instance = new Fleet();
        }
        return instance;
    }

    private static Fleet instance = null;

    private Fleet() {
        locations = Collections.synchronizedMap(new HashMap<>());
    }

    public Map<String, Location> locations;
}
