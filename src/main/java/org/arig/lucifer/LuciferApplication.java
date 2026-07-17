package org.arig.lucifer;

import javafx.application.Application;
import org.arig.lucifer.fx.LuciferFxApp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LuciferApplication implements CommandLineRunner {

    static void main(String[] args) {
        SpringApplication app = new SpringApplication(LuciferApplication.class);
        app.setHeadless(false);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        Application.launch(LuciferFxApp.class, args);
    }
}
