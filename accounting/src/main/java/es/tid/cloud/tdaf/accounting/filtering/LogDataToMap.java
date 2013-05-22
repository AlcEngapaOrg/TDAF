package es.tid.cloud.tdaf.accounting.filtering;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;

import pl.otros.logview.LogData;

public class LogDataToMap {
    public Map<String, Object> process(Exchange exchange) throws Exception {
        LogData logData = exchange.getIn().getBody(LogData.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(IPDRConstants.TIMESTAMP, logData.getDate());
//        map.put("logData.line", logData.getLine());
//        map.put("logData.level", logData.getLevel().getName());
//        map.put("logData.message", logData.getMessage());
//        map.put("logData.method", logData.getMethod());
//        map.put("logData.NDC", logData.getNDC());
//        map.put("logData.note", logData.getNote());
//        map.put("logData.thread", logData.getThread());
        if (logData.getProperties() != null) {
            for(Map.Entry<String, String> entry:logData.getProperties().entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }
}
