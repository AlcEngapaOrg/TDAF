package es.tid.cloud.tdaf.accounting.filtering.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import pl.otros.logview.LogData;

public class LogDataToHeadersProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());
        LogData logData = exchange.getIn().getBody(LogData.class);
        out.setHeader("logData.date", logData.getDate());
        out.setHeader("logData.line", logData.getLine());
        out.setHeader("logData.level", logData.getLevel().getName());
        out.setHeader("logData.message", logData.getMessage());
        out.setHeader("logData.method", logData.getMethod());
        out.setHeader("logData.NDC", logData.getNDC());
        out.setHeader("logData.note", logData.getNote());
        out.setHeader("logData.thread", logData.getThread());
        if (logData.getProperties() != null) {
            for(Map.Entry<String, String> entry:logData.getProperties().entrySet()) {
                out.setHeader("log.data.properties." + entry.getKey(), entry.getValue());
            }
        }
    }
}
