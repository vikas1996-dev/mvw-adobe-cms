package com.mvw.core.services;

import java.util.List;

import com.mvw.core.models.dto.CountryDTO;

public interface CountryService {

   List<CountryDTO> getCountries(); 

   List<CountryDTO> getStates(String countryCode);
}

