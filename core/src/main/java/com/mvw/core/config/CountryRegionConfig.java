package com.mvw.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "MVW Country Region Configuration", description = "Configuration for Country and Region mappings")
public @interface CountryRegionConfig {

    @AttributeDefinition(name = "AP Countries", description = "List of countries in AP region")
    String[] ap_countries() default {
            "China", "Hong Kong", "India", "Indonesia", "Philippines", "Singapore", "Thailand"
    };

    @AttributeDefinition(name = "AU Countries", description = "List of countries in AU region")
    String[] au_countries() default {
            "New Zealand", "Australia"
    };

    @AttributeDefinition(name = "EU Countries", description = "List of countries in EU region")
    String[] eu_countries() default {
            "Austria", "Belgium", "Bulgaria", "Croatia", "Cyprus", "Czech Republic", "Denmark", "Estonia", "Finland",
            "France", "Germany", "Greece", "Hungary", "Ireland", "Italy", "Latvia", "Lithuania", "Luxembourg", "Malta",
            "Netherlands", "Norway", "Poland", "Portugal", "Romania", "Slovakia", "Slovenia", "Spain", "Sweden",
            "Switzerland", "United Kingdom"
    };

    @AttributeDefinition(name = "Japan Countries", description = "List of countries in Japan region")
    String[] japan_countries() default {
            "Japan"
    };

    @AttributeDefinition(name = "LATAM Countries", description = "List of countries in LATAM region")
    String[] latam_countries() default {
            "Argentina", "Bolivia", "Chile", "Colombia", "Costa Rica", "Dominican Republic", "Ecuador", "El Salvador",
            "Guatemala", "Honduras", "Mexico", "Nicaragua", "Panama", "Paraguay", "Peru", "Trinidad and Tobago",
            "Venezuela"
    };

    @AttributeDefinition(name = "Lead Submission Countries", description = "List of countries for Lead Submission")
    String[] lead_submission_countries() default {
            "usa", "c6"
    };

    @AttributeDefinition(name = "ME Countries", description = "List of countries in ME region")
    String[] me_countries() default {
            "Oman", "Qatar"
    };

    @AttributeDefinition(name = "Other Countries", description = "List of countries in Other region")
    String[] other_countries() default {
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica",
            "Antigua and Barbuda", "Armenia", "Aruba", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
            "Belarus", "Belize", "Benin", "Bermuda", "Bhutan", "Bosnia and Herzegovina", "Botswana", "Bouvet Island",
            "Brazil", "British Virgin Islands", "British West Indies", "Brunei Darussalam", "Burkina Faso",
            "Burma Myanmar",
            "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic",
            "Chad",
            "Christmas Island", "Cocos Islands", "Comoros", "Congo", "Cook Islands", "Cuba", "Curacao", "Djibouti",
            "Dominica", "East Timor", "Egypt", "Equatorial Guinea", "Eritrea", "Ethiopia", "Falkland Islands",
            "Faroe Islands", "Fiji", "French Guiana", "French Polynesia", "Gabon", "Gambia", "Georgia", "Ghana",
            "Gibraltar", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guinea", "Guinea-Bissau", "Guyana", "Haiti",
            "Heard and McDonald Islands", "Iceland", "Iran", "Iraq", "Israel", "Ivory Coast", "Jamaica", "Jordan",
            "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Lebanon", "Lesotho", "Liberia",
            "Libyan Arab Jamahiriya", "Liechtenstein", "Macau, China", "Macedonia", "Madagascar", "Malawi", "Malaysia",
            "Maldives", "Mali", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Micronesia",
            "Moldova", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Namibia", "Nauru", "Nepal",
            "New Caledonia", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Mariana Islands",
            "Pakistan", "Palau", "Palestine", "Papua New Guinea", "Peoples Republic Of Korea", "Pitcairn Islands",
            "Reunion", "Russia", "Rwanda", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia",
            "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino",
            "Sao Tome and Principe",
            "Saudi Arabia", "Senegal", "Serbia", "Serbia Montenegro", "Seychelles", "Sierra", "Sierra Leone",
            "Sint Maarten (Dutch part)", "Solomon Islands", "Somalia", "South Africa",
            "South Georgia and South Sandwich Islands", "South Korea", "Sri Lanka", "Sudan", "Suriname",
            "Svalbard and Jan Mayen", "Swaziland", "Syria", "Taiwan, China", "Tajikistan", "Tanzania",
            "The Democratic Republic of Congo", "Togo", "Tokelau", "Tonga", "Tunisia", "Turkey", "Turkmenistan",
            "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "Uruguay",
            "US Virgin Islands", "Uzbekistan", "Vanuatu", "Vatican City", "Vietnam", "Wallis and Futuna Islands",
            "Western Sahara", "Yemen", "Zambia", "Zimbabwe"
    };
}
