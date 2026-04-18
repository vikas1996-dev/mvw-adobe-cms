package com.mvw.core.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mvw.core.models.dto.Amenities;
import com.mvw.core.models.dto.Properties;
import com.mvw.core.models.dto.Villas;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sling Model for generating JSON-LD schema for VacationRental resort pages.
 * This model creates structured data according to schema.org VacationRental specification.
 * 
 * <p>Used in resort-detail-page.html via the resort-details-schema component.
 * Data is sourced from the Jahia response set by ResortPageModel, using:
 * <ul>
 *   <li>{@link ResortDetailsModel} - resort properties (name, address, coordinates, rating, reviews, etc.)</li>
 *   <li>{@link ResortAccommodationModel} - villa/room accommodations</li>
 *   <li>{@link ResortAmenitiesModel} - featured amenities</li>
 * </ul>
 * </p>
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortSchemaModel {

    private static final Logger LOG = LoggerFactory.getLogger(ResortSchemaModel.class);

    /** Parent organization name */
    private static final String PARENT_ORG_NAME = "The Marriott Vacation Clubs";

    /** Parent organization URL */
    private static final String PARENT_ORG_URL = "https://www.marriottvacationclubs.com";

    @Self
    private ResortDetailsModel resortDetails;

    @Self
    private ResortAccommodationModel accommodationDetails;

    @Self
    private ResortAmenitiesModel amenitiesDetails;

    @SlingObject
    private SlingHttpServletRequest request;

    private String jsonLD;

    /**
     * Initializes the model by building the JSON-LD schema from resort data.
     */
    @PostConstruct
    protected void init() {
        try {
            if (resortDetails == null) {
                LOG.warn("ResortDetailsModel is null for path: {}", request.getResource().getPath());
                this.jsonLD = "{}";
                return;
            }

            List<Properties> propertiesList = resortDetails.getPropertiesList();

            if (propertiesList == null || propertiesList.isEmpty()) {
                LOG.warn("No properties found for path: {}", request.getResource().getPath());
                this.jsonLD = "{}";
                return;
            }

            Properties properties = propertiesList.get(0);
            Map<String, Object> schema = buildSchema(properties);

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            this.jsonLD = gson.toJson(schema);

        } catch (IOException e) {
            LOG.error("IOException while initializing ResortSchemaModel for path: {}", request.getResource().getPath(), e);
            this.jsonLD = "{}";
        } catch (Exception e) {
            LOG.error("Failed to initialize ResortSchemaModel for path: {}", request.getResource().getPath(), e);
            this.jsonLD = "{}";
        }
    }

    /**
     * Builds the VacationRental schema from the property data.
     *
     * @param properties the resort properties data
     * @return a map representing the JSON-LD schema
     */
    private Map<String, Object> buildSchema(Properties properties) {
        Map<String, Object> schema = new LinkedHashMap<>();

        // Use request URL as the canonical URL
        String currentUrl = request.getRequestURL().toString();

        // Core schema properties
        schema.put("@context", "https://schema.org");
        schema.put("@type", "VacationRental");
        schema.put("name", properties.getName());
        schema.put("description", properties.getDescription());
        schema.put("url", currentUrl);
        schema.put("identifier", currentUrl);

        // Images
        List<String> images = buildImageList(properties);
        schema.put("image", images);

        // Address
        Map<String, String> address = buildAddress(properties);
        schema.put("address", address);

        // Telephone
        if (StringUtils.isNotBlank(properties.getPhoneMain())) {
            schema.put("telephone", properties.getPhoneMain());
        }

        // Price Range - extracted from structuredContent
        String priceRange = extractPriceRange(properties.getStructuredContent());
        if (StringUtils.isNotBlank(priceRange)) {
            schema.put("priceRange", priceRange);
        }

        // GeoCoordinates
        Map<String, Object> geo = buildGeoCoordinates(properties);
        if (!geo.isEmpty()) {
            schema.put("geo", geo);
        }

        // Check-in/Check-out times and policies
        addTimesAndPolicies(schema, properties);

        // Aggregate Rating from Properties (rating and reviews fields)
        Map<String, Object> aggregateRating = buildAggregateRating(properties);
        if (!aggregateRating.isEmpty()) {
            schema.put("aggregateRating", aggregateRating);
        }

        // Featured Amenities
        List<Map<String, Object>> amenities = buildAmenities();
        schema.put("amenityFeature", amenities);

        // Parent Organization
        Map<String, String> parentOrg = buildParentOrganization();
        schema.put("parentOrganization", parentOrg);

        // Accommodations (contained places)
        List<Map<String, Object>> accommodations = buildAccommodations();
        schema.put("containsPlace", accommodations);

        return schema;
    }

    /**
     * Builds the list of image URLs from resort gallery.
     * Uses the same URL pattern as resort gallery components.
     *
     * @param properties the resort properties
     * @return list of image URLs
     */
    private List<String> buildImageList(Properties properties) {
        List<String> images = new ArrayList<>();

        if (properties.getGallery() != null && !properties.getGallery().isEmpty()) {
            var firstGallery = properties.getGallery().get(0);
            if (firstGallery.getSortedImages() != null) {
                for (var image : firstGallery.getSortedImages()) {
                    if (image.getPhoto() != null && !image.getPhoto().isEmpty()) {
                        var firstPhoto = image.getPhoto().get(0);
                        if (firstPhoto != null && StringUtils.isNotBlank(firstPhoto.getPath())) {
                            images.add(firstPhoto.getPath());
                        }
                    }
                }
            }
        }

        return images;
    }

    /**
     * Builds the PostalAddress object from property data.
     *
     * @param properties the resort properties
     * @return a map representing the postal address
     */
    private Map<String, String> buildAddress(Properties properties) {
        Map<String, String> address = new LinkedHashMap<>();
        address.put("@type", "PostalAddress");

        if (StringUtils.isNotBlank(properties.getAddress1())) {
            address.put("streetAddress", properties.getAddress1());
        }
        if (StringUtils.isNotBlank(properties.getCity())) {
            address.put("addressLocality", properties.getCity());
        }
        if (StringUtils.isNotBlank(properties.getRegion())) {
            address.put("addressRegion", properties.getRegion());
        }

        String postalCode = StringUtils.isNotBlank(properties.getZip()) ? properties.getZip() : "NA";
        address.put("postalCode", postalCode);

        if (StringUtils.isNotBlank(properties.getCountry())) {
            address.put("addressCountry", properties.getCountry());
        }

        return address;
    }

    /**
     * Builds the GeoCoordinates object from property data.
     *
     * @param properties the resort properties
     * @return a map representing the geo coordinates
     */
    private Map<String, Object> buildGeoCoordinates(Properties properties) {
        Map<String, Object> geo = new LinkedHashMap<>();

        String latitude = properties.getCoordinateLatitude();
        String longitude = properties.getCoordinateLongitude();

        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
            geo.put("@type", "GeoCoordinates");
            geo.put("latitude", latitude);
            geo.put("longitude", longitude);
        }

        return geo;
    }

    /**
     * Adds check-in/check-out times and policy information to the schema.
     *
     * @param schema     the schema map to update
     * @param properties the resort properties
     */
    private void addTimesAndPolicies(Map<String, Object> schema, Properties properties) {
        if (StringUtils.isNotBlank(properties.getCheckIn())) {
            schema.put("checkinTime", properties.getCheckIn());
        }
        if (StringUtils.isNotBlank(properties.getCheckOut())) {
            schema.put("checkoutTime", properties.getCheckOut());
        }
        schema.put("smokingAllowed", "false");
        schema.put("petsAllowed", "serviceAnimals");
    }

    /**
     * Builds the AggregateRating object from Properties data.
     *
     * @param properties the resort properties containing rating and reviews
     * @return a map representing the aggregate rating, or empty if no rating data
     */
    private Map<String, Object> buildAggregateRating(Properties properties) {
        Map<String, Object> aggregateRating = new LinkedHashMap<>();

        if (properties != null && StringUtils.isNotBlank(properties.getRating())) {
            aggregateRating.put("@type", "AggregateRating");
            aggregateRating.put("ratingValue", properties.getRating());
            aggregateRating.put("bestRating", "5");
            aggregateRating.put("ratingCount", properties.getReviews());
        }

        return aggregateRating;
    }

    /**
     * Builds the list of amenity features from the amenities model.
     *
     * @return list of amenity feature specifications
     */
    private List<Map<String, Object>> buildAmenities() {
        List<Map<String, Object>> amenities = new ArrayList<>();

        if (amenitiesDetails != null) {
            List<Amenities> featuredAmenities = amenitiesDetails.getFeaturedAmenities();
            if (featuredAmenities != null) {
                for (Amenities amenity : featuredAmenities) {
                    Map<String, Object> amenityMap = new LinkedHashMap<>();
                    amenityMap.put("@type", "LocationFeatureSpecification");
                    amenityMap.put("name", amenity.getName());
                    amenityMap.put("value", true);
                    amenities.add(amenityMap);
                }
            }
        }

        return amenities;
    }

    /**
     * Builds the parent organization object.
     *
     * @return a map representing the parent organization
     */
    private Map<String, String> buildParentOrganization() {
        Map<String, String> parentOrg = new LinkedHashMap<>();
        parentOrg.put("@type", "Organization");
        parentOrg.put("name", PARENT_ORG_NAME);
        parentOrg.put("url", PARENT_ORG_URL);
        return parentOrg;
    }

    /**
     * Builds the list of accommodation objects from the accommodation model.
     *
     * @return list of accommodation entries for containsPlace
     */
    private List<Map<String, Object>> buildAccommodations() {
        List<Map<String, Object>> accommodations = new ArrayList<>();

        if (accommodationDetails == null) {
            return accommodations;
        }

        try {
            List<Villas> villasList = accommodationDetails.getPropertiesList();
            if (villasList != null) {
                for (Villas room : villasList) {
                    Map<String, Object> roomMap = buildAccommodationEntry(room);
                    accommodations.add(roomMap);
                }
            }
        } catch (IOException e) {
            LOG.debug("Could not build accommodations list", e);
        }

        return accommodations;
    }

    /**
     * Builds a single accommodation entry from villa data.
     *
     * @param room the villa/room data
     * @return a map representing the accommodation
     */
    private Map<String, Object> buildAccommodationEntry(Villas room) {
        Map<String, Object> roomMap = new LinkedHashMap<>();
        roomMap.put("@type", "Accommodation");
        roomMap.put("additionalType", "EntirePlace");
        roomMap.put("name", room.getName());
        roomMap.put("description", room.getDescription());

        // Calculate number of bedrooms from name
        int beds = calculateBedroomCount(room.getName());
        roomMap.put("numberOfBedrooms", beds);

        // Floor size
        if (room.getSquareFootage() != null) {
            Map<String, Object> floorSize = new LinkedHashMap<>();
            floorSize.put("@type", "QuantitativeValue");
            floorSize.put("value", room.getSquareFootage());
            roomMap.put("floorSize", floorSize);
        }

        // Occupancy
        Map<String, Object> occupancy = new LinkedHashMap<>();
        occupancy.put("@type", "QuantitativeValue");
        occupancy.put("value", room.getSleeps());
        roomMap.put("occupancy", occupancy);

        // Bed details
        List<Map<String, Object>> bedDetails = buildBedDetails(room.getDescription());
        roomMap.put("bed", bedDetails);

        return roomMap;
    }

    /**
     * Calculates the number of bedrooms from the room name.
     * Supports multiple naming conventions from Jahia data:
     * - "Three-Bedroom", "3-Bedroom", "3 Bedroom"
     * - "Two-Bedroom", "2-Bedroom", "2 Bedroom"
     * - "One-Bedroom", "1-Bedroom", "1 Bedroom"
     * - "Four-Bedroom", "4-Bedroom", "4 Bedroom"
     * - "Five-Bedroom", "5-Bedroom", "5 Bedroom"
     * - "Studio" (returns 0)
     *
     * @param roomName the name of the room/villa
     * @return the number of bedrooms
     */
    private int calculateBedroomCount(String roomName) {
        if (StringUtils.isBlank(roomName)) {
            return 1;
        }

        String name = roomName.toLowerCase();

        // Check for studio (0 bedrooms)
        if (name.contains("studio")) {
            return 0;
        }

        // Check for five bedrooms
        if (name.contains("five-bedroom") || name.contains("5-bedroom") || name.contains("5 bedroom")) {
            return 5;
        }

        // Check for four bedrooms
        if (name.contains("four-bedroom") || name.contains("4-bedroom") || name.contains("4 bedroom")) {
            return 4;
        }

        // Check for three bedrooms
        if (name.contains("three-bedroom") || name.contains("3-bedroom") || name.contains("3 bedroom")) {
            return 3;
        }

        // Check for two bedrooms
        if (name.contains("two-bedroom") || name.contains("2-bedroom") || name.contains("2 bedroom")) {
            return 2;
        }

        // Check for one bedroom (explicit)
        if (name.contains("one-bedroom") || name.contains("1-bedroom") || name.contains("1 bedroom")) {
            return 1;
        }

        // Default to 1 bedroom if not specified
        return 1;
    }

    /**
     * Builds the list of bed details from the room description.
     * Parses the description to identify bed types from the Accommodations section.
     * 
     * <p>Handles structured formats like:
     * <pre>
     * Accommodations
     * Bedroom 1: 1 King bed
     * Bedroom 2: 2 Queen beds
     * Pull-out sofa bed
     * </pre>
     * 
     * Supported bed types: King, Queen, Twin, Double, Full, Murphy, Bunk, 
     * Daybed, Trundle, Rollaway, SofaBed, Crib
     *
     * @param description the room description
     * @return list of bed detail entries
     */
    private List<Map<String, Object>> buildBedDetails(String description) {
        List<Map<String, Object>> bedDetails = new ArrayList<>();

        if (StringUtils.isBlank(description)) {
            return bedDetails;
        }

        // Extract Accommodations section if present
        String accommodationsSection = extractAccommodationsSection(description);
        String textToParse = StringUtils.isNotBlank(accommodationsSection) ? accommodationsSection : description;

        // Parse structured bed entries (e.g., "Bedroom 1: 1 King bed", "2 Queen beds")
        parseStructuredBedEntries(textToParse, bedDetails);

        // If no structured entries found, fall back to keyword-based detection
        if (bedDetails.isEmpty()) {
            parseKeywordBasedBeds(description.toLowerCase(), bedDetails);
        }

        return bedDetails;
    }

    /**
     * Extracts the Accommodations section from the description.
     * Looks for content between "Accommodations" header and the next section header (e.g., "KITCHEN").
     *
     * @param description the full description
     * @return the accommodations section text, or null if not found
     */
    private String extractAccommodationsSection(String description) {
        String descLower = description.toLowerCase();
        
        // Find the start of Accommodations section
        int startIndex = descLower.indexOf("accommodations");
        if (startIndex == -1) {
            // Try alternate marker
            startIndex = descLower.indexOf("bedroom 1:");
            if (startIndex == -1) {
                return null;
            }
        } else {
            // Move past the "Accommodations" header
            startIndex += "accommodations".length();
        }

        // Find the end - look for next section headers
        String[] sectionMarkers = {"kitchen", "general amenities", "bathroom", "living", "dining", "entertainment"};
        int endIndex = description.length();
        
        for (String marker : sectionMarkers) {
            int markerIndex = descLower.indexOf(marker, startIndex);
            if (markerIndex != -1 && markerIndex < endIndex) {
                endIndex = markerIndex;
            }
        }

        return description.substring(startIndex, endIndex).trim();
    }

    /**
     * Parses structured bed entries from the accommodations section.
     * Handles formats like:
     * - "Bedroom 1: 1 King bed"
     * - "2 Queen beds"
     * - "Pull-out sofa bed"
     *
     * @param text       the text to parse
     * @param bedDetails the list to add bed entries to
     */
    private void parseStructuredBedEntries(String text, List<Map<String, Object>> bedDetails) {
        if (StringUtils.isBlank(text)) {
            return;
        }

        String textLower = text.toLowerCase();

        // Map to accumulate bed counts by type
        Map<String, Integer> bedCounts = new LinkedHashMap<>();

        // Pattern for "Bedroom X: N Type bed" format (e.g., "Bedroom 1: 1 King bed")
        java.util.regex.Pattern bedroomPattern = java.util.regex.Pattern.compile(
            "bedroom\\s*\\d*\\s*:?\\s*(\\d+)\\s+(king|queen|twin|double|full|murphy|bunk|daybed|day bed|trundle|rollaway)\\s*beds?",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        
        java.util.regex.Matcher bedroomMatcher = bedroomPattern.matcher(text);
        while (bedroomMatcher.find()) {
            int count = Integer.parseInt(bedroomMatcher.group(1));
            String bedType = normalizeBedType(bedroomMatcher.group(2));
            bedCounts.merge(bedType, count, Integer::sum);
        }

        // Pattern for standalone "N Type bed(s)" format (e.g., "2 Queen beds")
        java.util.regex.Pattern standalonePattern = java.util.regex.Pattern.compile(
            "(?<!bedroom\\s*\\d*\\s*:?\\s*)(\\d+)\\s+(king|queen|twin|double|full|murphy|bunk|daybed|day bed|trundle|rollaway)\\s*beds?",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        
        java.util.regex.Matcher standaloneMatcher = standalonePattern.matcher(text);
        while (standaloneMatcher.find()) {
            int count = Integer.parseInt(standaloneMatcher.group(1));
            String bedType = normalizeBedType(standaloneMatcher.group(2));
            // Only add if not already counted from bedroom pattern
            if (!bedCounts.containsKey(bedType)) {
                bedCounts.merge(bedType, count, Integer::sum);
            }
        }

        // Handle sofa bed variations
        if (textLower.contains("sofa bed") || textLower.contains("sofabed") || 
            textLower.contains("pull-out sofa") || textLower.contains("pullout sofa") ||
            textLower.contains("sleeper sofa") || textLower.contains("sofa sleeper")) {
            bedCounts.merge("SofaBed", 1, Integer::sum);
        }

        // Handle crib
        if (textLower.contains("crib")) {
            bedCounts.merge("Crib", 1, Integer::sum);
        }

        // Handle rollaway variations
        if (textLower.contains("rollaway") || textLower.contains("roll-away") || textLower.contains("roll away")) {
            if (!bedCounts.containsKey("Rollaway")) {
                bedCounts.merge("Rollaway", 1, Integer::sum);
            }
        }

        // Add all accumulated beds to the list
        for (Map.Entry<String, Integer> entry : bedCounts.entrySet()) {
            addBed(bedDetails, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Normalizes bed type string to standard format.
     *
     * @param bedType the raw bed type from parsing
     * @return normalized bed type name
     */
    private String normalizeBedType(String bedType) {
        String type = bedType.toLowerCase().trim();
        switch (type) {
            case "king": return "King";
            case "queen": return "Queen";
            case "twin": return "Twin";
            case "double": return "Double";
            case "full": return "Full";
            case "murphy": return "Murphy";
            case "bunk": return "Bunk";
            case "daybed":
            case "day bed": return "Daybed";
            case "trundle": return "Trundle";
            case "rollaway": return "Rollaway";
            default: return StringUtils.capitalize(type);
        }
    }

    /**
     * Fallback keyword-based bed detection for unstructured descriptions.
     *
     * @param descLower  lowercase description
     * @param bedDetails the list to add bed entries to
     */
    private void parseKeywordBasedBeds(String descLower, List<Map<String, Object>> bedDetails) {
        // King bed
        if (descLower.contains("king")) {
            addBed(bedDetails, "King", 1);
        }

        // Queen bed
        if (descLower.contains("queen")) {
            addBed(bedDetails, "Queen", 1);
        }

        // Sofa bed variations
        if (descLower.contains("sofa bed") || descLower.contains("sofabed") || 
            descLower.contains("sleeper sofa") || descLower.contains("sofa sleeper") ||
            descLower.contains("pull-out sofa") || descLower.contains("pullout sofa")) {
            addBed(bedDetails, "SofaBed", 1);
        }

        // Twin bed
        if (descLower.contains("twin")) {
            addBed(bedDetails, "Twin", 1);
        }

        // Double bed
        if (descLower.contains("double bed") || descLower.contains("double-size")) {
            addBed(bedDetails, "Double", 1);
        }

        // Full bed
        if ((descLower.contains("full bed") || descLower.contains("full-size") || descLower.contains("full size bed"))
                && !descLower.contains("double")) {
            addBed(bedDetails, "Full", 1);
        }

        // Murphy bed
        if (descLower.contains("murphy") || descLower.contains("wall bed")) {
            addBed(bedDetails, "Murphy", 1);
        }

        // Bunk bed
        if (descLower.contains("bunk")) {
            addBed(bedDetails, "Bunk", 1);
        }

        // Daybed
        if (descLower.contains("daybed") || descLower.contains("day bed")) {
            addBed(bedDetails, "Daybed", 1);
        }

        // Trundle bed
        if (descLower.contains("trundle")) {
            addBed(bedDetails, "Trundle", 1);
        }

        // Rollaway bed
        if (descLower.contains("rollaway") || descLower.contains("roll-away") || descLower.contains("roll away")) {
            addBed(bedDetails, "Rollaway", 1);
        }

        // Crib
        if (descLower.contains("crib")) {
            addBed(bedDetails, "Crib", 1);
        }
    }

    /**
     * Adds a bed entry to the bed details list.
     *
     * @param list  the list to add to
     * @param type  the type of bed
     * @param count the number of beds of this type
     */
    private void addBed(List<Map<String, Object>> list, String type, int count) {
        Map<String, Object> bed = new LinkedHashMap<>();
        bed.put("@type", "BedDetails");
        bed.put("typeOfBed", type);
        bed.put("numberOfBeds", count);
        list.add(bed);
    }

    /**
     * Extracts priceRange value from structuredContent list.
     * Looks for entries like "priceRange=$$$" and returns the value.
     *
     * @param structuredContent the list of structured content strings
     * @return the price range value, or null if not found
     */
    private String extractPriceRange(List<String> structuredContent) {
        if (structuredContent == null || structuredContent.isEmpty()) {
            return null;
        }

        for (String content : structuredContent) {
            if (StringUtils.isNotBlank(content) && content.startsWith("priceRange=")) {
                return content.substring("priceRange=".length());
            }
        }

        return null;
    }

    /**
     * Returns the generated JSON-LD string for the VacationRental schema.
     *
     * @return the JSON-LD string
     */
    public String getJsonLD() {
        return jsonLD;
    }
}
