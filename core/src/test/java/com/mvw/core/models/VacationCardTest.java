package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VacationCardTest {

    private VacationCard vacationCard;

    @BeforeEach
    void setUp() {
        vacationCard = new VacationCard();
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetCards() {
        List<VacationCardItem> cards = new ArrayList<>();
        setField(vacationCard, "cards", cards);
        assertEquals(cards, vacationCard.getCards());
    }

    @Test
    void testGetCardsWithItems() {
        List<VacationCardItem> cards = new ArrayList<>();
        cards.add(new VacationCardItem());
        cards.add(new VacationCardItem());
        setField(vacationCard, "cards", cards);
        assertEquals(2, vacationCard.getCards().size());
    }

    @Test
    void testGetCardsNull() {
        setField(vacationCard, "cards", null);
        assertNull(vacationCard.getCards());
    }
}
