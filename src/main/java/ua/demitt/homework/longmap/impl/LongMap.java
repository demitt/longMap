package ua.demitt.homework.longmap.impl;

import ua.demitt.homework.longmap.TestMap;


public class LongMap<V> implements TestMap<V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private static final int MAX_CAPACITY = Integer.MAX_VALUE >> 1; //TODO: почему половина?
    //Для справки:
    //  Integer.MAX_VALUE >> 1 == 1073741823
    //  в хэшмапе: 1073741824

    private int size = 0;
    private int capacity;
    private final float loadFactor;
    private int minSizeForResize; //threshold
    private Entry<V>[] table;


    public LongMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public LongMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    //@SuppressWarnings("unchecked")
    public LongMap(int initCapacity, float loadFactor) {
        if (initCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initCapacity);
        }
        if (initCapacity > MAX_CAPACITY) {
            initCapacity = MAX_CAPACITY;
        }
        if (loadFactor > 0.0F /*&& (!Float.isNaN(loadFactor))*/) { //TODO: понять, зачем вторая проверка
            this.loadFactor = loadFactor;
            capacity = initCapacity;
            minSizeForResize = minSizeFor(initCapacity);
            table = /*(Entry<V>[])*/ new Entry[initCapacity]; //TODO: cast?
        } else {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        System.out.println(
            "cap=" + initCapacity + ", loadFactor=" + loadFactor + ", minSizeForResize=" + minSizeForResize +
            ", table.length=" + table.length
        );
    }


    private int minSizeFor(long capacity) { //"tableSizeFor" ?
        System.out.println("minSizeFor()=" + Math.ceil(capacity * loadFactor));
        return (int) Math.ceil(capacity * loadFactor);
    }

    @Override
    public V put(long key, V value) {
        System.out.println("put(); key=" + key + ", value=" + value);
        int index = indexFor(key, capacity);
        Entry<V> entry = table[index];
        V oldValue;
        System.out.println("entry == null ? " + (entry == null));
        while (entry != null) {
            System.out.println("entry != null, entry.key=" + entry.key);
            if (key == entry.key) {
                System.out.println("key == entry.key, oldValue=" + entry.value);
                oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
            entry = entry.next;
        }
        addEntry(index, key, value, table);

        if ( ++size == minSizeForResize) {
            resize();
        }
        return null;
    }

    private int indexFor(long key, int tableLength) {
        System.out.println("indexFor()=" + key%tableLength);
        return (int) key%tableLength;  //  == key & (table.length-1)
    }

    private void addEntry(int index, long key, V value, Entry<V>[] table) {
        System.out.println("addEntry()");
        Entry<V> firstEntry = table[index];
        table[index] = new Entry<V>(index, key, value, firstEntry);
    }

    //@SuppressWarnings("unchecked")
    private void resize() {
        capacityIncreasing(); //capacity = tableSizeFor(capacity);
        System.out.println("resize(), новое capacity=" + capacity);
        minSizeForResize = minSizeFor(capacity);
        Entry<V>[] newTable = new Entry[capacity];
        transfer(newTable);
        table = newTable;
        System.out.println("resize() завершен");
    }

    private int tableSizeFor(int capacity) {
        return capacity * 2; //capacity << 1
    }

    private void capacityIncreasing() {
        capacity *= 2;
    }

    private void transfer(Entry<V>[] newTable) {
        int index;
        long key;
        Entry<V> entry;
        for (int i = 0; i < table.length; i++) {
            entry = table[i];
            while (entry != null) {
                key = entry.key;
                index = indexFor(key, capacity);
                addEntry(index, key, entry.value, newTable);
                System.out.println("Добавляем в " + index + " " + key + "=" + entry.value);
                entry = entry.next;
            }
        }
    }

    @Override
    public V get(long key) {
        int index = indexFor(key, capacity);
        Entry<V> entry = table[index];
        while (entry != null) {
            if (key == entry.key) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public V remove(long key) {
        int index = indexFor(key, capacity);
        Entry<V> entry = table[index];
        Entry<V> prev = null;
        while (entry != null) {
            if (entry.key == key) {
                V removedValue = entry.value;
                if (prev != null) {
                    prev.next = entry.next;
                } else {
                    table[index] = entry.next;
                }
                size--;
                return removedValue;
            }
            prev = entry;
            entry = entry.next;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        int index = indexFor(key, capacity);
        Entry entry = table[index];
        while (entry != null) {
            if (key == entry.key) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        Entry<V> entry;
        V curValue;
        boolean isNull = value == null;
        for (int i = 0; i < capacity; i++ ) {
            entry = table[i];
            while (entry != null) {
                curValue = entry.value;
                if (curValue != null && curValue.equals(value) || isNull && curValue == null) { //TODO: оптимизировать?
                    return true;
                }
                entry = entry.next;
            }
        }
        return false;
    }

    @Override
    public long[] keys() {
        long[] keys = new long[size];
        Entry<V> entry;
        int index = 0;
        for (int i = 0; i < capacity; i++) {
            entry = table[i];
            while (entry != null) {
                keys[index] = entry.key;
                entry = entry.next;
            }
        }
        return keys;
    }

    @Override
    //@SuppressWarnings("unchecked")
    public V[] values() { //TODO: пересмотреть концепцию, т.к. возвращает массив Object[]
        Entry<V> entry;
        int index = 0;
        Object[] values = new Object[size];
        for (int i = 0; i < capacity; i++) {
            entry = table[i];
            while (entry != null) {
                values[index] = entry.value;
                entry = entry.next;
            }
        }
        return (V[]) values;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        //if (capacity <= ???) { table = new Entry[capacity]; size=0; return; } //TODO: ?
        for (int i = 0; i < capacity; i++) {
            table[i] = null;
        }
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LongMap{ ");
        Entry<V> entry;
        for (int i = 0; i < table.length; i++) {
            entry = table[i];
            while (entry != null) {
                sb.
                    append("{key=").
                    append(entry.key).
                    append(", value=").
                    append(entry.value).
                    append("} ");
                entry = entry.next;
            }
        }
        sb.append("}");
        return sb.toString();
    }


    private class Entry<T> {
        final int index; //TODO: final?
        final long key;
        T value;
        Entry<T> next;

        private Entry(int i, long k, T v, Entry<T> e) {
            index = i;
            key = k;
            value = v;
            next = e;
        }
    }

}
