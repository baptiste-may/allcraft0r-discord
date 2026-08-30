package fr.may_baptiste.allcraft0r_discord;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Allcraft0rDiscordApplication {

  public static void main(String[] args) {
    final var dotenv = Dotenv.configure().ignoreIfMissing().load();
    dotenv
        .entries()
        .forEach(
            entry -> {
              if (System.getProperty(entry.getKey()) == null
                  && System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
              }
            });
    SpringApplication.run(Allcraft0rDiscordApplication.class, args);
  }
}
