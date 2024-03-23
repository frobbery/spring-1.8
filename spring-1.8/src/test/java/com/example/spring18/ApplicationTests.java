package com.example.spring18;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.shell.interactive.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:testdb", "spring.datasource.driver-class-name=org.h2.Driver"})
class ApplicationTests {

	@Test
	void contextLoads() {
	}
}
