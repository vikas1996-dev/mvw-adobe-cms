package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class BubbleImagesModelTest {

    private final AemContext context = new AemContext();
    private MultiBubbleModel model;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(MultiBubbleModel.class);
    }

    @Test
    void testGetBgColor() {
        context.create().resource("/content/test", "bgColor", "teal");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("teal", model.getBgColor());
    }

    @Test
    void testGetBgColorNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getBgColor());
    }

    @Test
    void testGetBgImage() {
        context.create().resource("/content/test", "bgImage", "/content/dam/image.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image.jpg", model.getBgImage());
    }

    @Test
    void testGetBgImageNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getBgImage());
    }

    @Test
    void testGetBgImageTransparency() {
        context.create().resource("/content/test", "bgImageTransparency", 50);
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals(50, model.getBgImageTransparency());
    }

    @Test
    void testGetBgImageTransparencyNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getBgImageTransparency());
    }

    @Test
    void testGetHeadlineText() {
        context.create().resource("/content/test", "headlineText", "Test Headline");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Test Headline", model.getHeadlineText());
    }

    @Test
    void testGetHeadlineTextNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getHeadlineText());
    }

    @Test
    void testGetHeadlineSize() {
        context.create().resource("/content/test", "headlineSize", "heading3");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("heading3", model.getHeadlineSize());
    }

    @Test
    void testGetHeadlineSizeNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getHeadlineSize());
    }

    @Test
    void testGetHeadlineAlignment() {
        context.create().resource("/content/test", "headlineAlignment", "center");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("center", model.getHeadlineAlignment());
    }

    @Test
    void testGetHeadlineAlignmentNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getHeadlineAlignment());
    }

    @Test
    void testGetDescription() {
        context.create().resource("/content/test", "description", "<p>Test Description</p>");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("<p>Test Description</p>", model.getDescription());
    }

    @Test
    void testGetDescriptionNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getDescription());
    }

    @Test
    void testGetDescriptionAlignment() {
        context.create().resource("/content/test", "descriptionAlignment", "left");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("left", model.getDescriptionAlignment());
    }

    @Test
    void testGetDescriptionAlignmentNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getDescriptionAlignment());
    }

    @Test
    void testGetImage1() {
        context.create().resource("/content/test", "image1", "/content/dam/image1.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image1.jpg", model.getImage1());
    }

    @Test
    void testGetImage1Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getImage1());
    }

    @Test
    void testGetAltText1() {
        context.create().resource("/content/test", "altText1", "Alt text for image 1");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Alt text for image 1", model.getAltText1());
    }

    @Test
    void testGetAltText1Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getAltText1());
    }

    @Test
    void testGetImage2() {
        context.create().resource("/content/test", "image2", "/content/dam/image2.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image2.jpg", model.getImage2());
    }

    @Test
    void testGetImage2Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getImage2());
    }

    @Test
    void testGetAltText2() {
        context.create().resource("/content/test", "altText2", "Alt text for image 2");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Alt text for image 2", model.getAltText2());
    }

    @Test
    void testGetAltText2Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getAltText2());
    }

    @Test
    void testGetImage3() {
        context.create().resource("/content/test", "image3", "/content/dam/image3.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image3.jpg", model.getImage3());
    }

    @Test
    void testGetImage3Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getImage3());
    }

    @Test
    void testGetAltText3() {
        context.create().resource("/content/test", "altText3", "Alt text for image 3");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Alt text for image 3", model.getAltText3());
    }

    @Test
    void testGetAltText3Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getAltText3());
    }

    @Test
    void testGetImage4() {
        context.create().resource("/content/test", "image4", "/content/dam/image4.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image4.jpg", model.getImage4());
    }

    @Test
    void testGetImage4Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getImage4());
    }

    @Test
    void testGetAltText4() {
        context.create().resource("/content/test", "altText4", "Alt text for image 4");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Alt text for image 4", model.getAltText4());
    }

    @Test
    void testGetAltText4Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getAltText4());
    }

    @Test
    void testGetImage5() {
        context.create().resource("/content/test", "image5", "/content/dam/image5.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image5.jpg", model.getImage5());
    }

    @Test
    void testGetImage5Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getImage5());
    }

    @Test
    void testGetAltText5() {
        context.create().resource("/content/test", "altText5", "Alt text for image 5");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Alt text for image 5", model.getAltText5());
    }

    @Test
    void testGetAltText5Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getAltText5());
    }

    @Test
    void testGetImage6() {
        context.create().resource("/content/test", "image6", "/content/dam/image6.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image6.jpg", model.getImage6());
    }

    @Test
    void testGetImage6Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getImage6());
    }

    @Test
    void testGetAltText6() {
        context.create().resource("/content/test", "altText6", "Alt text for image 6");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Alt text for image 6", model.getAltText6());
    }

    @Test
    void testGetAltText6Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getAltText6());
    }

    @Test
    void testGetImage7() {
        context.create().resource("/content/test", "image7", "/content/dam/image7.jpg");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("/content/dam/image7.jpg", model.getImage7());
    }

    @Test
    void testGetImage7Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getImage7());
    }

    @Test
    void testGetAltText7() {
        context.create().resource("/content/test", "altText7", "Alt text for image 7");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("Alt text for image 7", model.getAltText7());
    }

    @Test
    void testGetAltText7Null() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getAltText7());
    }

    @Test
    void testGetMobileHeadlineAlignment() {
        context.create().resource("/content/test", "mobileHeadlineAlignment", "left");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("left", model.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileHeadlineAlignmentNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignment() {
        context.create().resource("/content/test", "mobileDescriptionAlignment", "right");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("right", model.getMobileDescriptionAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignmentNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertNull(model.getMobileDescriptionAlignment());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        context.create().resource("/content/test", "headlineSize", "heading2");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("h2", model.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        context.create().resource("/content/test", "headlineSize", "heading3");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("h3", model.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        context.create().resource("/content/test", "headlineSize", "heading4");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("h4", model.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        context.create().resource("/content/test", "headlineSize", "heading5");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("h5", model.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        context.create().resource("/content/test", "headlineSize", "heading6");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("h6", model.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidValue() {
        context.create().resource("/content/test", "headlineSize", "invalidheading");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("h2", model.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWhenNull() {
        context.create().resource("/content/test");
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("h2", model.getHeadlineTag());
    }

    @Test
    void testModelWithAllFieldsPopulated() {
        context.create().resource("/content/test",
            "bgColor", "teal",
            "bgImage", "/content/dam/bg.jpg",
            "bgImageTransparency", 75,
            "headlineText", "Complete Test",
            "headlineSize", "heading3",
            "headlineAlignment", "center",
            "description", "<p>Full description</p>",
            "descriptionAlignment", "left",
            "image1", "/content/dam/img1.jpg",
            "altText1", "Alt 1",
            "image2", "/content/dam/img2.jpg",
            "altText2", "Alt 2",
            "image3", "/content/dam/img3.jpg",
            "altText3", "Alt 3",
            "image4", "/content/dam/img4.jpg",
            "altText4", "Alt 4",
            "image5", "/content/dam/img5.jpg",
            "altText5", "Alt 5",
            "image6", "/content/dam/img6.jpg",
            "altText6", "Alt 6",
            "image7", "/content/dam/img7.jpg",
            "altText7", "Alt 7",
            "mobileHeadlineAlignment", "right",
            "mobileDescriptionAlignment", "center"
        );
        
        Resource resource = context.resourceResolver().getResource("/content/test");
        model = resource.adaptTo(MultiBubbleModel.class);
        
        assertNotNull(model);
        assertEquals("teal", model.getBgColor());
        assertEquals("/content/dam/bg.jpg", model.getBgImage());
        assertEquals(75, model.getBgImageTransparency());
        assertEquals("Complete Test", model.getHeadlineText());
        assertEquals("heading3", model.getHeadlineSize());
        assertEquals("center", model.getHeadlineAlignment());
        assertEquals("<p>Full description</p>", model.getDescription());
        assertEquals("left", model.getDescriptionAlignment());
        assertEquals("/content/dam/img1.jpg", model.getImage1());
        assertEquals("Alt 1", model.getAltText1());
        assertEquals("/content/dam/img2.jpg", model.getImage2());
        assertEquals("Alt 2", model.getAltText2());
        assertEquals("/content/dam/img3.jpg", model.getImage3());
        assertEquals("Alt 3", model.getAltText3());
        assertEquals("/content/dam/img4.jpg", model.getImage4());
        assertEquals("Alt 4", model.getAltText4());
        assertEquals("/content/dam/img5.jpg", model.getImage5());
        assertEquals("Alt 5", model.getAltText5());
        assertEquals("/content/dam/img6.jpg", model.getImage6());
        assertEquals("Alt 6", model.getAltText6());
        assertEquals("/content/dam/img7.jpg", model.getImage7());
        assertEquals("Alt 7", model.getAltText7());
        assertEquals("right", model.getMobileHeadlineAlignment());
        assertEquals("center", model.getMobileDescriptionAlignment());
        assertEquals("h3", model.getHeadlineTag());
    }
}