package com.ing.brokerage;

import com.ing.brokerage.controller.BrokerageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class BrokerageApplicationTests {

	@Autowired
	private BrokerageController brokerageController;

	@Test
	void contextLoads() {
		assertThat(brokerageController).isNotNull();
	}

}
