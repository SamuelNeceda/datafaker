package net.datafaker.internal.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CopyOnWriteMapTest {

    private CopyOnWriteMap<String, String> copyOnWriteMap;
    private Supplier<Map<String, String>> mapSupplier;

    @BeforeEach
    void setUp() {
        mapSupplier = mock(Supplier.class);
        when(mapSupplier.get()).thenReturn(new HashMap<>());
        copyOnWriteMap = new CopyOnWriteMap<>(mapSupplier);
    }

    @Test
    void testPut() {
        String key = "key1";
        String value = "value1";
        String result = copyOnWriteMap.put(key, value);
        assertNull(result);
        assertTrue(copyOnWriteMap.containsKey(key));
        assertEquals(value, copyOnWriteMap.get(key));
    }

    @Test
    void testPutNullValue() {
        String key = "key1";
        String value = null;
        assertThrows(NullPointerException.class, () -> copyOnWriteMap.put(key, value));
    }

    @Test
    void testPutAll() {
        Map<String, String> mapToAdd = new HashMap<>();
        mapToAdd.put("key2", "value2");
        mapToAdd.put("key3", "value3");
        copyOnWriteMap.putAll(mapToAdd);
        assertTrue(copyOnWriteMap.containsKey("key2"));
        assertTrue(copyOnWriteMap.containsKey("key3"));
        assertEquals(2, copyOnWriteMap.size());
    }

    @Test
    void testRemove() {
        String key = "key1";
        String value = "value1";
        copyOnWriteMap.put(key, value);
        String removedValue = copyOnWriteMap.remove(key);
        assertEquals(value, removedValue);
        assertFalse(copyOnWriteMap.containsKey(key));
    }

    @Test
    void testSize() {
        copyOnWriteMap.put("key1", "value1");
        copyOnWriteMap.put("key2", "value2");
        int size = copyOnWriteMap.size();
        assertEquals(2, size);
    }

    @Test
    void testIsEmpty() {
        boolean isEmptyInitially = copyOnWriteMap.isEmpty();
        copyOnWriteMap.put("key1", "value1");
        boolean isEmptyAfterPut = copyOnWriteMap.isEmpty();
        assertTrue(isEmptyInitially);
        assertFalse(isEmptyAfterPut);
    }

    @Test
    void testContainsKey() {
        copyOnWriteMap.put("key1", "value1");
        assertTrue(copyOnWriteMap.containsKey("key1"));
        assertFalse(copyOnWriteMap.containsKey("key2"));
    }

    @Test
    void testContainsValue() {
        copyOnWriteMap.put("key1", "value1");
        assertTrue(copyOnWriteMap.containsValue("value1"));
        assertFalse(copyOnWriteMap.containsValue("value2"));
    }

    @Test
    void testGetOrDefault() {
        copyOnWriteMap.put("key1", "value1");
        assertEquals("value1", copyOnWriteMap.getOrDefault("key1", "default"));
        assertEquals("default", copyOnWriteMap.getOrDefault("key2", "default"));
    }

    @Test
    void testGet() {
        copyOnWriteMap.put("key1", "value1");
        assertEquals("value1", copyOnWriteMap.get("key1"));
        assertNull(copyOnWriteMap.get("key2"));
    }

    @Test
    void testKeySet() {
        copyOnWriteMap.put("key1", "value1");
        copyOnWriteMap.put("key2", "value2");
        Set<String> keySet = copyOnWriteMap.keySet();
        assertTrue(keySet.contains("key1"));
        assertTrue(keySet.contains("key2"));
    }

    @Test
    void testValues() {
        copyOnWriteMap.put("key1", "value1");
        copyOnWriteMap.put("key2", "value2");
        Collection<String> values = copyOnWriteMap.values();
        assertTrue(values.contains("value1"));
        assertTrue(values.contains("value2"));
    }

    @Test
    void testEntrySet() {
        copyOnWriteMap.put("key1", "value1");
        copyOnWriteMap.put("key2", "value2");
        Set<Map.Entry<String, String>> entrySet = copyOnWriteMap.entrySet();
        assertTrue(entrySet.stream().anyMatch(entry -> entry.getKey().equals("key1") && entry.getValue().equals("value1")));
        assertTrue(entrySet.stream().anyMatch(entry -> entry.getKey().equals("key2") && entry.getValue().equals("value2")));
    }

    @Test
    void testUnsupportedPutIfAbsent() {
        assertThrows(UnsupportedOperationException.class, () -> copyOnWriteMap.putIfAbsent("key1", "value1"));
    }

    @Test
    void testToString() {
        copyOnWriteMap.put("key1", "value1");
        String mapString = copyOnWriteMap.toString();
        assertTrue(mapString.contains("key1"));
        assertTrue(mapString.contains("value1"));
    }
}
