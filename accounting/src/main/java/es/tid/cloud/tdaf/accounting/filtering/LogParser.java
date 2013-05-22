package es.tid.cloud.tdaf.accounting.filtering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
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
    private Log4jPatternMultilineLogParser logParser = null;
    
    class LogDataIterator implements Iterator<LogData> {
        private BufferedReader reader = null;
        private LogData logData = null;
        private int currentLine = 0;
        private int logDataLine = -1;
        private ParsingContext ctxt = null;

        LogDataIterator(InputStream in) {
            this.reader = new BufferedReader(new InputStreamReader(in));
            this.logData = null;
            this.ctxt = new ParsingContext();
            this.ctxt = new ParsingContext();
            this.ctxt.getCustomConextProperties().put("Log4jPatternMultilineLogParser.logEventProperties", new HashMap<String, String>());
        }
        
        private String readLine() throws IOException {
            String line = reader.readLine();
            currentLine ++;
            if (logDataLine < 0) {
                logDataLine = currentLine;
            }
            return line;
        }
        
        private LogData parseBuffer() throws IOException, ParseException {
            LogData logData = null;
            this.reader.close();
            this.reader = null;
            logData = logParser.parseBuffer(ctxt);
            logData.setLine(Integer.toString(logDataLine));
            return logData;
        }
        
        private LogData parseLine(String line) throws ParseException {
            LogData logData = null;
            logData = logParser.parse(line, ctxt);
            if (logData != null) {
                logData.setLine(Integer.toString(logDataLine));
                this.logDataLine = -1;
            }
            return logData;
        }

        @Override
        public boolean hasNext() {
            String line;
            while (this.logData == null && this.reader != null) {
                try {
                    line = readLine(); 
                    if (line == null) {
                        this.logData = parseBuffer();
                        return this.logData != null;
                    } else {
                        this.logData = parseLine(line);
                    }
                } catch (IOException e) {
                    try {
                        this.reader.close();
                    } catch (IOException e1) {
                    }
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    String msg = MessageFormat.format("Error parsing log in line {0}", (this.logDataLine < 0) ? 1 : this.logDataLine);
                    e.printStackTrace();
                    System.out.println(msg);
                }
            }
            return logData != null;
        }

        @Override
        public LogData next() {
            if (this.logData == null) {
                throw new NoSuchElementException();
            }
            LogData result = this.logData;
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
