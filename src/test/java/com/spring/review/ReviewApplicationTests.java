package com.spring.review;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Skipped: requires full context with DB sequences. Use unit tests instead.")
class ReviewApplicationTests {

	@Test
	void contextLoads() {
	}

}
