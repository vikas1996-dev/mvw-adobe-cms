package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResortInfoItemTest {

    private ResortInfoItem resortInfoItem;

    @BeforeEach
    void setUp() {
        resortInfoItem = new ResortInfoItem();
    }

    @Test
    void testDefaultConstructor() {
        assertNull(resortInfoItem.getName());
        assertNull(resortInfoItem.getDescription());
    }

    @Test
    void testParameterizedConstructor() {
        ResortInfoItem item = new ResortInfoItem("Check-in Policy", "Check-in starts at 4 PM");
        
        assertEquals("Check-in Policy", item.getName());
        assertEquals("Check-in starts at 4 PM", item.getDescription());
    }

    @Test
    void testGettersAndSetters() {
        resortInfoItem.setName("Pool Hours");
        resortInfoItem.setDescription("Pool is open from 8 AM to 10 PM");
        resortInfoItem.setIcon("pool-icon");

        assertEquals("Pool Hours", resortInfoItem.getName());
        assertEquals("Pool is open from 8 AM to 10 PM", resortInfoItem.getDescription());
        assertEquals("pool-icon", resortInfoItem.getIcon());
    }

    @Test
    void testIsServiceAnimalPolicyTrue() {
        resortInfoItem.setName("Service Animal Policy");
        assertTrue(resortInfoItem.isServiceAnimalPolicy());
    }

    @Test
    void testIsServiceAnimalPolicyFalse() {
        resortInfoItem.setName("Pet Policy");
        assertFalse(resortInfoItem.isServiceAnimalPolicy());
    }

    @Test
    void testIsServiceAnimalPolicyCaseInsensitive() {
        resortInfoItem.setName("SERVICE ANIMAL guidelines");
        assertTrue(resortInfoItem.isServiceAnimalPolicy());
    }

    @Test
    void testIsServiceAnimalPolicyWithNullName() {
        assertFalse(resortInfoItem.isServiceAnimalPolicy());
    }

    @Test
    void testIsCashlessResortTrue() {
        resortInfoItem.setName("Cashless Resort Policy");
        assertTrue(resortInfoItem.isCashlessResort());
    }

    @Test
    void testIsCashlessResortFalse() {
        resortInfoItem.setName("Payment Policy");
        assertFalse(resortInfoItem.isCashlessResort());
    }

    @Test
    void testIsCashlessResortCaseInsensitive() {
        resortInfoItem.setName("CASHLESS payment");
        assertTrue(resortInfoItem.isCashlessResort());
    }

    @Test
    void testIsCashlessResortWithNullName() {
        assertFalse(resortInfoItem.isCashlessResort());
    }

    @Test
    void testIsVacationOwnerShipTrue() {
        resortInfoItem.setName("vacation-ownership-header information");
        assertTrue(resortInfoItem.isVacationOwnerShip());
    }

    @Test
    void testIsVacationOwnerShipFalse() {
        resortInfoItem.setName("General Information");
        assertFalse(resortInfoItem.isVacationOwnerShip());
    }

    @Test
    void testIsVacationOwnerShipCaseInsensitive() {
        resortInfoItem.setName("VACATION-OWNERSHIP-HEADER details");
        assertTrue(resortInfoItem.isVacationOwnerShip());
    }

    @Test
    void testIsVacationOwnerShipWithNullName() {
        assertFalse(resortInfoItem.isVacationOwnerShip());
    }

    @Test
    void testToString() {
        resortInfoItem.setName("Test Item");
        resortInfoItem.setDescription("Test Description");
        
        String toString = resortInfoItem.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("ResortInfoItem"));
        assertTrue(toString.contains("Test Item"));
        assertTrue(toString.contains("Test Description"));
    }
}
