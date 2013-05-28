package es.tid.cloud.tdaf.accounting.itest;

import org.junit.Test;

import com.mongodb.WriteResult;

import es.tid.cloud.tdaf.accounting.persist.PersistEventsRouteTestSupport;


public class PersistEventsRouteITest extends PersistEventsRouteTestSupport {

    @Override
    public String appContext() {
        return "classpath:test-integ-context.xml";
    }

    @Test
    public void integrationPersistEvent() throws Exception {

        // When
        Object o = template.requestBody(getBodyAsMap());

        // Then
        assertTrue("Result is not of type WriteResult", o instanceof WriteResult);

    }

}
