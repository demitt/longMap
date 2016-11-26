package ua.demitt.homework.longmap.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ua.demitt.homework.longmap.TestMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LongMapTest {
    private TestMap<String> map;

    private final long key1 = 1L;
    private final String value1 = "value1";
    private final long key2 = 2L;
    private final String value2 = "value2";


    @Before
    public void setUp() throws Exception {
        this.map = new LongMap<>();
    }

    @Test
    public void testPutOnEmptyMap() throws Exception {
        //Given

        //When
        String actualPutResult = this.map.put(this.key1, this.value1);

        //Then
        assertNull("put() не вернул null", actualPutResult);
        assertEquals("Количество элементов ошибочное", 1, this.map.size());
        assertTrue("Добавленный элемент отсутствует в мапе", this.map.containsKey(this.key1));
    }

    @Test
    public void testPutOnNonEmptyMap() throws Exception {
        //Given
        this.map.put(this.key2, this.value2);

        //When
        String actualResult = this.map.put(this.key1, this.value1);

        //Then
        assertNull("put() не вернул null", actualResult);
        assertEquals("Количество элементов ошибочное", 2, this.map.size());
        assertTrue("Добавленный элемент отсутствует в мапе", this.map.containsKey(this.key1));
    }

    @Test
    public void testPutWithRepeatedKey() throws Exception {
        //Given
        this.map.put(this.key1, this.value1);

        //When
        String actualPutResult = this.map.put(this.key1, this.value2);

        //Then
        assertNotNull("put() вернул null", actualPutResult);
        assertEquals("put() не вернул прежнее значение для такого же ключа", this.value1, actualPutResult);
        assertEquals("Количество элементов ошибочное", 1, this.map.size());
        assertTrue("Добавленный элемент отсутствует в мапе", this.map.containsKey(this.key1));
    }

    @Test
    public void testGet() throws Exception {
        //Given
        this.map.put(this.key1, this.value1);
        this.map.put(this.key1, this.value2);
        this.map.put(this.key2, this.value2);

        //When
        String actualGetResult = this.map.get(this.key1);

        //Then
        assertNotNull("get() не нашел существующий ключ", actualGetResult);
        assertEquals("get() вернул ошибочное (возможно, предыдущее) значение", this.value2, actualGetResult);
    }

    @Test
    public void testGetOnEmptyMap() throws Exception {
        //Given

        //When
        String actualGetResult = this.map.get(this.key1);

        //Then
        assertNull("get() на пустой мапе не вернул null", actualGetResult);
    }

    @Test
    public void testGetWithNoneExistingKey() throws Exception {
        //Given
        this.map.put(this.key1, this.value1);

        //When
        String actualGetResult = this.map.get(this.key2);

        //Then
        assertNull("get() не вернул null для несуществующего ключа", actualGetResult);
    }

    @Test
    public void testRemove() throws Exception {
        //Given
        addSeveralElementsToMap();
        long initialSize = this.map.size();

        //When
        String actualRemoveResult = this.map.remove(this.key1);

        //Then
        assertEquals("Количество элементов не уменьшилось", initialSize - 1, this.map.size());
        assertEquals("remove() вернул ошибочное значение", this.value1, actualRemoveResult);
        assertFalse("Удаленный элемент остался в мапе", this.map.containsKey(this.key1));
    }

    private void addSeveralElementsToMap() {
        this.map.put(this.key1, this.value1);
        this.map.put(this.key2, this.value2);
    }

    @Test
    public void testIsEmptyOnEmptyMap() throws Exception {
        //Given

        //When
        boolean actualIsEmptyResult = this.map.isEmpty();

        //Then
        assertTrue("Пустая мапа определилась как не пустая", actualIsEmptyResult);
    }

    @Test
    public void testIsEmptyOnNonEmptyMap() throws Exception {
        //Given
        addSeveralElementsToMap();

        //When
        boolean actualIsEmptyResult = this.map.isEmpty();

        //Then
        assertFalse("Непустая мапа определилась как пустая", actualIsEmptyResult);
    }

    @Test
    public void testContainsKey() throws Exception {
        //Given
        addSeveralElementsToMap();

        //When
        boolean actualContainsKeyResult = this.map.containsKey(this.key1);

        //Then
        assertTrue("containsKey() не нашел существующий ключ", actualContainsKeyResult);
    }

    @Test
    public void testContainsKeyWithNonExistingKey() throws Exception {
        //Given
        addSeveralElementsToMap();

        //When
        boolean actualContainsKeyResult = this.map.containsKey(100L);

        //Then
        assertFalse("containsKey() нашел несуществующий ключ", actualContainsKeyResult);
    }

    @Test
    public void testContainsValue() throws Exception {
        //Given
        addSeveralElementsToMap();

        //When
        boolean actualContainsValueResult = this.map.containsValue(this.value1);

        //Then
        assertTrue("containsValue() не нашел существующее значение", actualContainsValueResult);
    }

    @Test
    public void testContainsValueWithNonExistingKey() throws Exception {
        //Given
        addSeveralElementsToMap();

        //When
        boolean actualContainsValueResult = this.map.containsValue("some value");

        //Then
        assertFalse("containsValue() нашел несуществующее значение", actualContainsValueResult);
    }

    @Test
    public void testKeysOnEmptyMap() throws Exception {
        //Given

        //When
        long[] actualKeysResult = this.map.keys();

        //Then
        assertEquals("Получена ненулевая длина массива ключей", 0, actualKeysResult.length);
    }

    @Test
    public void testKeys() throws Exception {
        //Given
        addSeveralElementsToMap();
        List<Long> existingKeys = Arrays.asList(this.key1, this.key2);
        existingKeys.sort(null);

        //When
        long[] actualKeysResult = this.map.keys();
        List<Long> actualKeys = new ArrayList<>(actualKeysResult.length);
        for (long e : actualKeysResult) {
            actualKeys.add(e);
        }
        actualKeys.sort(null);

        //Then
        assertEquals("Получена ошибочная длина массива ключей", this.map.size(), actualKeysResult.length);
        assertEquals("Список ключей ошибочный", existingKeys, actualKeys);
    }

    @Test
    @Ignore
    public void testValuesOnEmptyMap() throws Exception {
        //Given

        //When
        //TODO

        //Then
        fail("NOT IMPLEMENTED YET");
    }

    @Test
    @Ignore
    public void testValues() throws Exception {
        //Given
        //TODO

        //When
        //TODO

        //Then
        fail("NOT IMPLEMENTED YET");
    }

    @Test
    public void testSize() throws Exception {
        //Given
        addSeveralElementsToMap();

        //When
        long actualSize = this.map.size();

        //Then
        assertEquals("size() вернул ошибочный размер", 2, actualSize);
    }

    @Test
    public void testClear() throws Exception {
        //Given
        addSeveralElementsToMap();

        //When
        this.map.clear();

        //Then
        assertEquals("Мапа не пуста", 0, this.map.size());
    }
}