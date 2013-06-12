package es.tid.cloud.tdaf.accounting.filtering;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

import es.tid.cloud.tdaf.accounting.filtering.csv.EventCSVEntryFilter;
import es.tid.cloud.tdaf.accounting.model.EventBase.Mode;
import es.tid.cloud.tdaf.accounting.model.EventPattern;

public class EventCSVEntryFilterTest {

    @Test
    public void matchTest(){
      
        //Given
        EventPattern accountingEventEntry = 
                new EventPattern("1","2","3","4","5", Mode.OFFLINE);
        EventPattern accountingEventEntryFilter = new EventPattern();
        BeanUtils.copyProperties(accountingEventEntry, accountingEventEntryFilter);

        //When
        EventCSVEntryFilter accountingEventCSVEntryFilter = new EventCSVEntryFilter();
        accountingEventCSVEntryFilter.setEvent(accountingEventEntryFilter);
        boolean matches = accountingEventCSVEntryFilter.match(accountingEventEntry);
        //Then
        assertTrue(matches);
    }

    @Test
    public void noMatchTest(){
      //Given
        EventPattern accountingEventEntry = 
                new EventPattern("1","2","3","4","5", Mode.OFFLINE);
        EventPattern accountingEventEntryFilter = 
                new EventPattern("6","7","8","9","0", Mode.ONLINE);

        //When
        EventCSVEntryFilter accountingEventCSVEntryFilter = new EventCSVEntryFilter(accountingEventEntryFilter);
        boolean matches = accountingEventCSVEntryFilter.match(accountingEventEntry);
        //Then
        assertFalse(matches);
    }

    @Test
    public void noMatchWithNullFilterTest(){
      //Given
        EventPattern accountingEventEntry = 
                new EventPattern("1","2","3","4","5", Mode.OFFLINE);

        //When
        EventCSVEntryFilter accountingEventCSVEntryFilter = new EventCSVEntryFilter();
        boolean matches = accountingEventCSVEntryFilter.match(accountingEventEntry);
        //Then
        assertFalse(matches);
    }
}
