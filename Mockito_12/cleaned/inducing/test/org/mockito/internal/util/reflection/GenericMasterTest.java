package org.mockito.internal.util.reflection;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class GenericMasterTest {
    
    GenericMaster m = new GenericMaster();
    
    List<String> one;
    Set<Integer> two;
    Map<Double, String> map;
    String nonGeneric;
    List<Set<String>> nested;
    List<Set<Collection<String>>> multiNested;
    
    @Test
    public void shouldFindGenericClass() throws Exception {
        assertEquals(m.getGenericType(field("one")), String.class);
        assertEquals(m.getGenericType(field("two")), Integer.class);
        assertEquals(m.getGenericType(field("map")), Double.class);
    }
    
    @Test
    public void shouldGetObjectForNonGeneric() throws Exception {
        assertEquals(Object.class, m.getGenericType(field("nonGeneric")));
    }

    @Test
    public void shouldDealWithNestedGenerics() throws Exception {
        assertEquals(Set.class, m.getGenericType(field("nested")));
        assertEquals(Set.class, m.getGenericType(field("multiNested")));
    }

    private Field field(String fieldName) throws SecurityException, NoSuchFieldException {
        return this.getClass().getDeclaredField(fieldName);
    }
}
