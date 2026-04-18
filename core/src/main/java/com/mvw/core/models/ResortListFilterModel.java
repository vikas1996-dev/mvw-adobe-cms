package com.mvw.core.models;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.mvw.core.models.dto.ResortDto;
import com.mvw.core.models.dto.ResortListFilterDTO;
import com.mvw.core.models.dto.Tags;

public class ResortListFilterModel {

    public ResortListFilterDTO buildFilters(List<ResortDto> resorts) {

        ResortListFilterDTO filter = new ResortListFilterDTO();

        if (resorts == null || resorts.isEmpty()) {
            return filter;
        }
filter.setVacationTypes(buildVacationFilters(resorts));
        // -------- REGIONS --------
        filter.setRegions(
            resorts.stream()
                .map(ResortDto::getRegion)
                .filter(Objects::nonNull)
                .distinct()
                .map(this::toTag)
                .collect(Collectors.toList())
        );

        // -------- ACTIVITIES --------
        filter.setActivities(
            resorts.stream()
                .flatMap(r -> r.getActivities() == null
                        ? java.util.stream.Stream.empty()
                        : r.getActivities().stream())
                .distinct()
                .map(this::toTag)
                .collect(Collectors.toList())
        );

        // -------- BRANDS --------
        filter.setBrands(
            resorts.stream()
                .map(ResortDto::getDcmBrand)
                .filter(Objects::nonNull)
                .map(Tags::getName)
                .filter(Objects::nonNull)
                .distinct()
                .map(this::toTag)
                .collect(Collectors.toList())
        );

        return filter;
    }

  public List<Tags> buildVacationFilters(List<ResortDto> resorts) {

    if (resorts == null || resorts.isEmpty()) {
        return List.of();
    }

    return resorts.stream()
        .flatMap(resort ->
            resort.getVacationTypes() == null
                ? java.util.stream.Stream.empty()
                : resort.getVacationTypes().stream()
        )
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(
            Tags::getNodename,   // dedupe key
            tag -> tag,          // keep original Tags
            (existing, duplicate) -> existing
        ))
        .values()
        .stream()
        .collect(Collectors.toList());
}


    // Used only for simple string-based filters
    private Tags toTag(String value) {
        Tags tag = new Tags();
        tag.setName(value);
        tag.setNodename(value.toLowerCase().replace(" - ", "-"));
        return tag;
    }



    // Used for vacation types coming from API
    private Tags copyTag(Tags source) {
        Tags tag = new Tags();
        tag.setName(source.getName());
        tag.setNodename(source.getNodename());
        return tag;
    }
}
