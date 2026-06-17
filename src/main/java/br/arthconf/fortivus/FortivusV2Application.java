package br.arthconf.fortivus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FortivusV2Application {

    public static void main(String[] args) {
        SpringApplication.run(FortivusV2Application.class, args);
    }
}
