import net.xdice.HelpGenerator;
import net.xdice.enums.ConfigStep;
import net.xdice.enums.CritFailBehaviour;
import net.xdice.enums.ExplodeBehaviour;
import net.xdice.enums.PlusBehaviour;
import net.xdice.models.XDiceConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HelpTextTests {
    HelpGenerator ai = new HelpGenerator();
    XDiceConfig config = new XDiceConfig(
            1L,
            false,
            ConfigStep.BEGIN,
            101,
            false,
            new ArrayList<>(),
            false,
            PlusBehaviour.IGNORE,
            ExplodeBehaviour.NONE,
            new ArrayList<>(),
            CritFailBehaviour.NONE
    );

    @Test
    void placeholderLineIsSuccessfullyRemoved() {
        assertFalse(ai.getHelp(config).contains("IF YOU ARE SEEING THIS LINE, THE DEVELOPER MESSED UP"));
    }

    @Test
    void basic() throws IOException, URISyntaxException {
        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/basic.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }
    
    @Test
    void addTotal() throws IOException, URISyntaxException {
        config.setAddTotal(true);

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/add_total.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void countSuccesses() throws IOException, URISyntaxException {
        config.setCountSuccesses(true);
        config.setSuccessOn(List.of(99));

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/count_successes.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void critFail() throws IOException, URISyntaxException {
        config.setCritFailBehaviour(CritFailBehaviour.ONE_NO_SUCCESSES);

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/crit_fail_one.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void explodeDouble() throws IOException, URISyntaxException {
        config.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        config.setExplodeOn(List.of(99));

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/explode_double.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void explodeExtra() throws IOException, URISyntaxException {
        config.setExplodeBehaviour(ExplodeBehaviour.EXTRA);
        config.setExplodeOn(List.of(99));

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/explode_extra.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void explodeExtraChain() throws IOException, URISyntaxException {
        config.setExplodeBehaviour(ExplodeBehaviour.EXTRA_CHAIN);
        config.setExplodeOn(List.of(99));

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/explode_extra_chain.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void plusAdd() throws IOException, URISyntaxException {
        config.setPlusBehaviour(PlusBehaviour.ADD);

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/plus_add.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void plusAutoSuccess() throws IOException, URISyntaxException {
        config.setPlusBehaviour(PlusBehaviour.AUTO_SUCCESS);

        var resourceFile = HelpTextTests.class.getResource("ExpectedHelpFiles/plus_auto.txt").toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = ai.getHelp(config);

        assertEquals(expectedString, result);
    }
}
