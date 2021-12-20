package net.xdice.interfaces;

import net.xdice.models.XDiceConfig;

import java.sql.SQLException;
import java.util.HashSet;

public interface XDiceRepository {
    HashSet<String> getKnownGuilds() throws SQLException;
    XDiceConfig getConfig(String guildId) throws SQLException;
    void saveConfig(XDiceConfig config) throws SQLException;
    void saveConfigDraft(XDiceConfig draftConfig) throws SQLException;
}
