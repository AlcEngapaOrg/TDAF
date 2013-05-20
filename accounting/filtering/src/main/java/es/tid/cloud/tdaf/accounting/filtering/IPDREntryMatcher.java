package es.tid.cloud.tdaf.accounting.filtering;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import pl.otros.logview.LogData;

import com.google.code.regexp.Matcher;


public class IPDREntryMatcher {
    private IPDREntryIdentifier ipdrIdentifier = null;
    
    public IPDREntryMatcher(IPDREntryIdentifier ipdrIdentifier) {
        this.ipdrIdentifier = ipdrIdentifier;
    }

    public Matcher matches(Exchange exchange) throws Exception {
        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());
        LogData logData = exchange.getIn().getBody(LogData.class);
        String message = logData.getMessage();
        for(List<IPRDEntryPattern> patterns : ipdrIdentifier.getPatterns().values()) {
            for(IPRDEntryPattern pattern: patterns) {
                Matcher matcher = pattern.getPattern().matcher(message);
                if (matcher.matches()) {
                    return matcher;
                }
            }
        }
        return null;
    }
}
