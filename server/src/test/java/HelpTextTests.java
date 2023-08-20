import net.xdice.behaviour.standard.StandardHelpGenerator;
import net.xdice.enums.CritFailBehaviour;
import net.xdice.enums.ExplodeBehaviour;
import net.xdice.enums.PlusBehaviour;
import net.xdice.models.XDiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HelpTextTests {
    StandardHelpGenerator helpGenerator = new StandardHelpGenerator();
    XDiceConfig config = XDiceConfig.getDefaultConfig("1");

    @BeforeEach
    void setDefaultDiceToOneHundredAndOne() {
        config.setDefaultDice(101);
    }

    @Test
    void placeholderLineIsSuccessfullyRemoved() {
        assertFalse(helpGenerator.getHelp(config).contains("IF YOU ARE SEEING THIS LINE, THE DEVELOPER MESSED UP"));
    }

    @Test
    void basic() throws IOException, URISyntaxException {
        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/basic.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }
    
    @Test
    void addTotal() throws IOException, URISyntaxException {
        config.setAddTotal(true);

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/add_total.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void countSuccesses() throws IOException, URISyntaxException {
        config.setCountSuccesses(true);
        config.setSuccessOn(List.of(99));

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/count_successes.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void critFail() throws IOException, URISyntaxException {
        config.setCritFailBehaviour(CritFailBehaviour.ONE_NO_SUCCESSES);

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/crit_fail_one.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void explodeDouble() throws IOException, URISyntaxException {
        config.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        config.setExplodeOn(List.of(99));

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/explode_double.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void explodeExtra() throws IOException, URISyntaxException {
        config.setExplodeBehaviour(ExplodeBehaviour.EXTRA);
        config.setExplodeOn(List.of(99));

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/explode_extra.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void explodeExtraChain() throws IOException, URISyntaxException {
        config.setExplodeBehaviour(ExplodeBehaviour.EXTRA_CHAIN);
        config.setExplodeOn(List.of(99));

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/explode_extra_chain.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void plusAdd() throws IOException, URISyntaxException {
        config.setPlusBehaviour(PlusBehaviour.ADD);

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/plus_add.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }

    @Test
    void plusAutoSuccess() throws IOException, URISyntaxException {
        config.setPlusBehaviour(PlusBehaviour.AUTO_SUCCESS);

        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ExpectedHelpFiles/plus_auto.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(config);

        assertEquals(expectedString, result);
    }
}
