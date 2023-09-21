package co.kr.poptrend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class PoptrendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoptrendApplication.class, args);
	}

}
