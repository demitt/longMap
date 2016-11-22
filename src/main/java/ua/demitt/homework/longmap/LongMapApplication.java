package ua.demitt.homework.longmap;

import ua.demitt.homework.longmap.impl.LongMap;

public class LongMapApplication {
    public static void main(String[] args) {
        System.out.println("LongMap");
        //Map hashMap = new HashMap<>();
        TestMap<Integer> map = new LongMap<>(4);

        System.out.println("isEmpty=" + map.isEmpty());

        map.put(5, 100);
        map.put(5, 1000);
        map.put(10, 200);
        map.put(26, 300);
        map.put(34, 340);

        map.put(41, 401);
        map.put(42, 402);
        map.put(43, 403);
        map.put(44, 404);
        map.put(45, 405);
        map.put(77, 770);

        System.out.println("size=" + map.size());
        System.out.println("isEmpty=" + map.isEmpty());

        System.out.println("*********");
        System.out.println(map.get(5));
        System.out.println(map.get(10));
        System.out.println(map.get(100));

        System.out.println("*********");
        System.out.println(map.containsValue(null));
        System.out.println(map.containsValue(400));
        System.out.println(map.containsValue(500));

        long keyForRemoving = 77;
        System.out.println("*********");
        Integer removed = map.remove(keyForRemoving);
        System.out.println("removedValue=" + removed);
        System.out.println("Содержит ли удаленный ключ? " + map.containsKey(keyForRemoving));

        System.out.println("size=" + map.size());
        System.out.println("keys.length=" + map.keys().length);
        System.out.println("*********");
        //Integer[] v = map.values();
        /*for (int i = 0; i < values.length; i++) {
            System.out.println(values[i]);
        }*/
        System.out.println(map.toString());


        //TestMap<Integer> map2 = new LongMap<>(4);

    }
}
