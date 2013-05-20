package es.tid.cloud.tdaf.accounting.data;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

	

}
