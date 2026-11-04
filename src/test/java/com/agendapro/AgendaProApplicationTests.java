package com.agendapro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:agendapro-test")
class AgendaProApplicationTests {

	@Test
	void contextLoads() {
	}

}
