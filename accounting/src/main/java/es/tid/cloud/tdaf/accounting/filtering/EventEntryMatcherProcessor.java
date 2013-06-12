package es.tid.cloud.tdaf.accounting.filtering;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;

import pl.otros.logview.LogData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.regexp.Matcher;

import es.tid.cloud.tdaf.accounting.model.EventEntry;
import es.tid.cloud.tdaf.accounting.model.EventPattern;


/**
 * Matcher for event entries
 * @author AlcEngapaOrg
 *
 */
public class EventEntryMatcherProcessor implements Processor{

    public static final String MATCHER_HEADER = "EventEntryMatches";

    private EventPatternParser eventPatternParser;
    private ObjectMapper mapper = new ObjectMapper();

    public EventEntryMatcherProcessor(EventPatternParser eventPatternParser) {
        this.eventPatternParser = eventPatternParser;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) throws Exception {

        LogData logData = exchange.getIn().getBody(LogData.class);
        String message = logData.getMessage();

        for(List<EventPattern> patterns : (Collection<List<EventPattern>>)eventPatternParser.getEventPatterns().values()) {
            for(EventPattern pattern: patterns) {
                Matcher matcher = pattern.getPattern().matcher(message);
                if (matcher.matches()) {
                    exchange.getIn().getHeaders().put(MATCHER_HEADER, true);
                    EventEntry eventEntry = getEventEntry(pattern, logData, matcher);
                    exchange.getIn().setBody(eventEntry, EventEntry.class);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private EventEntry getEventEntry(EventPattern eventPattern, LogData logData, Matcher matcher) {

        EventEntry eventEntry = new EventEntry();
        BeanUtils.copyProperties(eventPattern, eventEntry);
        eventEntry.setEventInfo(MapUtils.typedMap(matcher.namedGroups(), String.class, Object.class));
        eventEntry.setTime(logData.getDate());
        eventEntry.setLogDetails(filterLogDetailsForm(logData));
        return eventEntry;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> filterLogDetailsForm(LogData logData) {
        return mapper.convertValue(logData, Map.class);
    }
}
