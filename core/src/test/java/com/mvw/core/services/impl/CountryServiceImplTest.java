// package com.mvw.core.services.impl;



// import com.mvw.core.config.CountryApiConfig;
// import com.mvw.core.models.dto.CountryDTO;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;

// import static org.junit.jupiter.api.Assertions.*;

// import java.util.List;

// class CountryServiceImplTest {

//     private CountryServiceImpl countryService;

//     @BeforeEach
//     void setUp() {
//         countryService = Mockito.spy(new CountryServiceImpl());

//         CountryApiConfig config = Mockito.mock(CountryApiConfig.class);

//         Mockito.when(config.apiUrl())
//                 .thenReturn("https://dummy-url.com/countries");
//         Mockito.when(config.jwtToken())
//                 .thenReturn("dummy-token");

//         countryService.activate(config);
//     }

//     @Test
//     void testGetStates_NullCountryCode() {
//        List<CountryDTO>  response = countryService.getStates(null);
//         assertEquals("[]", response);
//     }

//     @Test
//     void testGetStates_EmptyCountryCode() {
//         List<CountryDTO> response = countryService.getStates("");
//         assertEquals("[]", response);
//     }

//     @Test
//     void testServiceActivation() {
//         assertNotNull(countryService);
//     }
// }

