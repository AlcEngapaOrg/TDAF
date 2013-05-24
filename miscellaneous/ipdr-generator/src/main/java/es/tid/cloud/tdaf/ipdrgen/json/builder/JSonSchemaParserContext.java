package es.tid.cloud.tdaf.ipdrgen.json.builder;

import java.util.HashMap;
import java.util.Map;

import es.tid.cloud.tdaf.ipdrgen.json.JSchemaType;

public class JSonSchemaParserContext {
    private Map<String, JSchemaType> types = new HashMap<String, JSchemaType>();

    Map<String, JSchemaType> getTypes() {
        return types;
    }
}
