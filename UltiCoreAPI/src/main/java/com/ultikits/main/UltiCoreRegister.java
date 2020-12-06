package com.ultikits.main;

import java.util.HashMap;
import java.util.Map;

public class UltiCoreRegister {

    private static Map<String, UltiCoreAPI> map = new HashMap<>();

    private UltiCoreRegister() {
    }

    public static void register(String name, UltiCoreAPI ultiCoreAPI){
        map.put(name, ultiCoreAPI);
    }

    public static UltiCoreAPI getApi(String name){
        return map.get(name);
    }
}
