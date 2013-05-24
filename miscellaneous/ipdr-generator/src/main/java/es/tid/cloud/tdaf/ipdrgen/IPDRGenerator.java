package es.tid.cloud.tdaf.ipdrgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.math.NumberRange;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class IPDRGenerator {
    private static final Random RANDOM = new Random();

    protected static void generate(File baseDir, Map<String, List<File>> services, NumberRange numberOfIPRPerFiles, NumberRange numberOfFiles) throws JsonGeneratorException, IOException {
        JsonGenerator generator = new JsonGenerator();
        ObjectMapper mapper = new ObjectMapper();
        for(Map.Entry<String, List<File>> serviceEntry: services.entrySet()) {
            String serviceName = serviceEntry.getKey();
            File serviceDir = new File(baseDir, serviceName);
            serviceDir.mkdirs();
            int maxFiles = getRandom(numberOfFiles);
            for (int i = 0; i < maxFiles; i++) {
                File ipdrFile = new File(serviceDir, serviceName + i + ".ipdr");
                FileWriter writer = null;
                int maxIPDR = getRandom(numberOfIPRPerFiles);
                ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
                for (int j = 0; j < maxIPDR; j++) {
                    List<File> schemas = serviceEntry.getValue();
                    File schema = getRandomItem(schemas);
                    JsonNode json = generator.generateRandomJson(schema);
                    array.add(json);
                }
                writer = new FileWriter(ipdrFile);
                mapper.writeValue(writer, array);
            }
        }
    }
    
    private static <T> T getRandomItem(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
    
    private static int getRandom(NumberRange range) {
        int min = range.getMinimumInteger();
        int max = range.getMaximumInteger();
        return RANDOM.nextInt(max - min) + min;
    }
    
    public static void main(String...args) throws Exception {
        ClassLoader cl = JsonGenerator.class.getClassLoader();
        URL urlFile = cl.getResource("json/IPDRDoc.schema.json");
        File file = new File(urlFile.toURI());
        Map<String, List<File>> services = new HashMap<String, List<File>>();
        services.put("i2cm", Arrays.asList(file));
        File baseDir = new File("d:\\temp\\ipdr");
        generate(baseDir, services, new NumberRange(20, 100), new NumberRange(1000, 1100));
    }
}
