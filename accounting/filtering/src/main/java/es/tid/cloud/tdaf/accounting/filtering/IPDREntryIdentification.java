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

public class IPDREntryIdentification implements InitializingBean {
    private File file = null;
    private Map<String, List<Pattern>> patterns = new LinkedHashMap<String, List<Pattern>>();

    public Map<String, List<Pattern>> getPatterns() {
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
                if (row.length < 2) {
                    patternId = row[0];
                    patternValue = row[2];
                }
                if (patternValue == null || patternId == null) {
                    String msg = MessageFormat.format("Error while reading row {0}. The row must have 2 values.", rowCount);
                    throw new RuntimeException(msg);
                }
                Pattern pattern = Pattern.compile(patternValue);
                List<Pattern> ps = this.patterns.get(patternId);
                if (ps == null) {
                    this.patterns.put(patternId, ps = new ArrayList<Pattern>());
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
