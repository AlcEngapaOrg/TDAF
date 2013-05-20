package es.tid.cloud.tdaf.accounting.data;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PersistEventsRouteTest extends CamelSpringTestSupport {

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext(
                new String[] {
                    "classpath:META-INF/spring/persist-context.xml"});
	}



}
