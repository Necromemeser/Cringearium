package com.edu.cringearium;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CringeariumApplication {

	public static void main(String[] args) {
		SpringApplication.run(CringeariumApplication.class, args);
	}

}