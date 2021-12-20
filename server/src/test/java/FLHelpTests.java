import net.xdice.behaviour.forbiddenlands.FLHelpGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FLHelpTests {
    FLHelpGenerator helpGenerator = new FLHelpGenerator();

    @Test
    void flHelpGenerator_Returns_Correct_Help_Text() throws URISyntaxException, IOException {
        var resourceFile = Objects.requireNonNull(HelpTextTests.class.getResource("ForbiddenLands/help.txt")).toURI();
        var expectedString = Files.readString(Path.of(resourceFile));

        var result = helpGenerator.getHelp(null);

        assertEquals(expectedString, result);
    }
}
