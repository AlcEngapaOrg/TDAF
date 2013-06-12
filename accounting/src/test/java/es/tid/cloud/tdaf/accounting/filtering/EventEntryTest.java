package es.tid.cloud.tdaf.accounting.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import es.tid.cloud.tdaf.accounting.model.EventBase.Mode;
import es.tid.cloud.tdaf.accounting.model.EventPattern;

public class EventEntryTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void notNullAttributesTest(){
        //Given
        EventPattern accountingEventEntry = 
                new EventPattern("ole", "probas","eso","borra",".+", null);
        //When
        Set<ConstraintViolation<EventPattern>> constraintViolations =
                validator.validate(accountingEventEntry);
        //Then
        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void nullAllAttributesTest() {

        //Given
        EventPattern accountingEventEntry = new EventPattern();
        //When
        Set<ConstraintViolation<EventPattern>> constraintViolations =
                validator.validate(accountingEventEntry);
        //Then
        assertEquals(4, constraintViolations.size());
    }

    @Test
    public void minSizeAllAttributesTest() {

        //Given
        String empty = StringUtils.EMPTY;
        EventPattern accountingEventEntry = 
                new EventPattern(empty,empty,empty,empty,empty, null);
        //When
        Set<ConstraintViolation<EventPattern>> constraintViolations =
                validator.validate(accountingEventEntry);
        //Then
        assertEquals(4, constraintViolations.size());
    }

    @Test
    public void matchingAccountingEventEntriesTest() {

        //Given
        EventPattern accountingEventEntry = 
                new EventPattern("1","2","3","4","5", Mode.OFFLINE);

        ///ServiceId
        //When
        EventPattern accountingEventEntryFilter = new EventPattern();
        accountingEventEntryFilter.setServiceId("1");

        //Then
        assertTrue(accountingEventEntry.match(accountingEventEntryFilter));

        ///Id
        //When
        accountingEventEntryFilter.setId("2");

        //Then
        assertTrue(accountingEventEntry.match(accountingEventEntryFilter));

        ///Concept
        //When
        accountingEventEntryFilter.setConcept("3");

        //Then
        assertTrue(accountingEventEntry.match(accountingEventEntryFilter));

        ///Type
        //When
        accountingEventEntryFilter.setEvent("4");

        //Then
        assertTrue(accountingEventEntry.match(accountingEventEntryFilter));

        ///Pattern
        //When
        accountingEventEntryFilter.setLogPattern("5");

        //Then
        assertTrue(accountingEventEntry.match(accountingEventEntryFilter));

        ///Mode
        //When
        accountingEventEntryFilter.setMode(Mode.OFFLINE);

        //Then
        assertTrue(accountingEventEntry.match(accountingEventEntryFilter));

        ///Change diferent logPattern
        //When
        accountingEventEntryFilter.setLogPattern("123");

        //Then
        assertFalse(accountingEventEntry.match(accountingEventEntryFilter));
    }

    @Test
    public void matchingWithNullAccountingEventEntryTest() {
        //Given
        EventPattern accountingEventEntry = 
                new EventPattern("1","2","3","4","5", Mode.OFFLINE);

        //When
        EventPattern accountingEventEntryFilter = null;

        //Then
        assertFalse(accountingEventEntry.match(accountingEventEntryFilter));
    }

    @Test
    public void matchingEqualsPropertiesAccountingEventEntriesTest() {
        //Given
        EventPattern accountingEventEntry = 
                new EventPattern("1","2","3","4","5", Mode.OFFLINE);

        //When
        EventPattern accountingEventEntryFilter = new EventPattern();
        BeanUtils.copyProperties(accountingEventEntry, accountingEventEntryFilter);

        //Then
        assertTrue(accountingEventEntry.match(accountingEventEntryFilter));
    }
}
