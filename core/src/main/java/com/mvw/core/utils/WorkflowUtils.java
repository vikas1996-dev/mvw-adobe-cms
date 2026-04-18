package com.mvw.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkflowUtils {

    public static Map<String, String> parseArgumentsToMap(String argsString) {
        if (StringUtils.isBlank(argsString)) return Collections.emptyMap();
        return Arrays.stream(argsString.split(","))
            .map(String::trim)
            .map(pair -> pair.split("=", 2))
            .filter(p -> p.length == 2)
            .collect(Collectors.toMap(
                p -> p[0].trim(),
                p -> p[1].trim(),
                (existing, replacement) -> existing
            ));
    }
}
