package es.tid.cloud.tdaf.accounting.filtering;

import org.apache.commons.collections.map.MultiKeyMap;

public interface EventPatternParser {

    public MultiKeyMap getEventPatterns();

    public void parseEventPatterns() throws Exception;

}