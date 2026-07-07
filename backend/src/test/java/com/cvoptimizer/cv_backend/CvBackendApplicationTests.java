package com.cvoptimizer.cv_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CvBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
