package es.tid.cloud.tdaf.accounting.filtering.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.google.code.regexp.Matcher;

public class Matches2HeadersProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());
        Matcher matcher = out.getHeader("matcher", Matcher.class);
        out.getHeaders().putAll(matcher.namedGroups());
    }
}
