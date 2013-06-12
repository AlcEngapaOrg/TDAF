package es.tid.cloud.tdaf.accounting.filtering.csv;

import com.googlecode.jcsv.reader.CSVEntryFilter;

import es.tid.cloud.tdaf.accounting.model.EventBase;
import es.tid.cloud.tdaf.accounting.model.EventPattern;

/**
 * Event Entry Filter
 * @author AlcEngapaOrg
 */
public class EventCSVEntryFilter implements CSVEntryFilter<EventPattern>{

    EventPattern eventEntryFilter = null;

    public EventCSVEntryFilter() {
        super();
    }

    public EventCSVEntryFilter(EventPattern eventEntryFilter) {
        super();
        this.eventEntryFilter = eventEntryFilter;
    }

    public EventBase getEvent() {
        return eventEntryFilter;
    }

    public void setEvent(EventPattern eventEntryFilter) {
        this.eventEntryFilter = eventEntryFilter;
    }

    @Override
    public boolean match(EventPattern eventEntry) {
        if(eventEntryFilter == null || eventEntry == null) {
            return false;
        }
        return eventEntry.match(eventEntryFilter);
    }

}
