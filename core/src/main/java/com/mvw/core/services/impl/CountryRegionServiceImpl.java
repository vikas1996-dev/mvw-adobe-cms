package com.mvw.core.services.impl;

import com.mvw.core.config.CountryRegionConfig;
import com.mvw.core.services.CountryRegionService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = CountryRegionService.class, immediate = true)
@Designate(ocd = CountryRegionConfig.class)
public class CountryRegionServiceImpl implements CountryRegionService {

    private CountryRegionConfig config;

    @Activate
    @Modified
    protected void activate(CountryRegionConfig config) {
        this.config = config;
    }

    @Override
    public String[] getAPCountries() {
        return config.ap_countries();
    }

    @Override
    public String[] getAUCountries() {
        return config.au_countries();
    }

    @Override
    public String[] getEUCountries() {
        return config.eu_countries();
    }

    @Override
    public String[] getJapanCountries() {
        return config.japan_countries();
    }

    @Override
    public String[] getLATAMCountries() {
        return config.latam_countries();
    }

    @Override
    public String[] getLeadSubmissionCountries() {
        return config.lead_submission_countries();
    }

    @Override
    public String[] getMECountries() {
        return config.me_countries();
    }

    @Override
    public String[] getOtherCountries() {
        return config.other_countries();
    }

    @Override
    public String getRegionByCountry(String countryName) {
        if (countryName == null || countryName.isEmpty()) {
            return null;
        }

        if (containsCountry(config.ap_countries(), countryName))
            return "AP";
        if (containsCountry(config.au_countries(), countryName))
            return "AU";
        if (containsCountry(config.eu_countries(), countryName))
            return "EU";
        if (containsCountry(config.japan_countries(), countryName))
            return "Japan";
        if (containsCountry(config.latam_countries(), countryName))
            return "LATAM";
        if (containsCountry(config.me_countries(), countryName))
            return "ME";
        if (containsCountry(config.other_countries(), countryName))
            return "Other";
        if (containsCountry(config.lead_submission_countries(), countryName))
            return "Lead Submission";

        return "Other"; // Default fallback if not found in specific lists, or could return null
    }

    private boolean containsCountry(String[] countries, String countryName) {
        if (countries == null) {
            return false;
        }
        for (String country : countries) {
            if (country.equalsIgnoreCase(countryName)) {
                return true;
            }
        }
        return false;
    }
}
