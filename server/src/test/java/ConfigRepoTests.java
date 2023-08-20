import net.xdice.core.XDiceRepositoryImpl;
import net.xdice.enums.*;
import net.xdice.models.XDiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigRepoTests {
    private Connection testDb;
    private XDiceRepositoryImpl testRepo;
    private Map<String, XDiceConfig> correctResponses;

    @BeforeEach
    void init() throws SQLException {
        Connection conn;

        conn = DriverManager.getConnection("jdbc:sqlite::memory:");

        Statement createTableStatement = conn.createStatement();
        createTableStatement.executeUpdate("""
CREATE TABLE configs
(
    Id                  INTEGER NOT NULL CONSTRAINT configs_pk PRIMARY KEY,
    GuildId             INTEGER NOT NULL,
    InConfigMode        INTEGER DEFAULT 0 NOT NULL,
    CurrentConfigStep   INTEGER DEFAULT 1 NOT NULL,
    DefaultDice         INTEGER NOT NULL,
    CountSuccesses      INTEGER DEFAULT 0 NOT NULL,
    SuccessOn           TEXT,
    AddTotal            INTEGER NOT NULL,
    PlusBehaviourId     INTEGER DEFAULT 1 NOT NULL,
    ExplodeBehaviourId  INTEGER DEFAULT 1 NOT NULL,
    ExplodeOn           TEXT,
    CritFailBehaviourId INTEGER DEFAULT 1 NOT NULL,
    RollerId            INTEGER DEFAULT 1 NOT NULL
);

CREATE UNIQUE INDEX configs_GuildId_uindex
    ON configs (GuildId);

CREATE UNIQUE INDEX configs_Id_uindex
    ON configs (Id);
""");

            Statement insertStatement = conn.createStatement();
            insertStatement.executeUpdate("""
INSERT INTO configs
VALUES
    (1, 1001, 0, 1, 20, 0,       null, 0, 1, 1, null, 1, 1),
    (2, 1002, 0, 1, 10, 1, '10,9,8,7', 0, 3, 2, '10', 2, 1),
    (3, 1003, 0, 1, 10, 1, '10,9,8,7', 0, 3, 2, '10', 2, 1),
    (4, 1004, 0, 1, 10, 1, '10,9,8,7', 0, 3, 2, '10', 2, 1),
    (5, 1005, 0, 1, 20, 0,       null, 0, 1, 1, null, 1, 1),
    (6, 1006, 0, 1,  6, 1,      '6,5', 0, 1, 1, null, 2, 1),
    (7, 1007, 0, 1,  6, 1,      '6,5', 0, 1, 1, null, 2, 1),
    (8, 1008, 0, 1, 20, 0,       null, 0, 1, 1, null, 1, 1)
""");   // TODO: Deduplicate these test cases.

        Statement createDraftTableStatement = conn.createStatement();
        createDraftTableStatement.executeUpdate("""
CREATE TABLE config_drafts
(
    Id                  INTEGER NOT NULL CONSTRAINT configs_pk PRIMARY KEY,
    GuildId             INTEGER NOT NULL,
    InConfigMode        INTEGER DEFAULT 0 NOT NULL,
    CurrentConfigStep   INTEGER DEFAULT 1 NOT NULL,
    DefaultDice         INTEGER NOT NULL,
    CountSuccesses      INTEGER DEFAULT 0 NOT NULL,
    SuccessOn           TEXT,
    AddTotal            INTEGER NOT NULL,
    PlusBehaviourId     INTEGER DEFAULT 1 NOT NULL,
    ExplodeBehaviourId  INTEGER DEFAULT 1 NOT NULL,
    ExplodeOn           TEXT,
    CritFailBehaviourId INTEGER DEFAULT 1 NOT NULL,
    RollerId            INTEGER DEFAULT 1 NOT NULL
);

CREATE UNIQUE INDEX config_drafts_GuildId_uindex
    ON config_drafts (GuildId);

CREATE UNIQUE INDEX config_drafts_Id_uindex
    ON config_drafts (Id);
""");

        Statement insertDraftStatement = conn.createStatement();
        insertDraftStatement.executeUpdate("""
INSERT INTO config_drafts
VALUES
    (1, 1001, 0, 1, 20, 0,       null, 0, 1, 1, null, 1, 1),
    (2, 1002, 0, 1, 10, 1, '10,9,8,7', 0, 3, 2, '10', 2, 1),
    (3, 1003, 0, 1, 10, 1, '10,9,8,7', 0, 3, 2, '10', 2, 1),
    (4, 1004, 0, 1, 10, 1, '10,9,8,7', 0, 3, 2, '10', 2, 1),
    (5, 1005, 0, 1, 20, 0,       null, 0, 1, 1, null, 1, 1),
    (6, 1006, 0, 1,  6, 1,      '6,5', 0, 1, 1, null, 2, 1),
    (7, 1007, 0, 1,  6, 1,      '6,5', 0, 1, 1, null, 2, 1),
    (8, 1008, 0, 1, 20, 0,       null, 0, 1, 1, null, 1, 1)
""");   // TODO: Deduplicate these test cases.

        testDb = conn;
        testRepo = new XDiceRepositoryImpl(conn);

        correctResponses = Map.of(
            "1001", new XDiceConfig("1001", false, ConfigStep.BEGIN, 20, false, null, false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, null, CritFailBehaviour.NONE, RollerSelection.STANDARD),
            "1002", new XDiceConfig("1002", false, ConfigStep.BEGIN, 10, true, List.of(10,9,8,7), false, PlusBehaviour.AUTO_SUCCESS, ExplodeBehaviour.DOUBLE, List.of(10), CritFailBehaviour.ONE_NO_SUCCESSES, RollerSelection.STANDARD),
            "1003", new XDiceConfig("1003", false, ConfigStep.BEGIN, 10, true, List.of(10,9,8,7), false, PlusBehaviour.AUTO_SUCCESS, ExplodeBehaviour.DOUBLE, List.of(10), CritFailBehaviour.ONE_NO_SUCCESSES, RollerSelection.STANDARD),
            "1004", new XDiceConfig("1004", false, ConfigStep.BEGIN, 10, true, List.of(10,9,8,7), false, PlusBehaviour.AUTO_SUCCESS, ExplodeBehaviour.DOUBLE, List.of(10), CritFailBehaviour.ONE_NO_SUCCESSES, RollerSelection.STANDARD),
            "1005", new XDiceConfig("1005", false, ConfigStep.BEGIN, 20, false, null, false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, null, CritFailBehaviour.NONE, RollerSelection.STANDARD),
            "1006", new XDiceConfig("1006", false, ConfigStep.BEGIN, 6, true, List.of(6,5), false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, null, CritFailBehaviour.ONE_NO_SUCCESSES, RollerSelection.STANDARD),
            "1007", new XDiceConfig("1007", false, ConfigStep.BEGIN, 6, true, List.of(6,5), false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, null, CritFailBehaviour.ONE_NO_SUCCESSES, RollerSelection.STANDARD),
            "1008", new XDiceConfig("1008", false, ConfigStep.BEGIN, 20, false, null, false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, null, CritFailBehaviour.NONE, RollerSelection.STANDARD)
        );
    }

    @Test
    void GetKnownConfigsReturnsListOfAllIDs() throws SQLException {
        HashSet<String> result = testRepo.getKnownGuilds();
        assertEquals(correctResponses.size(), result.size());
        for (String id : correctResponses.keySet()) {
            assertTrue(result.contains(id));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "1001", "1002", "1003", "1004", "1005", "1006", "1007", "1008" })
    void GetConfigReturnsCorrectEntry(String id) throws SQLException {
        XDiceConfig expectedConfig = correctResponses.get(id);
        XDiceConfig actualConfig = testRepo.getConfig(id);

        assertEquals(expectedConfig.getGuildId(), actualConfig.getGuildId());
        assertEquals(expectedConfig.isConfigMode(), actualConfig.isConfigMode());
        assertEquals(expectedConfig.getCurrentConfigStep(), actualConfig.getCurrentConfigStep());
        assertEquals(expectedConfig.getDefaultDice(), actualConfig.getDefaultDice());
        assertEquals(expectedConfig.isCountSuccesses(), actualConfig.isCountSuccesses());
        assertEquals(expectedConfig.getSuccessOn(), actualConfig.getSuccessOn());
        assertEquals(expectedConfig.isAddTotal(), actualConfig.isAddTotal());
        assertEquals(expectedConfig.getPlusBehaviour(), actualConfig.getPlusBehaviour());
        assertEquals(expectedConfig.getExplodeBehaviour(), actualConfig.getExplodeBehaviour());
        assertEquals(expectedConfig.getExplodeOn(), actualConfig.getExplodeOn());
        assertEquals(expectedConfig.getCritFailBehaviour(), actualConfig.getCritFailBehaviour());
    }

    @Test
    void SaveConfigUpdatesExistingRowIfConfigWithMatchingGuildIdExistedInDb() throws SQLException {
        Statement countBeforeStatement = testDb.createStatement();
        ResultSet countBeforeResult = countBeforeStatement.executeQuery("SELECT COUNT(*) FROM configs");
        countBeforeResult.next();
        int countBefore = countBeforeResult.getInt(1);

        XDiceConfig changedConfig = correctResponses.get("1001");
        int newDefaultDice = changedConfig.getDefaultDice() + 1;
        changedConfig.setDefaultDice(newDefaultDice);
        testRepo.saveConfig(changedConfig);

        Statement countAfterStatement = testDb.createStatement();
        ResultSet countAfterResult = countAfterStatement.executeQuery("SELECT COUNT(*) FROM configs");
        countAfterResult.next();
        int countAfter = countAfterResult.getInt(1);

        Statement changedDefaultDiceStatement = testDb.createStatement();
        ResultSet changedDefaultDiceResult = changedDefaultDiceStatement.executeQuery("SELECT DefaultDice FROM configs WHERE GuildId = 1001");
        changedDefaultDiceResult.next();
        int savedDefaultDice = changedDefaultDiceResult.getInt(1);

        assertEquals(countBefore, countAfter, "New row created");
        assertEquals(newDefaultDice, savedDefaultDice, "Adjusted value not saved");
    }

    @Test
    void SaveConfigCreatesNewRowIfConfigDoesNotExistInDB() throws SQLException {
        Statement countBeforeStatement = testDb.createStatement();
        ResultSet countBeforeResult = countBeforeStatement.executeQuery("SELECT COUNT(*) FROM configs");
        countBeforeResult.next();
        int countBefore = countBeforeResult.getInt(1);

        XDiceConfig newConfig = new XDiceConfig("2001", false, ConfigStep.BEGIN, 100, false, null, false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, null, CritFailBehaviour.NONE, RollerSelection.STANDARD);
        testRepo.saveConfig(newConfig);

        Statement countAfterStatement = testDb.createStatement();
        ResultSet countAfterResult = countAfterStatement.executeQuery("SELECT COUNT(*) FROM configs");
        countAfterResult.next();
        int countAfter = countAfterResult.getInt(1);

        assertNotEquals(countBefore, countAfter);

        Statement changedDefaultDiceStatement = testDb.createStatement();
        ResultSet changedDefaultDiceResult = changedDefaultDiceStatement.executeQuery("SELECT * FROM configs WHERE GuildId = 2001");
        if (!changedDefaultDiceResult.next()) {
            fail("No Config with ID 2001 in TestDB");
        }
    }

    @Test
    void SaveDraftConfigUpdatesExistingRowIfConfigWithMatchingGuildIdExistedInDb() throws SQLException {
        Statement countBeforeStatement = testDb.createStatement();
        ResultSet countBeforeResult = countBeforeStatement.executeQuery("SELECT COUNT(*) FROM config_drafts");
        countBeforeResult.next();
        int countBefore = countBeforeResult.getInt(1);

        XDiceConfig changedConfig = correctResponses.get("1001");
        int newDefaultDice = changedConfig.getDefaultDice() + 1;
        changedConfig.setDefaultDice(newDefaultDice);
        testRepo.saveConfigDraft(changedConfig);

        Statement countAfterStatement = testDb.createStatement();
        ResultSet countAfterResult = countAfterStatement.executeQuery("SELECT COUNT(*) FROM config_drafts");
        countAfterResult.next();
        int countAfter = countAfterResult.getInt(1);

        Statement changedDefaultDiceStatement = testDb.createStatement();
        ResultSet changedDefaultDiceResult = changedDefaultDiceStatement.executeQuery("SELECT DefaultDice FROM config_drafts WHERE GuildId = 1001");
        changedDefaultDiceResult.next();
        int savedDefaultDice = changedDefaultDiceResult.getInt(1);

        assertEquals(countBefore, countAfter, "New row created");
        assertEquals(newDefaultDice, savedDefaultDice, "Adjusted value not saved");
    }

    @Test
    void SaveDraftConfigCreatesNewRowIfConfigDoesNotExistInDB() throws SQLException {
        Statement countBeforeStatement = testDb.createStatement();
        ResultSet countBeforeResult = countBeforeStatement.executeQuery("SELECT COUNT(*) FROM config_drafts");
        countBeforeResult.next();
        int countBefore = countBeforeResult.getInt(1);

        XDiceConfig newConfig = new XDiceConfig("2001", false, ConfigStep.BEGIN, 100, false, null, false, PlusBehaviour.IGNORE, ExplodeBehaviour.NONE, null, CritFailBehaviour.NONE, RollerSelection.STANDARD);
        testRepo.saveConfigDraft(newConfig);

        Statement countAfterStatement = testDb.createStatement();
        ResultSet countAfterResult = countAfterStatement.executeQuery("SELECT COUNT(*) FROM config_drafts");
        countAfterResult.next();
        int countAfter = countAfterResult.getInt(1);

        assertNotEquals(countBefore, countAfter);

        Statement changedDefaultDiceStatement = testDb.createStatement();
        ResultSet changedDefaultDiceResult = changedDefaultDiceStatement.executeQuery("SELECT * FROM config_drafts WHERE GuildId = 2001");
        if (!changedDefaultDiceResult.next()) {
            fail("No Config with ID 2001 in TestDB");
        }
    }

    @Test
    @Disabled
    void getConfigThrowsSQLExceptionIfNoConfigWithSpecifiedIDExists() {
        assertThrows(SQLException.class, () -> testRepo.getConfig("9999"));
    }
}
