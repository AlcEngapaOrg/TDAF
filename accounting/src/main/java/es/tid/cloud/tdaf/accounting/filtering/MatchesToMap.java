package es.tid.cloud.tdaf.accounting.filtering;

import java.util.Map;

import org.apache.camel.Header;

import com.google.code.regexp.Matcher;

public class MatchesToMap {
    public Map<String, String> process(@Header(IPDREntryMatcher.MATCHER_HEADER) Matcher matcher) throws Exception {
        return matcher.namedGroups();
    }
}
