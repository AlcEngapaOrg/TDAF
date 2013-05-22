package es.tid.cloud.tdaf.accounting.filtering;

import java.io.File;

import com.google.code.regexp.Pattern;

public class IPRDEntryPattern {
    private String id = null;
    private String concept = null;
    private String event = null;
    private Pattern pattern = null;
    private File template = null;

    public IPRDEntryPattern(String id, String concept, String event, Pattern pattern, File template) {
        this.id = id;
        this.concept = concept;
        this.event = event;
        this.pattern = pattern;
        this.template = template;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConcept() {
        return concept;
    }

    public String getEvent() {
        return event;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }
}
