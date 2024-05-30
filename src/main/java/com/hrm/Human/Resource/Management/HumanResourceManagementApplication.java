package com.hrm.Human.Resource.Management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HumanResourceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumanResourceManagementApplication.class, args);
	}

}
