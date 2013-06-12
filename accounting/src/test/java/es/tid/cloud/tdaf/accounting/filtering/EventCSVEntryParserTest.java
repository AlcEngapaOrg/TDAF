package es.tid.cloud.tdaf.accounting.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.tid.cloud.tdaf.accounting.AccountingException;
import es.tid.cloud.tdaf.accounting.AccountingException.Code;
import es.tid.cloud.tdaf.accounting.filtering.csv.EventCSVEntryParser;
import es.tid.cloud.tdaf.accounting.model.EventBase;
import es.tid.cloud.tdaf.accounting.model.EventBase.Mode;
import es.tid.cloud.tdaf.accounting.model.EventPattern;

public class EventCSVEntryParserTest {

    private static Validator validator;

    private EventCSVEntryParser accountingEventCSVEntryParser;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void init() {
        accountingEventCSVEntryParser = new EventCSVEntryParser(validator);
    }

    @Test
    public void invalidLengthOfFildsForAnEntryTest() {

        //Given, more than max data number
        String[] data = new String[]{"1", "2", "3", "4", "5", "6", "7"};
        //When
        try {
            accountingEventCSVEntryParser.parseEntry(data);
            Assert.fail("Should be throw an Exception");
        } catch (RuntimeException e) {
            //Then
            assertTrue(IllegalArgumentException.class.isInstance(e));
            assertTrue(AccountingException.class.isInstance(e.getCause()));
            AccountingException accountingException = (AccountingException)e.getCause();
            assertEquals(Code.AC_0002, accountingException.getCode());
            assertTrue("Should appear max and min number of parts",
                        accountingException.getMessage().contains(String.valueOf(EventCSVEntryParser.MAX_ENTRY_PARTS))
                        && accountingException.getMessage().contains(String.valueOf(EventCSVEntryParser.MIN_ENTRY_PARTS)));
        }

        //Given , less than min data number
        data = new String[]{"1", "2", "3"};
        //When
        try {
            accountingEventCSVEntryParser.parseEntry(data);
            fail("Should be throw an Exception");
        } catch (RuntimeException e) {
            //Then
            assertTrue(IllegalArgumentException.class.isInstance(e));
            assertTrue(AccountingException.class.isInstance(e.getCause()));
        }
    }

    @Test
    public void invalidModeOfAnEntryTest() {
        //Given
        String wrongMode = "PEPE";
        String[] data = new String[]{"1", "2", "3", "4", "5", wrongMode};
        //When
        try {
            accountingEventCSVEntryParser.parseEntry(data);
            Assert.fail("Should be throw an Exception");
        } catch (RuntimeException e) {
            //Then
            assertTrue(IllegalArgumentException.class.isInstance(e));
            assertTrue(AccountingException.class.isInstance(e.getCause()));
            AccountingException accountingException = (AccountingException)e.getCause();
            assertEquals(Code.AC_0002, accountingException.getCode());
            assertTrue("Should appear name of invalid part and valid values",
                        accountingException.getMessage().contains(wrongMode)
                        && accountingException.getMessage().contains(String.format("%s", Arrays.asList(Mode.values()))));
        }
    }

    @Test
    public void invalidDataPartsForEntry() {
        //Given
        String empty = StringUtils.EMPTY;
        String[] data = new String[]{empty, empty, empty, empty, empty};
        //When
        try {
            accountingEventCSVEntryParser.parseEntry(data);
            Assert.fail("Should be throw an Exception");
        } catch (RuntimeException e) {
            //Then
            assertTrue(IllegalArgumentException.class.isInstance(e));
            assertTrue(AccountingException.class.isInstance(e.getCause()));
            AccountingException accountingException = (AccountingException)e.getCause();
            assertEquals(Code.AC_0002, accountingException.getCode());
            assertTrue("Should appear name and message for invalid properties",
                        accountingException.getMessage().contains("empty"));
        }
    }

    @Test
    public void validDataForOfAnEntryTest() {
        //Given
        String[] data = new String[]{"1", "2", "3", "4", "5", Mode.OFFLINE.name()};
        EventBase accountingEventEntry = new EventPattern("1", "2", "3", "4", "5", Mode.OFFLINE);
        //When
        EventBase accountingEventEntryResult = accountingEventCSVEntryParser.parseEntry(data);
        //Then
        assertTrue(StringUtils.substringAfter(accountingEventEntryResult.toString(), "[")
                .equals(StringUtils.substringAfter(accountingEventEntry.toString(), "[")));
    }
}
