package com.thetechprepper.emcommtools.api.service.template;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleTemplateEngine {

    private static final Pattern TOKEN =
            Pattern.compile("\\$\\{([A-Z0-9_]+)\\}");

    public static String renderStrict(String template, Map<String, String> values) {
        Matcher matcher = TOKEN.matcher(template);

        Set<String> requiredKeys = matcher.results()
                .map(m -> m.group(1))
                .collect(Collectors.toSet());

        Set<String> missing = requiredKeys.stream()
                .filter(k -> !values.containsKey(k))
                .collect(Collectors.toSet());

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                "Missing template variables: " + missing
            );
        }

        return render(template, values);
    }

    private static String render(String template, Map<String, String> values) {
        Matcher matcher = TOKEN.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(
                sb,
                Matcher.quoteReplacement(values.get(matcher.group(1)))
            );
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
