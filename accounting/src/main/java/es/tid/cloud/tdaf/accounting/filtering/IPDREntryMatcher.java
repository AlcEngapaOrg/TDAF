package es.tid.cloud.tdaf.accounting.filtering;

import java.util.List;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.OutHeaders;

import pl.otros.logview.LogData;

import com.google.code.regexp.Matcher;



public class IPDREntryMatcher {
    public static final String IPDR_PATTERN_HEADER = "IPRDEntryPattern";
    public static final String MATCHER_HEADER = "REG_EXP_MATCHER";

    private IPDREntryIdentifier ipdrIdentifier = null;
    
    public IPDREntryMatcher(IPDREntryIdentifier ipdrIdentifier) {
        this.ipdrIdentifier = ipdrIdentifier;
    }

    public Matcher matches(@Body LogData logData, @OutHeaders Map<String, Object> outHeaders) throws Exception {
        String message = logData.getMessage();
        for(List<IPRDEntryPattern> patterns : ipdrIdentifier.getPatterns().values()) {
            for(IPRDEntryPattern pattern: patterns) {
                Matcher matcher = pattern.getPattern().matcher(message);
                if (matcher.matches()) {
                    outHeaders.put(IPDR_PATTERN_HEADER, pattern);
                    outHeaders.put(MATCHER_HEADER, matcher);
                    return matcher;
                }
            }
        }
        return null;
    }
}
