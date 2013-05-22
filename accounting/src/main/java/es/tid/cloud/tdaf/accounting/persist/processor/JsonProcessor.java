package es.tid.cloud.tdaf.accounting.persist.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonProcessor implements Processor {

    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) throws Exception {
        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(body);
        out.setBody(json);
    }
}
