package com.mvw.core.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilityNavItemTest {

    @Test
    void testUtilityNavItemCanBeInstantiated() {
        UtilityNavItem utilityNavItem = new UtilityNavItem();
        assertNotNull(utilityNavItem);
    }

    @Test
    void testUtilityNavItemIsNotNull() {
        UtilityNavItem item = new UtilityNavItem();
        assertNotNull(item, "UtilityNavItem should be instantiable");
    }

    @Test
    void testMultipleInstancesAreIndependent() {
        UtilityNavItem item1 = new UtilityNavItem();
        UtilityNavItem item2 = new UtilityNavItem();
        
        assertNotSame(item1, item2, "Multiple instances should be different objects");
    }
}
