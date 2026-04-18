package com.mvw.core.services;

public interface CountryRegionService {
    String[] getAPCountries();

    String[] getAUCountries();

    String[] getEUCountries();

    String[] getJapanCountries();

    String[] getLATAMCountries();

    String[] getLeadSubmissionCountries();

    String[] getMECountries();

    String[] getOtherCountries();

    String getRegionByCountry(String countryName);
}
