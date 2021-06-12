import net.xdice.enums.ConfigStep;
import net.xdice.enums.CritFailBehaviour;
import net.xdice.enums.ExplodeBehaviour;
import net.xdice.enums.PlusBehaviour;
import net.xdice.models.XDiceConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTests {
    @Test
    void defaultConfigHasNotChanged() {
        XDiceConfig actual = XDiceConfig.getDefaultConfig(7357L);
        assertEquals(7357L, actual.getGuildId());
        assertFalse(actual.isConfigMode());
        assertEquals(ConfigStep.BEGIN, actual.getCurrentConfigStep());
        assertEquals(20, actual.getDefaultDice());
        assertFalse(actual.isCountSuccesses());
        assertNull(actual.getSuccessOn());
        assertFalse(actual.isAddTotal());
        assertEquals(PlusBehaviour.IGNORE, actual.getPlusBehaviour());
        assertEquals(ExplodeBehaviour.NONE, actual.getExplodeBehaviour());
        assertNull(actual.getExplodeOn());
        assertEquals(CritFailBehaviour.NONE, actual.getCritFailBehaviour());
    }
}
