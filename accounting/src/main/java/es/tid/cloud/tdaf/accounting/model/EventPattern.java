package es.tid.cloud.tdaf.accounting.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.code.regexp.Pattern;

/**
 * Accounting Event.
 * Service provider specify pattern parameters.
 * 
 * @author AlcEngapaOrg
 *
 */

public class EventPattern extends EventBase implements Serializable {

    private static final long serialVersionUID = 4547790913698953727L;

    private static final int FLAGS = java.util.regex.Pattern.DOTALL | java.util.regex.Pattern.MULTILINE;

    @NotEmpty
    private String logPattern;
    private Pattern pattern;

    public EventPattern(){
        super();
    }

    public EventPattern(String serviceId, String id, String concept, String event, String logPattern, Mode mode) {
        super();
        this.serviceId = serviceId;
        this.id = id;
        this.concept = concept;
        this.event = event;
        this.logPattern = logPattern;
        this.pattern = buildPattern(logPattern);
        this.mode = mode;
    }

    public String getLogPattern() {
        return logPattern;
    }

    public void setLogPattern(String logPattern) {
        this.logPattern = logPattern;
        //Sets pattern value
        setPattern(buildPattern(this.logPattern));
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    private Pattern buildPattern(String logPattern) {
        Pattern regExpPattern = Pattern.compile(this.logPattern, FLAGS);
        return regExpPattern;
    }

    /**
     * Match objects
     * @param o An accounting event which compare to
     * @return true if not null attributes are equals, false in other case
     */
    public boolean match(EventPattern o) {

        if(o == null) {
            return false;
        } else if(this.equals(o)) {
            return true;
        }

        boolean matchValue = true;
        if(o.getServiceId() != null && !o.getServiceId().equals(this.serviceId)){
            matchValue = false;
        }
        if(o.getId() != null && !o.getId().equals(this.id)){
            matchValue = false;
        }
        if(o.getConcept() != null && !o.getConcept().equals(this.concept)){
            matchValue = false;
        }
        if(o.getEvent() != null && !o.getEvent().equals(this.event)){
            matchValue = false;
        }
        if(o.getLogPattern() != null && !o.getLogPattern().equals(this.logPattern)){
            matchValue = false;
        }
        if(o.getMode() != null && !o.getMode().equals(this.mode)){
            matchValue = false;
        }

        return matchValue;
    }

}
