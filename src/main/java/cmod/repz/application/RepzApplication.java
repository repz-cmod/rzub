package cmod.repz.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class RepzApplication {

	public static void main(String[] args) {
		System.out.println(new Date());
		SpringApplication.run(RepzApplication.class, args);
	}

}
