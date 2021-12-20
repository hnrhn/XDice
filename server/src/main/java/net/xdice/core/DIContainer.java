package net.xdice.core;

import net.xdice.behaviour.standard.ArtificialIntelligence;
import net.xdice.behaviour.standard.StandardHelpGenerator;
import net.xdice.behaviour.standard.StandardParser;
import net.xdice.behaviour.standard.StandardRoller;
import net.xdice.discordintegration.DiscordConfigurator;
import net.xdice.enums.RollerSelection;
import net.xdice.behaviour.forbiddenlands.FLHelpGenerator;
import net.xdice.behaviour.forbiddenlands.FLParser;
import net.xdice.behaviour.forbiddenlands.FLRoller;
import net.xdice.interfaces.HelpGenerator;
import net.xdice.interfaces.XDiceParser;
import net.xdice.interfaces.XDiceRoller;
import net.xdice.models.XDiceConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DIContainer {
    private static net.xdice.core.XDiceRepositoryImpl XDiceRepositoryImpl;

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

    private final String configId;
    private final String userId;
    private final XDiceConfig config;
    private final HelpGenerator helpGenerator;
    private final DiscordConfigurator discordConfigurator;
    private final XDiceRoller diceRoller;
    private final ArtificialIntelligence ai;
    private final XDiceParser parser;

    public DIContainer(String configId, String userId) throws SQLException {
        this.configId = configId;
        this.userId = userId;
        config = XDiceRepositoryImpl.getConfig(this.configId);

        this.ai = new ArtificialIntelligence();

        if (config.getRollerSelection() == RollerSelection.FORBIDDENLANDS) {
            this.diceRoller = new FLRoller();
            this.parser = new FLParser();
            this.helpGenerator = new FLHelpGenerator();
        } else {
            this.diceRoller = new StandardRoller();
            this.parser = new StandardParser();
            this.helpGenerator = new StandardHelpGenerator();
        }

        this.discordConfigurator = new DiscordConfigurator(XDiceRepositoryImpl, helpGenerator);
    }

    public String getConfigId() {
        return configId;
    }

    public String getUserId() {
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
