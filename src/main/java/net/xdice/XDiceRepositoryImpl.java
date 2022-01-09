package net.xdice;

import net.xdice.forbiddenlands.FLResult;
import net.xdice.interfaces.XDiceRepository;
import net.xdice.utilities.*;
import net.xdice.models.XDiceConfig;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XDiceRepositoryImpl implements XDiceRepository {
    private final Connection db;

    public XDiceRepositoryImpl(Connection dbConnection) {
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
            CritFailBehaviourConverter.intToEnum(results.getInt("CritFailBehaviourId")),
            RollerSelectionConverter.intToEnum(results.getInt("RollerId"))
        );
    }

    public void saveConfig(XDiceConfig config) throws SQLException {
        upsertConfig(config, "configs");
    }

    public FLResult getMostRecentRoll(String userId) throws SQLException {
        PreparedStatement rollStatement = db.prepareStatement("SELECT * FROM fk_last_rolls WHERE UserId = ?");
        rollStatement.setString(1, userId);
        ResultSet results = rollStatement.executeQuery();

        return new FLResult(
            results.getString("Base") != null ? Arrays.stream(results.getString("Base").split(",")).map(Integer::parseInt).collect(Collectors.toList()) : null,
            results.getString("Skill") != null ? Arrays.stream(results.getString("Skill").split(",")).map(Integer::parseInt).collect(Collectors.toList()) : null,
            results.getString("Gear") != null ? Arrays.stream(results.getString("Gear").split(",")).map(Integer::parseInt).collect(Collectors.toList()) : null,
            results.getInt("ArtifactType"),
            results.getString("Artifact") != null ? Arrays.stream(results.getString("Artifact").split(",")).map(Integer::parseInt).collect(Collectors.toList()) : null,
            results.getString("Pride") != null ? Arrays.stream(results.getString("Pride").split(",")).map(Integer::parseInt).collect(Collectors.toList()) : null,
            results.getBoolean("Pushed")
        );
    }

    public void upsertMostRecentRoll(String userId, FLResult roll, boolean isPush) throws SQLException {
        PreparedStatement upsert = db.prepareStatement("" +
            "INSERT INTO fk_last_rolls(UserId, Base, Skill, Gear, ArtifactType, Artifact, Pride, Pushed) " +
            "VALUES(?,?,?,?,?,?,?,?) " +
            "ON CONFLICT(UserId) DO " +
            "UPDATE SET " +
            "Base = excluded.Base, " +
            "Skill = excluded.Skill, " +
            "Gear = excluded.Gear, " +
            "Artifact = excluded.Artifact, " +
            "Pride = excluded.Pride, " +
            "Pushed = excluded.Pushed");
        upsert.setString(1, userId);

        if (roll.getBaseResult().size() == 0) {
            upsert.setNull(2, Types.VARCHAR);
        } else {
            upsert.setString(2, roll.getBaseResult().toString().replace("[", "").replace("]", "").replaceAll(" ", ""));
        }

        if (roll.getSkillResult().size() == 0) {
            upsert.setNull(3, Types.VARCHAR);
        } else {
            upsert.setString(3, roll.getSkillResult().toString().replace("[", "").replace("]", "").replaceAll(" ", ""));
        }

        if (roll.getGearResult().size() == 0) {
            upsert.setNull(4, Types.VARCHAR);
        } else {
            upsert.setString(4, roll.getGearResult().toString().replace("[", "").replace("]", "").replaceAll(" ", ""));
        }

        if (roll.getArtifactDieType() == null) {
            upsert.setNull(5, Types.INTEGER);
        } else {
            upsert.setInt(5, roll.getArtifactDieType());
        }

        if (roll.getArtifactResult().size() != 0) {
            upsert.setString(6, roll.getArtifactResult().toString().replace("[", "").replace("]", "").replaceAll(" ", ""));
        } else {
            upsert.setNull(6, Types.VARCHAR);
        }

        if (roll.getPrideResult().size() != 0) {
            upsert.setString(7, roll.getPrideResult().toString().replace("[", "").replace("]", "").replaceAll(" ", ""));
        } else {
            upsert.setNull(7, Types.VARCHAR);
        }

        upsert.setBoolean(8, isPush);

        upsert.executeUpdate();
    }

    public void saveConfigDraft(XDiceConfig draftConfig) throws SQLException {
        upsertConfig(draftConfig, "config_drafts");
    }

    public void upsertRemainingCards(String serverId, List<Integer> remainingCards) throws SQLException {
        assert db != null;
        PreparedStatement upsertStatement = db.prepareStatement("" +
            "INSERT INTO fk_initiative_deck" +
            "(GuildId, RemainingCards)" +
            "VALUES (?,?)" +
            "ON CONFLICT(GuildId) DO " +
            "UPDATE SET " +
            "RemainingCards = excluded.RemainingCards");
        upsertStatement.setString(1, serverId);
        if (remainingCards.size() != 0) {
            upsertStatement.setString(2, remainingCards.toString().replace("[", "").replace("]", "").replaceAll(" ", ""));
        } else {
            upsertStatement.setNull(2, Types.VARCHAR);
        }
        upsertStatement.executeUpdate();
    }

    public List<Integer> getRemainingCards(String guildId) throws SQLException {
        assert db != null;
        PreparedStatement getCardsStatement = db.prepareStatement("SELECT * FROM fk_initiative_deck WHERE GuildId = ?");
        getCardsStatement.setString(1, guildId);
        ResultSet results = getCardsStatement.executeQuery();
        String remainingCards = results.getString("RemainingCards");
        if (remainingCards == null) {
            return null;
        }
        return Arrays.stream(remainingCards.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private void upsertConfig(XDiceConfig config, String tableName) throws SQLException {
        assert db != null;  // TODO: Handle this properly
        PreparedStatement upsertStatement = db.prepareStatement("" +
            "INSERT INTO $tableName".replace("$tableName", tableName) +
            "(GuildId, InConfigMode, CurrentConfigStep, DefaultDice, CountSuccesses, SuccessOn, AddTotal, PlusBehaviourId, ExplodeBehaviourId, ExplodeOn, CritFailBehaviourId, RollerId) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?) " +
            "ON CONFLICT(GuildId) DO " +
            "UPDATE SET " +
            "CurrentConfigStep = excluded.CurrentConfigStep," +
            "DefaultDice = excluded.DefaultDice, " +
            "CountSuccesses = excluded.CountSuccesses, " +
            "SuccessOn = excluded.SuccessOn, " +
            "AddTotal = excluded.AddTotal, " +
            "PlusBehaviourId = excluded.PlusBehaviourId, " +
            "ExplodeBehaviourId = excluded.ExplodeBehaviourId, " +
            "ExplodeOn = excluded.ExplodeOn, " +
            "CritFailBehaviourId = excluded.CritFailBehaviourId, " +
            "RollerId = excluded.RollerId");
        upsertStatement.setLong(1, config.getGuildId());
        upsertStatement.setInt(2, 0);
        upsertStatement.setInt(3, ConfigStepConverter.enumToInt(config.getCurrentConfigStep()));
        upsertStatement.setInt(4, config.getDefaultDice());
        upsertStatement.setInt(5, config.isCountSuccesses() ? 1 : 0);
        upsertStatement.setString(6, config.getSuccessOn() != null ? Stream.of(config.getSuccessOn()).map(String::valueOf).collect(Collectors.joining(",")).replace("[", "").replace("]", "").replaceAll(" ", "") : null);
        upsertStatement.setInt(7, config.isAddTotal() ? 1 : 0);
        upsertStatement.setInt(8, PlusBehaviourConverter.enumToInt(config.getPlusBehaviour()));
        upsertStatement.setInt(9, ExplodeBehaviourConverter.enumToInt(config.getExplodeBehaviour()));
        upsertStatement.setString(10, config.getExplodeOn() != null ? Stream.of(config.getExplodeOn()).map(String::valueOf).collect(Collectors.joining(",")).replace("[", "").replace("]", "").replaceAll(" ", "") : null);
        upsertStatement.setInt(11, CritFailBehaviourConverter.enumToInt(config.getCritFailBehaviour()));
        upsertStatement.setInt(12, RollerSelectionConverter.enumToInt(config.getRollerSelection()));
        upsertStatement.executeUpdate();
    }
}
