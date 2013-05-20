package es.tid.cloud.tdaf.accounting.data;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.WriteResult;

public class PersistEventsRouteTest extends CamelSpringTestSupport {

	/** The producer template.*/
    @Produce(uri = "vm:persist-events")
    private ProducerTemplate template;

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext(
                new String[] {
                    "classpath:META-INF/spring/app-context.xml",
                    "classpath:META-INF/spring/persist-context.xml"});
	}

	@Test
	public void persistEvent(){
		Object o = template.requestBody(new String("{name:'enrique',lastName:'garcia'}"));
		assertTrue("Result is not of type WriteResult", o instanceof WriteResult);
	}

}
