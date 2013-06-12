package es.tid.cloud.tdaf.accounting.filtering.csv;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

import es.tid.cloud.tdaf.accounting.filtering.EventPatternParser;
import es.tid.cloud.tdaf.accounting.model.EventBase;
import es.tid.cloud.tdaf.accounting.model.EventPattern;

/**
 * Extract envent fields from CSV file
 * @author AlcEngapaOrg
 *
 */
public class EventCSVParser implements EventPatternParser {

    private File file = null;
    private MultiKeyMap eventPatternEntriesMap = MultiKeyMap.decorate(new LinkedMap());

    private EventPattern eventEntryFilter = null;
    private EventCSVEntryParser eventCSVEntryParser = null;

    public EventCSVParser() {
        super();
    }
    
    public EventCSVParser(File file, EventPattern eventEntryFilter,
            EventCSVEntryParser eventCSVEntryParser) {
        super();
        this.file = file;
        this.eventEntryFilter = eventEntryFilter;
        this.eventCSVEntryParser = eventCSVEntryParser;
    }

    public EventCSVEntryParser getEventCSVEntryParser() {
        return eventCSVEntryParser;
    }

    public void setEventCSVEntryParser(EventCSVEntryParser eventCSVEntryParser) {
        this.eventCSVEntryParser = eventCSVEntryParser;
    }

    public EventBase getEventEntryFilter() {
        return eventEntryFilter;
    }

    /**
     * Instance for filtering entries
     * @param eventEntryFilter
     */
    public void setAccountingEventEntryFilter(EventPattern eventEntryFilter) {
        this.eventEntryFilter = eventEntryFilter;
    }

    /*
     * (non-Javadoc)
     * @see es.tid.cloud.tdaf.accounting.filtering.EventPatternParser#getEventPatterns()
     */
    @Override
    public MultiKeyMap getEventPatterns() {
        return eventPatternEntriesMap;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * @see es.tid.cloud.tdaf.accounting.filtering.EventPatternParser#parseEventPatterns()
     */
    @Override
    @SuppressWarnings("unchecked")
    public void parseEventPatterns() throws Exception {
        Reader reader =  new FileReader(this.file);
        try {

            CSVReaderBuilder<EventPattern> eventCSVReaderBuilder = 
                    new CSVReaderBuilder<EventPattern>(reader).entryParser(eventCSVEntryParser);
            if(eventEntryFilter != null) {
                eventCSVReaderBuilder = eventCSVReaderBuilder.entryFilter(new EventCSVEntryFilter(eventEntryFilter));
            }

            CSVReader<EventPattern> eventCSVReader = eventCSVReaderBuilder.build();

            for (EventPattern eventPatternEntry : eventCSVReader.readAll()) {
                String serviceId = eventPatternEntry.getServiceId();
                String eventId = eventPatternEntry.getId();
                List<EventPattern> eventPatternEntries = (List<EventPattern>)this.eventPatternEntriesMap.get(serviceId, eventId);
                if (eventPatternEntries == null) {
                    this.eventPatternEntriesMap.put(serviceId, eventId, eventPatternEntries = new ArrayList<EventPattern>());
                }
                eventPatternEntries.add(eventPatternEntry);
            }

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
