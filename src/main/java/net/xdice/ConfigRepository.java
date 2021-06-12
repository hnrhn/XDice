package net.xdice;

import net.xdice.interfaces.XDiceRepository;
import net.xdice.utilities.ConfigStepConverter;
import net.xdice.utilities.CritFailBehaviourConverter;
import net.xdice.utilities.ExplodeBehaviourConverter;
import net.xdice.utilities.PlusBehaviourConverter;
import net.xdice.models.XDiceConfig;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigRepository implements XDiceRepository {
    private final Connection db;

    public ConfigRepository(Connection dbConnection) {
        db = dbConnection;
    }

    public HashSet<Long> getKnownGuilds() throws SQLException {
        assert db != null;  // TODO: Handle this properly
        Statement statement = db.createStatement();

        ResultSet results = statement.executeQuery("SELECT GuildId FROM configs");
        HashSet<Long> ids = new HashSet<>();
        while (results.next()) {
            ids.add(results.getLong("GuildId"));
        }

        return ids;
    }

    public XDiceConfig getConfig(Long guildId) throws SQLException {
        assert db != null;  // TODO: Handle this properly
        PreparedStatement statement = db.prepareStatement("SELECT * FROM configs WHERE GuildId = ?");
        statement.setLong(1, guildId);

        ResultSet results = statement.executeQuery();
        if (!results.next()){
            throw new SQLException("No Configuration found for guild " + guildId);  // TODO: By rights this shouldn't be a SQLException at all.
        }

        String nullableSuccessOn = results.getString("SuccessOn");
        String nullableExplodeOn = results.getString("ExplodeOn");

        return new XDiceConfig(
            guildId,
            results.getInt("InConfigMode") == 1,
            ConfigStepConverter.intToEnum(results.getInt("CurrentConfigStep")),
            results.getInt("DefaultDice"),
            results.getInt("CountSuccesses") == 1,
            nullableSuccessOn != null ? Arrays.stream(nullableSuccessOn.split(",")).map(Integer::parseInt).collect(Collectors.toList()) : null,
            results .getInt("AddTotal") == 1,
            PlusBehaviourConverter.intToEnum(results.getInt("PlusBehaviourId")),
            ExplodeBehaviourConverter.intToEnum(results.getInt("ExplodeBehaviourId")),
            nullableExplodeOn != null ? Arrays.stream(nullableExplodeOn.split(",")).map(Integer::parseInt).collect(Collectors.toList()) : null,
            CritFailBehaviourConverter.intToEnum(results.getInt("CritFailBehaviourId"))
        );
    }

    public void saveConfig(XDiceConfig config) throws SQLException {
        upsert(config, "configs");
    }

    public void saveConfigDraft(XDiceConfig draftConfig) throws SQLException {
        upsert(draftConfig, "config_drafts");
    }

    private void upsert(XDiceConfig config, String tableName) throws SQLException {
        assert db != null;  // TODO: Handle this properly
        PreparedStatement statement = db.prepareStatement("SELECT * FROM " + tableName + " WHERE GuildId = ?");
        statement.setLong(1, config.getGuildId());

        // TODO: Convert to SQLite UPSERT
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            PreparedStatement updateStatement = db.prepareStatement("UPDATE "+ tableName + " SET DefaultDice = ?, CountSuccesses = ?, SuccessOn = ?, AddTotal = ?, PlusBehaviourId = ?, ExplodeBehaviourId = ?, ExplodeOn = ?, CritFailBehaviourId = ? WHERE GuildId = ?");
            updateStatement.setInt(1, config.getDefaultDice());
            updateStatement.setInt(2, config.isCountSuccesses() ? 1 : 0);
            updateStatement.setString(3, Stream.of(config.getSuccessOn()).map(String::valueOf).collect(Collectors.joining(",")));
            updateStatement.setInt(4, config.isAddTotal() ? 1 : 0);
            updateStatement.setInt(5, PlusBehaviourConverter.enumToInt(config.getPlusBehaviour()));
            updateStatement.setInt(6, ExplodeBehaviourConverter.enumToInt(config.getExplodeBehaviour()));
            updateStatement.setString(7, Stream.of(config.getExplodeOn()).map(String::valueOf).collect(Collectors.joining(",")));
            updateStatement.setInt(8, CritFailBehaviourConverter.enumToInt(config.getCritFailBehaviour()));
            updateStatement.setLong(9, config.getGuildId());
            updateStatement.executeUpdate();
        } else {
            PreparedStatement insertStatement = db.prepareStatement("INSERT INTO " + tableName + " (GuildId, InConfigMode, CurrentConfigStep, DefaultDice, CountSuccesses, SuccessOn, AddTotal, PlusBehaviourId, ExplodeBehaviourId, ExplodeOn, CritFailBehaviourId) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            insertStatement.setLong(1, config.getGuildId());
            insertStatement.setInt(2, 0);
            insertStatement.setInt(3, 1);
            insertStatement.setInt(4, config.getDefaultDice());
            insertStatement.setInt(5, config.isCountSuccesses() ? 1 : 0);
            insertStatement.setString(6, config.getSuccessOn() != null ? Stream.of(config.getSuccessOn()).map(String::valueOf).collect(Collectors.joining(",")) : null);
            insertStatement.setInt(7, config.isAddTotal() ? 1 : 0);
            insertStatement.setInt(8, PlusBehaviourConverter.enumToInt(config.getPlusBehaviour()));
            insertStatement.setInt(9, ExplodeBehaviourConverter.enumToInt(config.getExplodeBehaviour()));
            insertStatement.setString(10, config.getExplodeOn() != null ? Stream.of(config.getExplodeOn()).map(String::valueOf).collect(Collectors.joining(",")) : null);
            insertStatement.setInt(11, CritFailBehaviourConverter.enumToInt(config.getCritFailBehaviour()));
            insertStatement.executeUpdate();
        }
    }
}
