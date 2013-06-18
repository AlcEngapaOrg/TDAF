package es.tid.cloud.tdaf.accounting.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


/**
 * Event Entry
 * @author AlcEngapaOrg
 */
@JsonAutoDetect(
        fieldVisibility=Visibility.PUBLIC_ONLY,
        getterVisibility=Visibility.PUBLIC_ONLY,
        setterVisibility=Visibility.PUBLIC_ONLY)
public class EventEntry extends EventBase implements Serializable {

    private static final long serialVersionUID = 4547790913698953727L;

    private Map<String,Object> eventInfo;
    private Map<String,Object> logDetails;
    @NotNull
    private Date time;

    public EventEntry() {
        super();
    }

    public EventEntry(String serviceId, String id, String concept, String event, Mode mode,
            Map<String,Object> eventInfo, Map<String,Object> logDetails, Date time) {
        super();
        this.serviceId = serviceId;
        this.id = id;
        this.concept = concept;
        this.event = event;
        this.mode = mode;
        this.eventInfo = eventInfo;
        this.logDetails = logDetails;
        this.time = time;
    }

    public Map<String, Object> getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(Map<String, Object> eventInfo) {
        this.eventInfo = eventInfo;
    }

    public Map<String, Object> getLogDetails(){
        return logDetails;
    }

    public void setLogDetails(Map<String, Object> logDetails){
        this.logDetails = logDetails;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
