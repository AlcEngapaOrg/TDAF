package es.tid.cloud.tdaf.accounting.filtering.csv;

import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.googlecode.jcsv.reader.CSVEntryParser;

import es.tid.cloud.tdaf.accounting.AccountingException;
import es.tid.cloud.tdaf.accounting.AccountingException.Code;
import es.tid.cloud.tdaf.accounting.model.EventBase.Mode;
import es.tid.cloud.tdaf.accounting.model.EventPattern;

/**
 * Event Entry Parser
 * @author AlcEngapaOrg
 */
public class EventCSVEntryParser implements CSVEntryParser<EventPattern>{

    public final static int MIN_ENTRY_PARTS = 5;
    public final static int MAX_ENTRY_PARTS = 6;

    //JRS-303 validation
    private Validator validator;

    public EventCSVEntryParser() {
        super();
    }

    public EventCSVEntryParser(Validator validator) {
        super();
        this.validator = validator;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public EventPattern parseEntry(String... data) {

        if (data.length < MIN_ENTRY_PARTS || data.length > MAX_ENTRY_PARTS) {
            AccountingException acException = new AccountingException(Code.AC_0002,
                    String.format("Number of parts per CVS entry invalid , min %s and max %s", MIN_ENTRY_PARTS, MAX_ENTRY_PARTS));
            throw new IllegalArgumentException(acException);
        }

        String serviceId = data[0];
        String id = data[1];
        String category = data[2];
        String type = data[3];
        String logPattern = data[4];
        Mode mode = null;

        if(data.length == MAX_ENTRY_PARTS){
            try {
                mode = Mode.valueOf(data[MAX_ENTRY_PARTS-1]);
            } catch (Throwable e) {
                AccountingException acException = new AccountingException(Code.AC_0002,
                        String.format("Invalid value for <mode> part : %s , valid values are: %s"
                                ,data[MAX_ENTRY_PARTS-1], Arrays.asList(Mode.values())), e);
                    throw new IllegalArgumentException(acException);
            }
        }

        EventPattern event = new EventPattern(serviceId, id, category, type, logPattern, mode);
        Set<ConstraintViolation<EventPattern>> validationResults= this.validator.validate(event);
        if(validationResults.size() > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ConstraintViolation<EventPattern> constraintViolation : validationResults) {
                stringBuilder.append(String.format("\n\t- %s , %s",constraintViolation.getPropertyPath(),constraintViolation.getMessage()));
            }
            AccountingException acException = new AccountingException(Code.AC_0002,
                    String.format("There are one or more entries with invalid values : %s", stringBuilder.toString()));
            throw new IllegalArgumentException(acException);
        }
        return event;
    }

}
