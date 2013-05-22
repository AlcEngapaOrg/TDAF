package es.tid.cloud.tdaf.accounting.filtering;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Header;


public class IPDRMatcherToMap {
    public Map<String, Object> process(@Header(IPDREntryMatcher.MATCHER_HEADER) IPRDEntryPattern pattern) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (pattern != null) {
            map.put(IPDRConstants.EVENT, pattern.getEvent());
            map.put(IPDRConstants.EVENT, pattern.getConcept());
        }
        return map;
    }
}
