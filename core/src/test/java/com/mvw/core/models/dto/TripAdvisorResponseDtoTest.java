package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TripAdvisorResponseDtoTest {

    private TripAdvisorResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new TripAdvisorResponseDto();
    }

    @Test
    void testGettersAndSetters() {
        responseDto.setCount(5);
        assertEquals(Integer.valueOf(5), responseDto.getCount());
    }

    @Test
    void testSetAndGetList() {
        List<TripAdvisorDto> list = new ArrayList<>();
        TripAdvisorDto dto1 = new TripAdvisorDto();
        dto1.setTripadvisorId("TA001");
        TripAdvisorDto dto2 = new TripAdvisorDto();
        dto2.setTripadvisorId("TA002");
        list.add(dto1);
        list.add(dto2);
        
        responseDto.setList(list);
        
        assertEquals(list, responseDto.getList());
        assertEquals(2, responseDto.getList().size());
    }

    @Test
    void testNullValues() {
        assertNull(responseDto.getCount());
        assertNull(responseDto.getList());
    }
}
