package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParentTest {

    private Parent parent;

    @BeforeEach
    void setUp() {
        parent = new Parent();
    }

    @Test
    void testGettersAndSetters() {
        parent.setName("Parent Category");
        assertEquals("Parent Category", parent.getName());
    }

    @Test
    void testNullValue() {
        assertNull(parent.getName());
    }
}
