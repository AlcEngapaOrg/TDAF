package es.tid.cloud.tdaf.accounting.filtering;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.InitializingBean;

import com.google.code.regexp.Pattern;

public class IPDREntryIdentifier implements InitializingBean {
    private static final int FLAGS = java.util.regex.Pattern.DOTALL | java.util.regex.Pattern.MULTILINE;
    private File file = null;
    private Map<String, List<IPRDEntryPattern>> patterns = new LinkedHashMap<String, List<IPRDEntryPattern>>();

    public Map<String, List<IPRDEntryPattern>> getPatterns() {
        return patterns;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Reader reader = null;
        reader = new FileReader(this.file);
        CSVParser parser = new CSVParser(reader);
        try {
            int rowCount = 0;
            for (String[] row : parser.getAllValues()) {
                rowCount++;
                if (row == null) {
                    continue;
                }
                String patternId = null;
                String patternValue = null;
                String concept = null;
                String event = null;
                File template = null;
                if (row.length > 2) {
                    patternId = row[0];
                    concept = row[1];
                    event = row[2];
                    patternValue = row[3];
                    template = new File(row[4]);
                }
                if (patternValue == null || patternId == null || concept == null || event == null) {
                    String msg = MessageFormat.format("Error while reading row {0}. The row must the following columns: PATTERN_ID, IPDR CONCEPT, IPDR EVENT, REGULAR EXPRESSION, PATTERN FILE NAME", rowCount);
                    throw new RuntimeException(msg);
                }
                Pattern regExpPattern = Pattern.compile(patternValue, FLAGS);
                IPRDEntryPattern pattern = new IPRDEntryPattern(patternId, concept, event, regExpPattern, template);
                List<IPRDEntryPattern> ps = this.patterns.get(patternId);
                if (ps == null) {
                    this.patterns.put(patternId, ps = new ArrayList<IPRDEntryPattern>());
                }
                ps.add(pattern);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
