package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class NewClubSpacerModelTest {

    private final AemContext context = new AemContext();

    private NewClubSpacer newClubSpacer;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(NewClubSpacer.class);
        context.load().json("/newclubspacer.json",
                "/spacer");
        context.currentResource("/spacer/spacer");
        newClubSpacer =  context.currentResource().adaptTo(NewClubSpacer.class);
    }

    @Test
    void testSpacer(){
        assertNotNull(newClubSpacer);
        assertEquals("64", newClubSpacer.getDesktopSpacing());
        assertEquals("24", newClubSpacer.getMobileSpacing());
        assertEquals("--spacer-height-desktop:64px;", newClubSpacer.getDesktopSpacingText());
        assertEquals("--spacer-height-mobile:24px;", newClubSpacer.getMobileSpacingText());
    }
}
 