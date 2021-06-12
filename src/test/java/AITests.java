import net.xdice.ArtificialIntelligence;
import net.xdice.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AITests {
    private ArtificialIntelligence ai;

    @BeforeAll
    public void init() {
        ai = new ArtificialIntelligence();
    }

    @RepeatedTest(100)
    void wtfReturnsFromCorrectPool() {
        assertTrue(new HashSet<>(Arrays.asList(Constants.wtfResponses)).contains(ai.randomWtfResponse()));
    }

    @RepeatedTest(100)
    void loveReturnsFromCorrectPool() {
        assertEquals(":heart:", ai.randomLoveResponse());
    }

    @RepeatedTest(100)
    void insultReturnsFromCorrectPool() {
        assertTrue(new HashSet<>(Arrays.asList(Constants.insultResponses)).contains(ai.randomInsultResponse()));
    }

    @RepeatedTest(100)
    void thanksReturnsFromCorrectPool() {
        assertTrue(new HashSet<>(Arrays.asList(Constants.thanksResponses)).contains(ai.randomThanksResponse()));
    }
}
