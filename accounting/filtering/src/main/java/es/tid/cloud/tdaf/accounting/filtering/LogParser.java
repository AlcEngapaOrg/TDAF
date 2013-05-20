package es.tid.cloud.tdaf.accounting.filtering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.camel.Body;
import org.springframework.beans.factory.InitializingBean;

import pl.otros.logview.LogData;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.parser.log4j.Log4jPatternMultilineLogParser;

public class LogParser implements InitializingBean {
    private Properties props = null;
    private String pattern = null;
    private String dateFormat = null;
    private String customLevels = null;
    private ParsingContext ctxt = null;
    private Log4jPatternMultilineLogParser logParser = null;
    
    class LogDataIterator implements Iterator<LogData> {
        private BufferedReader reader = null;
        private LogData logData = null;

        LogDataIterator(InputStream in) {
            this.reader = new BufferedReader(new InputStreamReader(in));
            this.logData = null;
        }

        @Override
        public boolean hasNext() {
            while (logData == null && this.reader != null) {
                try {
                    String line;
                    line = reader.readLine();
                    if (line == null) {
                        reader.close();
                        reader = null;
                        logData = logParser.parseBuffer(ctxt);
                        return logData != null;
                    } else {
                        logData = logParser.parse(line, ctxt);
                    }
                } catch (IOException e) {
                    try {
                        this.reader.close();
                    } catch (IOException e1) {
                    }
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return logData != null;
        }

        @Override
        public LogData next() {
            if (logData == null) {
                throw new NoSuchElementException();
            }
            LogData result = logData;
            this.logData = null;
            return result;
        }

        @Override
        public void remove() {
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties parserProps = new Properties();
        parserProps.put("type", "log4j");
        if (this.props != null) {
            parserProps.putAll(props);
        }
        if (this.pattern != null) {
            parserProps.put("pattern", this.pattern);
        }
        if (this.dateFormat != null) {
            parserProps.put("dateFormat", this.dateFormat);
        }
        if (this.customLevels != null) {
            parserProps.put("customLevels", this.customLevels);
        }
        logParser = new Log4jPatternMultilineLogParser();
        ctxt = new ParsingContext();
        ctxt.getCustomConextProperties().put("Log4jPatternMultilineLogParser.logEventProperties", new HashMap());
        logParser.init(parserProps);
    }

    public void setProperties(Properties props) {
        this.props = props;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setCustomLevels(String customLevels) {
        this.customLevels = customLevels;
    }
    
    public Iterator<LogData> iterator(@Body InputStream in) {
        return new LogDataIterator(in);
    }
}
