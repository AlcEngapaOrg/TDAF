package es.tid.cloud.tdaf.ipdrgen;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import nl.flotsam.xeger.Xeger;

import org.apache.commons.lang.CharRange;
import org.apache.commons.lang.CharSet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import es.tid.cloud.tdaf.ipdrgen.json.JSchemaArrayType;
import es.tid.cloud.tdaf.ipdrgen.json.JSchemaType;
import es.tid.cloud.tdaf.ipdrgen.json.builder.JSonSchemaParserContext;
import es.tid.cloud.tdaf.ipdrgen.json.builder.TypeBuilder;

public class JsonGenerator {
    private JSonSchemaParserContext context = new JSonSchemaParserContext();

    public static int MIN_ARRAY = 1;
    public static int MAX_ARRAY = 10;

    public static int MIN_STRING = 1;
    public static int MAX_STRING = 10;
    
    public static Date MIN_DATE = new Date() {{
        Calendar cal = Calendar.getInstance();
        cal.set(2013, 1, 1, 0, 0);
        this.setTime(cal.getTimeInMillis());
    }};
    public static Date MAX_DATE = new Date();
    
    private static List<Character> CHARSET = new ArrayList<Character>() {{
        CharSet charset = CharSet.getInstance("a-zA-Z0-9");
        for(CharRange range: charset.getCharRanges()) {
            for(Iterator<Character> charIt = range.iterator(); charIt.hasNext(); ) {
                this.add(charIt.next());
            }
        }
    }};

    private static final Random RANDOM = new Random();

    public static void main(String... args) throws Exception {
        JsonGenerator generator = new JsonGenerator();
        ClassLoader cl = JsonGenerator.class.getClassLoader();
        URL urlFile = cl.getResource("json/IPDRDoc.schema.json");
        File file = new File(urlFile.toURI());
        JsonNode node = generator.generateRandomJson(file);
        ObjectMapper mapper = new ObjectMapper();
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(mapper.writeValueAsString(node));
    }
    
    public JsonNode generateRandomJson(File file) throws JsonGeneratorException {
        JSchemaType rootType = TypeBuilder.parseJSchema(context, file);
        return generateRandomJson(rootType, JsonNodeFactory.instance);
    }
    
    private JsonNode generateRandomJson(JSchemaType schemaType, JsonNodeFactory factory) {
        JsonNode node = null;
        String type = schemaType.getType();
        if (type.equals("object")) {
            node = new ObjectNode(factory);
            for(Map.Entry<String, JSchemaType> entry: schemaType.getProperties().entrySet()) {
                JsonNode propValue = generateRandomJson(entry.getValue(), factory);
                if (propValue != null) {
                    ((ObjectNode) node).put(entry.getKey(), propValue);
                }
            }
        } else if (type.equals("array")) {
            int max = MAX_ARRAY - MIN_ARRAY;
            int length = RANDOM.nextInt(max) + MIN_ARRAY;
            node = new ArrayNode(factory);
            for (int i = 0; i < length; i++) {
                ((ArrayNode) node).add(generateRandomJson(((JSchemaArrayType) schemaType).getItems(), factory));
            }
        } else if (type.equals("integer")) {
            if (schemaType.getFormat() == JSchemaType.Format.MILLISEC) {
                long max = MAX_DATE.getTime() - MIN_DATE.getTime();
                node = new LongNode((long)(RANDOM.nextInt((int) max) + MIN_DATE.getTime()));
            } else {
                node = new LongNode(RANDOM.nextInt());
            }
        } else if (type.equals("string")) {
            String str;
            if (schemaType.getPattern() != null) {
                str = null;
                Xeger generator = new Xeger(schemaType.getPattern());
                str = generator.generate();
            } else {
                int max = MAX_STRING - MIN_STRING;
                int length = RANDOM.nextInt(max) + MIN_STRING;
                str = "";
                for (int i = 0; i < length; i++) {
                    str += CHARSET.get(RANDOM.nextInt(CHARSET.size()));
                }
            }
            node = new TextNode(str);
        }
        return node;
    }
}
