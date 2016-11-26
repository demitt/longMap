package ua.demitt.homework.longmap.impl;

import ua.demitt.homework.longmap.TestMap;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class LongMap<V> implements TestMap<V> /*, Iterable<LongMap.Node<V>>*/ {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private static final int MAX_CAPACITY = 1073741824;
    //Для справки:
    //  Integer.MAX_VALUE >> 1 == 1073741823
    //  в хэшмапе: 1073741824 (половина от макс. значения)

    private int size;
    private int capacity;
    private final float loadFactor;
    private int minSizeForResize;
    private Node<V>[] table;
    private Class<?> type;


    public LongMap(Class<?> clazz) {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, clazz);
    }

    public LongMap(int capacity, Class<?> clazz) {
        this(capacity, DEFAULT_LOAD_FACTOR, clazz);
    }

    //@SuppressWarnings("unchecked")
    public LongMap(int initCapacity, float loadFactor, Class<?> clazz) {
        if (initCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initCapacity);
        }
        initCapacity = initCapacityRounding(initCapacity);
        if (loadFactor > 0.0F /*&& (!Float.isNaN(loadFactor))*/) { //TODO: понять, зачем вторая проверка
            this.loadFactor = loadFactor;
            capacity = initCapacity;
            size = 0;
            minSizeForResize = minSizeForResizeFor(initCapacity);
            table = (Node<V>[]) new Node[initCapacity];
            type = clazz;
        } else {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        System.out.println(
            "cap=" + initCapacity + ", loadFactor=" + loadFactor + ", minSizeForResize=" + minSizeForResize +
            ", table.length=" + table.length
        );
    }


    private int initCapacityRounding(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAX_CAPACITY) ? MAX_CAPACITY : n + 1;
    }

    private int minSizeForResizeFor(long capacity) {
        System.out.println("minSizeForResizeFor()=" + Math.ceil(capacity * loadFactor));
        return (int) Math.ceil(capacity * loadFactor);
    }

    @Override
    public V put(long key, V value) {
        System.out.println("put(); key=" + key + ", value=" + value);
        int index = indexFor(key, capacity);
        Node<V> node = table[index];
        V oldValue;
        System.out.println("node == null ? " + (node == null));
        while (node != null) {
            System.out.println("node != null, node.key=" + node.key);
            if (key == node.key) {
                System.out.println("key == node.key, oldValue=" + node.value);
                oldValue = node.value;
                node.value = value;
                return oldValue;
            }
            node = node.next;
        }
        addNode(index, key, value, table);

        if ( ++size == minSizeForResize) {
            resize();
        }
        return null;
    }

    private int indexFor(long key, int tableLength) {
        System.out.println("indexFor()=" + key%tableLength);
        return (int) key & (tableLength - 1);
    }

    private void addNode(int index, long key, V value, Node<V>[] table) {
        System.out.println("addNode()");
        Node<V> firstNode = table[index];
        table[index] = new Node<>(index, key, value, firstNode);
    }

    //@SuppressWarnings("unchecked")
    private void resize() {
        capacityIncreasing();
        System.out.println("resize(), новое capacity=" + capacity);
        minSizeForResize = minSizeForResizeFor(capacity);
        Node<V>[] newTable = new Node[capacity];
        transfer(newTable);
        table = newTable;
        System.out.println("resize() завершен");
    }

    private void capacityIncreasing() {
        capacity <<= 1;
    }

    private void transfer(Node<V>[] newTable) {
        int index;
        long key;
        for (Node<V> node : table) {
            while (node != null) {
                key = node.key;
                index = indexFor(key, capacity);
                addNode(index, key, node.value, newTable);
                System.out.println("Добавляем в " + index + " " + key + "=" + node.value);
                node = node.next;
            }
        }
    }

    @Override
    public V get(long key) {
        if (size == 0) {
            return null;
        }
        int index = indexFor(key, capacity);
        Node<V> node = table[index];
        while (node != null) {
            if (key == node.key) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public V remove(long key) {
        if (size == 0) {
            return null;
        }
        int index = indexFor(key, capacity);
        Node<V> node = table[index];
        Node<V> prev = null;
        while (node != null) {
            if (node.key == key) {
                V removedValue = node.value;
                if (prev != null) {
                    prev.next = node.next;
                } else {
                    table[index] = node.next;
                }
                size--;
                return removedValue;
            }
            prev = node;
            node = node.next;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        if (size == 0) {
            return false;
        }
        int index = indexFor(key, capacity);
        Node node = table[index];
        while (node != null) {
            if (key == node.key) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        if (size == 0) {
            return false;
        }
        V curValue;
        boolean isNull = value == null;
        for (Node<V> node : table) {
            while (node != null) {
                curValue = node.value;
                if (curValue != null && curValue.equals(value) || isNull && curValue == null) { //TODO: оптимизировать?
                    return true;
                }
                node = node.next;
            }
        }
        return false;
    }

    @Override
    public long[] keys() {
        long[] keys = new long[size];
        if (size == 0) {
            return keys;
        }
        int index = 0;
        for (Node<V> node : table) {
            while (node != null) {
                keys[index++] = node.key;
                node = node.next;
            }
        }
        return keys;
    }

    @Override
    //@SuppressWarnings("unchecked")
    public V[] values() {
        V[] values = (V[]) Array.newInstance(type, size);
        int index = 0;
        for (Node<V> node : table) {
            while (node != null) {
                values[index++] = node.value;
                node = node.next;
            }
        }
        return values;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = null;
        }
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LongMap{ ");
        for (Node<V> node : table) {
            while (node != null) {
                sb.
                    append("{key=").
                    append(node.key).
                    append(", value=").
                    append(node.value).
                    append("} ");
                node = node.next;
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /*@Override
    public Iterator<Node<V>> iterator() {
        return new LongMapNodeIterator(); //LongMapNodeIterator<> ?  +м.б.this как аргумент
    }*/


    private static class Node<S> {
        final int index;
        final long key;
        S value;
        Node<S> next;

        private Node(int i, long k, S v, Node<S> e) {
            index = i;
            key = k;
            value = v;
            next = e;
        }
    }


    class LongMapValueIterator extends LongMapIterator implements Iterator<V> {
        @Override
        public V next() {
            return nextNode().value;
        }
    }

    /*class LongMapNodeIterator extends LongMapIterator implements Iterator<Node<V>> {
        @Override
        public Node<V> next() {
            return nextNode();
        }
    }*/

    abstract class LongMapIterator {
        private Node<V> current;
        private Node<V> next;
        private int index;
        private Node<V>[] t;

        public LongMapIterator() {
            t = table;
            current = null;
            index = 0;
            next = t[index];
            if (/*t != null &&*/ size > 0) {
                while (index < t.length && next == null) {
                    next = t[++index];
                }
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        public final Node<V> nextNode() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            current = next;
            next = current.next;
            while (next == null && index < t.length) {
                next = t[++index];
            }
            return current;
        }
    }
}

