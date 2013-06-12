package es.tid.cloud.tdaf.accounting.filtering;

import java.io.File;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.collections.map.MultiKeyMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.tid.cloud.tdaf.accounting.AccountingException;
import es.tid.cloud.tdaf.accounting.AccountingException.Code;
import es.tid.cloud.tdaf.accounting.filtering.csv.EventCSVEntryParser;
import es.tid.cloud.tdaf.accounting.filtering.csv.EventCSVParser;
import es.tid.cloud.tdaf.accounting.model.EventPattern;

public class EventCSVParserTest {

    private static Validator validator;
    private final static String fileBase = "./src/test/resources/conf/";

    private EventCSVEntryParser eventCSVEntryParser;
    private EventCSVParser eventCSVParser;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void init() {

        eventCSVParser = new EventCSVParser();
        eventCSVEntryParser = new EventCSVEntryParser(validator);

        eventCSVParser.setEventCSVEntryParser(eventCSVEntryParser);
    }

    @Test
    public void loadEventEntries() {
        //Given
        File file = new File(fileBase.concat("accounting-events.csv"));
        eventCSVParser.setFile(file);
        //When
        try {
            eventCSVParser.parseEventPatterns();
            MultiKeyMap multiKeyMap = eventCSVParser.getEventPatterns();
            Assert.assertNotNull(multiKeyMap);
            Assert.assertTrue(multiKeyMap.size()==3);
            //Check find one entry
            Assert.assertTrue(((List<?>)multiKeyMap.get("InstantServer", "addFirewallRule")).size()==1);
        } catch (Exception e) {
            Assert.fail("An Exception not should be throw");
        }
    }

    @Test
    public void loadEventEntriesIncorrectNumberPartsInLine() throws Exception{
        //Given
        File file = new File(fileBase.concat("accounting-events1.csv"));
        eventCSVParser.setFile(file);
        //When
        try {
            eventCSVParser.parseEventPatterns();
            Assert.fail("An Exception should be throw");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getCause() != null && AccountingException.class.isInstance(e.getCause()));
            Assert.assertEquals(Code.AC_0002, ((AccountingException)e.getCause()).getCode());
            Assert.assertTrue(eventCSVParser.getEventPatterns().size()<1);
        }
    }

    @Test
    public void loadEventEntriesIncorrectFormatOfPartsInLine() {
        //Given
        File file = new File(fileBase.concat("accounting-events2.csv"));
        eventCSVParser.setFile(file);
        //When
        try {
            eventCSVParser.parseEventPatterns();
            Assert.fail("An Exception should be throw");
        } catch (Exception e) {
            Assert.assertTrue(e.getCause() != null && AccountingException.class.isInstance(e.getCause()));
            Assert.assertEquals(Code.AC_0002, ((AccountingException)e.getCause()).getCode());
            Assert.assertTrue(eventCSVParser.getEventPatterns().size()<1);
        }
    }

    @Test
    public void loadEventEntriesWithServiceIdFilter() {
        //Given
        File file = new File(fileBase.concat("accounting-events3.csv"));
        eventCSVParser.setFile(file);
        EventPattern acEventEntryFilter = new EventPattern();
        acEventEntryFilter.setServiceId("InstantServer");
        eventCSVParser.setAccountingEventEntryFilter(acEventEntryFilter);
        //When
        try {
            eventCSVParser.parseEventPatterns();
            Assert.assertTrue(eventCSVParser.getEventPatterns().size()==3);
        } catch (Exception e) {
            Assert.fail("An Exception not should be throw");
        }
    }
}
