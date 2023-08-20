package net.xdice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Environment {
    private static Environment Instance = null;

    private final EnvironmentType CurrentEnvironment;
    private final String DiscordToken;

    public EnvironmentType getCurrentEnvironment() {
        return CurrentEnvironment;
    }

    public String getDiscordToken() {
        return DiscordToken;
    }

    private Environment(EnvironmentType env, String token) {
        CurrentEnvironment = env;
        DiscordToken = token;
    }

    public static synchronized Environment getInstance() {
        if (Instance == null) {
            try {
                String envString = Files.readString(Paths.get("current_environment.txt"));

                Path devTokenFile = Paths.get("DEV_TOKEN_DO_NOT_COMMIT.txt");
                Path prodTokenFile = Paths.get("PROD_TOKEN_DO_NOT_COMMIT.txt");
                switch (envString) {
                    case "dev" -> Instance = new Environment(EnvironmentType.Development, Files.readString(devTokenFile));
                    case "prod" -> Instance = new Environment(EnvironmentType.Production, Files.readString(prodTokenFile));
                    default -> throw new IOException("Environment from 'current_environment.txt' is not valid. Valid strings are 'dev' and 'prod'.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return Instance;
    }
}

