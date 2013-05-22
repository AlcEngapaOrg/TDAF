package es.tid.cloud.tdaf.accounting.filtering;

import java.util.Map;

import org.apache.camel.Exchange;

import com.google.code.regexp.Matcher;

public class MatchesToMap {
    public Map<String, String> process(Exchange exchange) throws Exception {
        Matcher matcher = exchange.getIn().getHeader(IPDREntryMatcher.MATCHER_HEADER, Matcher.class);
        return matcher.namedGroups();
    }
}
