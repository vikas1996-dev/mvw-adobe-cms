// package com.mvw.core.servlets;

// import com.mvw.core.services.CountryService;
// import com.mvw.core.models.dto.CountryDTO;

// import io.wcm.testing.mock.aem.junit5.AemContext;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.RegisterExtension;

// import java.util.Collections;
// import java.util.List;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// class LocationServletTest {

//     @RegisterExtension
//     final AemContext context = new AemContext();

//     private CountryService countryService;
//     private LocationServlet servlet;

//     @BeforeEach
//     void setUp() {

//         // Mock the service
//         countryService = mock(CountryService.class);

//         // Create servlet instance
//         servlet = new LocationServlet();

//         // Inject service using AEM Mocks
//         context.registerService(CountryService.class, countryService);
//         context.registerInjectActivateService(servlet);
//     }

//     @Test
//     void testGetCountries() throws Exception {

//         // Prepare mock response
//         CountryDTO country = mock(CountryDTO.class);
//         List<CountryDTO> countries = Collections.singletonList(country);

//         when(countryService.getCountries()).thenReturn(countries);

//         // Call servlet
//         servlet.doGet(context.request(), context.response());

//         // Verify service was called
//         verify(countryService, times(1)).getCountries();

//         // Assert response status
//         assertEquals(200, context.response().getStatus());

//         // Assert response content
//         String output = context.response().getOutputAsString();
//         assertTrue(output.contains("{}")); // since we used a mock DTO
//     }

//     @Test
//     void testGetStates() throws Exception {

//         context.request().setParameterMap(
//                 Collections.singletonMap("country", "USA")
//         );

//         CountryDTO stateDTO = mock(CountryDTO.class);
//         List<CountryDTO> states = Collections.singletonList(stateDTO);

//         when(countryService.getStates("USA")).thenReturn(states);

//         // Call servlet
//         servlet.doGet(context.request(), context.response());

//         // Verify service call
//         verify(countryService, times(1)).getStates("USA");
//         verify(countryService, never()).getCountries();

//         // Assert status
//         assertEquals(200, context.response().getStatus());

//         // Assert output contains mock JSON
//         String output = context.response().getOutputAsString();
//         assertTrue(output.contains("{}"));
//     }

//     @Test
//     void testServiceThrowsException() throws Exception {

//         // Simulate exception in service
//         when(countryService.getCountries()).thenThrow(new RuntimeException("API failure"));

//         servlet.doGet(context.request(), context.response());

//         // Verify status is 500
//         assertEquals(500, context.response().getStatus());

//         // Verify error JSON
//         String output = context.response().getOutputAsString();
//         assertTrue(output.contains("Unable to fetch data"));
//     }
// }
