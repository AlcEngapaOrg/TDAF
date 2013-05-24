package es.tid.cloud.tdaf.ipdrgen.json;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class JSchemaType {
    public static final String[] PRIMITIVE_TYPES = new String[] {
        "string",
        "number",
        "integer",
        "boolean",
        "object",
        "array",
        "null",
        "any"
    };

    public enum Format {
        DATE_TIME("date-time", "string"),
        DATE("date", "string"),
        TIME("time", "string"),
        MILLISEC("utc-millisec", "integer"),
        REGEX("regex", "string",  "number", "integer"),
        COLOR("color", "string"),
        STYLE("style", "string"),
        PHONE("phone", "string"),
        URI("uri", "string");

        private String format;
        private String[] validTypes;

        Format(String format, String... validTypes) {
            this.format = format;
            this.validTypes = validTypes;
        }
        
        public static Format parse(String str) {
            if (str == null) {
                return null;
            }
            for(Format f: Format.values()) {
                if (str.equals(f.format)) {
                    return f;
                }
            }
            return null;
        }

        public String[] getValidTypes() {
            return validTypes;
        }

        public String toString() {
            return format;
        }
    }

    private String uri = null;
    private String description = null;
    private String type = null;
    private Format format = null;
    private String pattern = null;
    private boolean required = false;
    private Map<String, JSchemaType> properties = new LinkedHashMap<String, JSchemaType>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, JSchemaType> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void addProperty(String name, JSchemaType type) {
        this.properties.put(name, type);
    }
    
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
