package com.example.spring18;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.shell.interactive.enabled=false"})
class ApplicationTests {

	@Test
	void contextLoads() {
	}
}
