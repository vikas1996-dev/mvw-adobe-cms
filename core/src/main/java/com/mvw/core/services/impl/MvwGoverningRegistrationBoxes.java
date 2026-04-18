package com.mvw.core.services.impl;

import com.mvw.core.config.MvwGoverningRegistrationBoxesConfig;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Component(service = MvwGoverningRegistrationBoxes.class, immediate = true)
@Designate(ocd = MvwGoverningRegistrationBoxesConfig.class)
public class MvwGoverningRegistrationBoxes {

    private static final Logger LOG = LoggerFactory.getLogger(MvwGoverningRegistrationBoxes.class);

    private String marriottsRegistrationBox;
    private String hyattRegistrationBox;
    private String fromAddress;

    @Activate
    @Modified
    protected void activate(MvwGoverningRegistrationBoxesConfig config) {
        this.marriottsRegistrationBox = StringUtils.trimToEmpty(config.marriotts_registration_box());
        this.hyattRegistrationBox = StringUtils.trimToEmpty(config.hyatt_registration_box());
        this.fromAddress = StringUtils.trimToEmpty(config.from_address());

        LOG.info(
                "MvwGoverningRegistrationBoxes activated. marriottBlank={} marriottCount={} marriottValue='{}' hyattBlank={} hyattCount={} hyattValue='{}' fromBlank={} fromValue='{}'",
                StringUtils.isBlank(marriottsRegistrationBox),
                countRecipients(marriottsRegistrationBox),
                marriottsRegistrationBox,
                StringUtils.isBlank(hyattRegistrationBox),
                countRecipients(hyattRegistrationBox),
                hyattRegistrationBox,
                StringUtils.isBlank(fromAddress),
                fromAddress);
    }

    private int countRecipients(String recipients) {
        if (StringUtils.isBlank(recipients)) {
            return 0;
        }

        int count = 0;
        for (String recipient : recipients.split("[,;]")) {
            if (StringUtils.isNotBlank(recipient)) {
                count++;
            }
        }
        return count;
    }
}
