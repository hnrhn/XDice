package net.xdice;

import net.xdice.enums.RollerSelection;
import net.xdice.fallenkingdoms.FKHelpGenerator;
import net.xdice.fallenkingdoms.FKParser;
import net.xdice.fallenkingdoms.FKRoller;
import net.xdice.interfaces.HelpGenerator;
import net.xdice.interfaces.XDiceParser;
import net.xdice.interfaces.XDiceRoller;
import net.xdice.models.XDiceConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DIContainer {
    private static XDiceRepositoryImpl XDiceRepositoryImpl;

    static {
        try {
            Connection databaseConnection = DriverManager.getConnection("jdbc:sqlite:prod.db");
            XDiceRepositoryImpl = new XDiceRepositoryImpl(databaseConnection);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static XDiceRepositoryImpl getRepository() {
        return XDiceRepositoryImpl;
    }

    private final long configId;
    private final long userId;
    private final XDiceConfig config;
    private final HelpGenerator helpGenerator;
    private final DiscordConfigurator discordConfigurator;
    private final XDiceRoller diceRoller;
    private final ArtificialIntelligence ai;
    private final XDiceParser parser;

    public DIContainer(long configId, long userId) throws SQLException {
        this.configId = configId;
        this.userId = userId;
        config = XDiceRepositoryImpl.getConfig(this.configId);

        this.ai = new ArtificialIntelligence();

        if (config.getRollerSelection() == RollerSelection.FALLENKINGDOMS) {
            this.diceRoller = new FKRoller();
            this.parser = new FKParser();
            this.helpGenerator = new FKHelpGenerator();
        } else {
            this.diceRoller = new StandardRoller();
            this.parser = new StandardParser();
            this.helpGenerator = new StandardHelpGenerator();
        }

        this.discordConfigurator = new DiscordConfigurator(XDiceRepositoryImpl, helpGenerator);
    }

    public long getConfigId() {
        return configId;
    }

    public long getUserId() {
        return userId;
    }

    public HelpGenerator getHelpGenerator() {
        return helpGenerator;
    }

    public DiscordConfigurator getDiscordConfigurator() {
        return discordConfigurator;
    }

    public XDiceRoller getDiceRoller() {
        return diceRoller;
    }

    public ArtificialIntelligence getAi() {
        return ai;
    }

    public XDiceConfig getConfig() {
        return config;
    }

    public XDiceParser getParser() {
        return parser;
    }
}
