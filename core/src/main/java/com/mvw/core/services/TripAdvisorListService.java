package com.mvw.core.services;

import java.util.List;

import com.mvw.core.models.dto.TripAdvisorDto;
import com.mvw.core.models.dto.TripAdvisorResponseDto;

public interface TripAdvisorListService {

      TripAdvisorResponseDto getResponseList();

      TripAdvisorResponseDto getTripadvisor(List<TripAdvisorDto> list);

      TripAdvisorDto getById(String id);
}
