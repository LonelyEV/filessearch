package onem.quzhigang.filessearch.filessearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("onem.quzhigang.filessearch")
public class FilessearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilessearchApplication.class, args);
	}
}
