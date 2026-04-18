const COUNTRY_CONFIG = {
  gdprCountries: [
    'Austria', 'Belgium', 'Bulgaria', 'Croatia', 'Denmark', 'Estonia',
    'Finland', 'Germany', 'Greece', 'Hungary', 'Iceland', 'Ireland',
    'Italy', 'Latvia', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Malta',
    'Netherlands', 'Norway', 'Poland', 'Portugal', 'Romania', 'Slovenia',
    'Sweden', 'United Kingdom'
  ],
  
  postalCodeCountries: [
    'United States of America'
  ]
};

class LocationService {
  constructor() {
    this.countriesCache = null;
    this.statesCache = {};
    this.API_BASE = '/bin/mvw/location';
    this.countriesPromise = null;
    this.statesPromises = {};
  }

  async fetchCountries() {
    if (this.countriesCache) {
      return this.countriesCache;
    }

    if (this.countriesPromise) {
      return this.countriesPromise;
    }

    this.countriesPromise = this._fetchCountriesFromAPI();
    
    try {
      const countries = await this.countriesPromise;
      this.countriesCache = countries;
      return countries;
    } finally {
      this.countriesPromise = null;
    }
  }

  async _fetchCountriesFromAPI() {
    try {
      const response = await fetch(this.API_BASE);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const countries = await response.json();
      
      return countries.map(country => ({
        countryName: country.country,
        countryCode: country.countryCode,
        hasGDPR: COUNTRY_CONFIG.gdprCountries.includes(country.country),
        hasPostalCode: COUNTRY_CONFIG.postalCodeCountries.includes(country.country)
      }));
    } catch (error) {
      console.error('Error fetching countries:', error);
      return [];
    }
  }

  async fetchStates(countryCode) {
    if (this.statesCache[countryCode]) {
      return this.statesCache[countryCode];
    }

    if (this.statesPromises[countryCode]) {
      return this.statesPromises[countryCode];
    }

    this.statesPromises[countryCode] = this._fetchStatesFromAPI(countryCode);
    
    try {
      const states = await this.statesPromises[countryCode];
      this.statesCache[countryCode] = states;
      return states;
    } finally {
      delete this.statesPromises[countryCode];
    }
  }

  async _fetchStatesFromAPI(countryCode) {
    try {
      const response = await fetch(`${this.API_BASE}?country=${countryCode}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      if (response.status === 204) {
        return [];
      }
      
      const data = await response.json();
      
      if (data && data.length > 0 && data[0].states) {
        return data[0].states.map(state => ({
          value: state.name,
          label: state.name,
          stateCode: state.stateCode
        }));
      } else {
        return [];
      }
    } catch (error) {
      console.error(`Error fetching states for ${countryCode}:`, error);
      return [];
    }
  }

  async getCountryByName(countryName) {
    const countries = await this.fetchCountries();
    return countries.find(c => c.countryName === countryName) || null;
  }

  clearCache() {
    this.countriesCache = null;
    this.statesCache = {};
    this.countriesPromise = null;
    this.statesPromises = {};
  }
}

export const locationService = new LocationService();
export { COUNTRY_CONFIG };
