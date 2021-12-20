import net.xdice.constants.Constants;
import net.xdice.discordintegration.DiscordConfigurator;
import net.xdice.behaviour.standard.StandardHelpGenerator;
import net.xdice.constants.ConfigInstructions;
import net.xdice.enums.ConfigStep;
import net.xdice.enums.CritFailBehaviour;
import net.xdice.enums.ExplodeBehaviour;
import net.xdice.enums.PlusBehaviour;
import net.xdice.interfaces.XDiceRepository;
import net.xdice.models.XDiceConfig;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiscordConfiguratorTests {
    String goodGuildId = "7357";
    String badGuildId = "848";

    XDiceRepository mockedRepo = mock(XDiceRepository.class);
    Server mockedServer = mock(Server.class);
    Message mockedMessage = mock(Message.class);
    ServerTextChannel mockedServerTextChannel = mock(ServerTextChannel.class);
    MessageAuthor mockedMessageAuthor = mock(MessageAuthor.class);
    User mockedUser = mock(User.class);

    StandardHelpGenerator helpGenerator = new StandardHelpGenerator();
    XDiceConfig baseConfig = XDiceConfig.getDefaultConfig("1");
    DiscordConfigurator dc = new DiscordConfigurator(mockedRepo, helpGenerator);

    private final Map<String, PlusBehaviour> correctPlusBehaviourMapping = Map.of(
            "1", PlusBehaviour.IGNORE,
            "2", PlusBehaviour.ADD,
            "3", PlusBehaviour.AUTO_SUCCESS
    );

    private final Map<String, ExplodeBehaviour> correctExplodeBehaviourMapping = Map.of(
            "2", ExplodeBehaviour.DOUBLE,
            "3", ExplodeBehaviour.EXTRA,
            "4", ExplodeBehaviour.EXTRA_CHAIN
    );

    @BeforeEach
    void init() throws SQLException {
        when(mockedRepo.getConfig(goodGuildId)).thenReturn(baseConfig);
        when(mockedRepo.getConfig(badGuildId)).thenThrow(new SQLException());

        when(mockedMessage.getAuthor()).thenReturn(mockedMessageAuthor);
        when(mockedMessage.getUserAuthor()).thenReturn(java.util.Optional.ofNullable(mockedUser));
        when(mockedMessage.getServer()).thenReturn(java.util.Optional.ofNullable(mockedServer));

        when(mockedServer.getIdAsString()).thenReturn(goodGuildId);
        when(mockedServer.getTextChannelsByName(Constants.configChannelName)).thenReturn(List.of(mockedServerTextChannel));

        when(mockedMessageAuthor.canManageServer()).thenReturn(true);

        when(mockedUser.getMentionTag()).thenReturn("TESTUSER");
    }

    @Test
    void configuratorReturnsERRORIfNoConfigFoundInRepo() {
        when(mockedServer.getIdAsString()).thenReturn(badGuildId);
        assertEquals("ERROR: Could not load an existing configuration for your server.", dc.configure(mockedMessage));
    }

    @Test
    void configuratorReturnsERRORIfUserDoesNotHavePermissionToManageServer() {
        when(mockedMessageAuthor.canManageServer()).thenReturn(false);
        assertEquals("TESTUSER: You do not have permission to manage XDice on this server.", dc.configure(mockedMessage));
    }

    @Test
    void configuratorCorrectlyCallsDeleteChannelMethod() {
        when(mockedMessage.getContent()).thenReturn("/xdice config delete");
        String result = dc.configure(mockedMessage);
        assertEquals("", result);
        verify(mockedServerTextChannel).delete();
    }

    @Test
    void configuratorReturnsGenericErrorIfMessageDoesNotMatchCurrentConfigStep() throws IOException, URISyntaxException {
        when(mockedMessage.getContent()).thenReturn("/xdice config default_dice");
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        assertEquals(expectedResponse, dc.configure(mockedMessage));
    }

    //region BEGIN step
    @Test
    @DisplayName("BEGIN step should set step to DEFAULT_DICE")
    void configuratorSetsCurrentStepToDefaultDice() {
        when(mockedMessage.getContent()).thenReturn("/xdice config begin");
        dc.configure(mockedMessage);
        assertEquals(ConfigStep.DEFAULT_DICE, baseConfig.getCurrentConfigStep());
    }

    @Test
    @DisplayName("BEGIN step should return the instructions for the DEFAULT_DICE step")
    void configuratorReturnsDefaultDiceMessage() throws IOException, URISyntaxException {
        when(mockedMessage.getContent()).thenReturn("/xdice config begin");
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/DefaultDice.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        String actualResponse = dc.configure(mockedMessage);
        assertTrue(almostMatches(expectedResponse, actualResponse, 4));
    }
    //endregion

    //region DEFAULT_DICE step
    @DisplayName("DEFAULT_DICE step should set next step to COUNT_SUCCESSES")
    @ParameterizedTest
    @ValueSource(strings = {"501", "d501"})
    void defaultDiceOne(String diceSelection) {
        baseConfig.setCurrentConfigStep(ConfigStep.DEFAULT_DICE);
        when(mockedMessage.getContent()).thenReturn("/xdice config default_dice " + diceSelection);

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.COUNT_SUCCESSES, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("DEFAULT_DICE step should set defaultDice to the value provided by the user")
    @ParameterizedTest
    @ValueSource(strings = {"501", "d501"})
    void defaultDiceTwo(String diceSelection) {
        baseConfig.setCurrentConfigStep(ConfigStep.DEFAULT_DICE);
        when(mockedMessage.getContent()).thenReturn("/xdice config default_dice " + diceSelection);

        dc.configure(mockedMessage);

        assertEquals(501, baseConfig.getDefaultDice());
    }

    @DisplayName("DEFAULT_DICE step returns the instructions for the COUNT_SUCCESSES step")
    @ParameterizedTest
    @ValueSource(strings = {"501", "d501"})
    void defaultDiceThree(String diceSelection) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.DEFAULT_DICE);
        when(mockedMessage.getContent()).thenReturn("/xdice config default_dice " + diceSelection);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/CountSuccesses.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("DEFAULT_DICE step returns the standard error message when the user does not supply a number")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", " banana"})
    void defaultDiceFour(String diceSelection) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.DEFAULT_DICE);
        when(mockedMessage.getContent()).thenReturn("/xdice config default_dice" + diceSelection);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }
    //endregion

    //region COUNT_SUCCESSES step
    @Test
    @DisplayName("When user selects Yes, COUNT_SUCCESSES step sets countSuccesses to TRUE")
    void countSuccessesOne() {
        baseConfig.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
        when(mockedMessage.getContent()).thenReturn("/xdice config count_successes yes");

        dc.configure(mockedMessage);

        assertTrue(baseConfig.isCountSuccesses());
    }

    @Test
    @DisplayName("When user selects Yes, COUNT_SUCCESSES step sets next Step to SUCCESS_ON")
    void countSuccessesTwo() {
        baseConfig.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
        when(mockedMessage.getContent()).thenReturn("/xdice config count_successes yes");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.SUCCESS_ON, baseConfig.getCurrentConfigStep());
    }

    @Test
    @DisplayName("When user selects Yes, COUNT_SUCCESSES step returns instructions for the SUCCESS_ON step")
    void countSuccessesThree() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
        when(mockedMessage.getContent()).thenReturn("/xdice config count_successes yes");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/SuccessOn.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When user selects No, COUNT_SUCCESSES step sets countSuccesses to FALSE")
    void countSuccessesFour() {
        baseConfig.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
        when(mockedMessage.getContent()).thenReturn("/xdice config count_successes no");

        dc.configure(mockedMessage);

        assertFalse(baseConfig.isCountSuccesses());
    }

    @Test
    @DisplayName("When user selects No, COUNT_SUCCESSES step sets next Step to ADD_TOTAL")
    void countSuccessesFive() {
        baseConfig.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
        when(mockedMessage.getContent()).thenReturn("/xdice config count_successes no");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.ADD_TOTAL, baseConfig.getCurrentConfigStep());
    }

    @Test
    @DisplayName("When user selects No, COUNT_SUCCESSES step returns instructions for the ADD_TOTAL step")
    void countSuccessesSix() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
        when(mockedMessage.getContent()).thenReturn("/xdice config count_successes no");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/AddTotal.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits invalid input, COUNT_SUCCESSES step returns the standard error message")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", " banana"})
    void countSuccessesSeven(String input) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.COUNT_SUCCESSES);
        when(mockedMessage.getContent()).thenReturn("/xdice config count_successes" + input);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        assertEquals(expectedResponse, dc.configure(mockedMessage));
    }
    //endregion

    //region SUCCESS_ON step
    @DisplayName("SUCCESS_ON step sets next step to PLUS_BEHAVIOUR")
    @Test
    void successOnOne() {
        baseConfig.setCurrentConfigStep(ConfigStep.SUCCESS_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config success_on 900");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.PLUS_BEHAVIOUR, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits a single number, SUCCESS_ON step sets successOn to an array containing that number")
    @Test
    void successOnTwo() {
        baseConfig.setCurrentConfigStep(ConfigStep.SUCCESS_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config success_on 900");

        dc.configure(mockedMessage);

        var newSuccessOnArray = baseConfig.getSuccessOn();
        assertTrue(newSuccessOnArray.size() == 1 && newSuccessOnArray.contains(900));
    }

    @DisplayName("When user submits multiple numbers, SUCCESS_ON step sets successOn to an array containing those numbers")
    @Test
    void successOnStep_Correctly_Sets_Multiple_Values() {
        baseConfig.setCurrentConfigStep(ConfigStep.SUCCESS_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config success_on 900 901");

        dc.configure(mockedMessage);

        var newSuccessOnArray = baseConfig.getSuccessOn();
        assertTrue(newSuccessOnArray.size() == 2 && newSuccessOnArray.containsAll(List.of(900, 901)));
    }

    @DisplayName("When user submits invalid input, SUCCESS_ON step returns standard error message")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", " 900  901"})
    void successOnStep_Throws_When_Multiple_Spaces_In_Selection(String input) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.SUCCESS_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config success_on" + input);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        dc.configure(mockedMessage);

        assertEquals(expectedResponse, dc.configure(mockedMessage));
    }

    @DisplayName("When user submits a number which is outside of the rollable range of the default die, SUCCESS_ON step returns the standard error message")
    @Test
    void successOn_TooHigh() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.SUCCESS_ON);
        baseConfig.setDefaultDice(20);
        when(mockedMessage.getContent()).thenReturn("/xdice config success_on 900");
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        dc.configure(mockedMessage);

        assertEquals(expectedResponse, dc.configure(mockedMessage));
    }

    @DisplayName("SUCCESS_ON step returns the PLUS_BEHAVIOUR instructions with the AddTo lines crossed out")
    @Test
    void successOnStep_Response_AddToTotalInvalid() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.SUCCESS_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config success_on 900");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/PlusBehaviour_AddToTotalDisabled.txt")).toURI(); // TODO: Add preamble.
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }
    //endregion

    //region ADD_TOTAL step
    @DisplayName("When user submits Yes, ADD_TOTAL step sets addTotal to True")
    @Test
    void addTotalOne() {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total yes");

        dc.configure(mockedMessage);

        assertTrue(baseConfig.isAddTotal());
    }

    @DisplayName("When user submits Yes, ADD_TOTAL step sets the next Step to PLUS_BEHAVIOUR")
    @Test
    void addTotalTwo() {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total yes");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.PLUS_BEHAVIOUR, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits Yes, ADD_TOTAL step returns PLUS_BEHAVIOUR instructions with the AutomaticSuccess lines crossed out")
    @Test
    void addTotalThree() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total yes");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/PlusBehaviour_AutomaticSuccessDisabled.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits No, ADD_TOTAL step sets addTotal to False")
    @Test
    void addTotalFour() {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total no");

        dc.configure(mockedMessage);

        assertFalse(baseConfig.isAddTotal());
    }

    @DisplayName("When user submits No, ADD_TOTAL step sets the next Step to EXPLODE_BEHAVIOUR")
    @Test
    void addTotalFive() {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total no");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.EXPLODE_BEHAVIOUR, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits No and countSuccesses is True, ADD_TOTAL step returns EXPLODE_BEHAVIOUR instructions")
    @Test
    void addTotalSix() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        baseConfig.setCountSuccesses(true);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total no");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ExplodeBehaviour.txt")).toURI();
        var expectedResponse = "No assistance needed on the ol' numbers, you've got it!\n\n" + Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits No and countSuccesses is False, ADD_TOTAL step returns EXPLODE_BEHAVIOUR instructions with Double Successes lines crossed out")
    @Test
    void addTotalSeven() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        baseConfig.setCountSuccesses(false);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total no");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ExplodeBehaviour_DoubleSuccessDisabled.txt")).toURI();
        var expectedResponse = "No assistance needed on the ol' numbers, you've got it!\n\n" + Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits invalid input, ADD_TOTAL step returns standard error")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", " banana"})
    void addTotalEight(String input) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.ADD_TOTAL);
        when(mockedMessage.getContent()).thenReturn("/xdice config add_total" + input);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        assertEquals(expectedResponse, dc.configure(mockedMessage));
    }
    //endregion

    //region PLUS_BEHAVIOUR step
    @DisplayName("PLUS_BEHAVIOUR step sets next Step to EXPLODE_BEHAVIOUR")
    @ParameterizedTest
    @ValueSource(strings = {"plus_behaviour", "plus_behavior"})
    void plusBehaviourStep_Valid(String command) {
        baseConfig.setCurrentConfigStep(ConfigStep.PLUS_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command + " 1");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.EXPLODE_BEHAVIOUR, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("PLUS_BEHAVIOUR step sets plusBehaviour to the correct Enum based on user's input")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void plusBehaviourTwo(String input) {
        baseConfig.setCurrentConfigStep(ConfigStep.PLUS_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config plus_behaviour " + input);

        dc.configure(mockedMessage);

        assertEquals(correctPlusBehaviourMapping.get(input), baseConfig.getPlusBehaviour());
    }

    @DisplayName("When countSuccess is True, PLUS_BEHAVIOUR step returns EXPLODE_BEHAVIOUR instructions")
    @ParameterizedTest
    @ValueSource(strings = {"plus_behaviour", "plus_behavior"})
    void plusBehaviourStep_Response(String command) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.PLUS_BEHAVIOUR);
        baseConfig.setCountSuccesses(true);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command + " 1");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ExplodeBehaviour.txt")).toURI();
        var expectedResponse = "Done and done!\n\n" + Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When countSuccess is False, PLUS_BEHAVIOUR step returns EXPLODE_BEHAVIOUR instructions with DoubleSuccess option crossed out")
    @ParameterizedTest
    @ValueSource(strings = {"plus_behaviour", "plus_behavior"})
    void plusBehaviourStep_Valid_Response(String command) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.PLUS_BEHAVIOUR);
        baseConfig.setCountSuccesses(false);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command + " 1");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ExplodeBehaviour_DoubleSuccessDisabled.txt")).toURI();
        var expectedResponse = "Done and done!\n\n" + Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plus_behaviour",
            "plus_behavior",
            "plus_behaviour ",
            "plus_behavior ",
            "plus_behaviour 4",
            "plus_behavior 4",
            "plus_behaviour banana",
            "plus_behavior banana"
    })
    void plusBehaviourStep_Invalid(String command) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.PLUS_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        assertEquals(expectedResponse, dc.configure(mockedMessage));
    }
    //endregion

    //region EXPLODE_BEHAVIOUR step
    @DisplayName("When user submits 1, EXPLODE_BEHAVIOUR step sets explodeBehaviour to NONE")
    @Test
    void explodeBehaviourOne() {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour 1");

        dc.configure(mockedMessage);

        assertEquals(ExplodeBehaviour.NONE, baseConfig.getExplodeBehaviour());
        assertEquals(ConfigStep.CONFIRM, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits 1 and countSuccesses is True, EXPLODE_BEHAVIOUR step sets next Step to CRIT_FAIL_BEHAVIOUR")
    @Test
    void explodeBehaviourTwo() {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        baseConfig.setCountSuccesses(true);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour 1");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.CRIT_FAIL_BEHAVIOUR, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits 1 and countSuccesses is False, EXPLODE_BEHAVIOUR step sets next Step to CONFIRM")
    @Test
    void explodeBehaviourThree() {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        baseConfig.setCountSuccesses(false);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour 1");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.CONFIRM, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits 1 and countSuccesses is True, EXPLODE_BEHAVIOUR step returns CRIT_FAIL_BEHAVIOUR instructions")
    @Test
    void explodeBehaviourFour() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        baseConfig.setCountSuccesses(true);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour 1");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/CritFail.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits 1 and countSuccesses is False, EXPLODE_BEHAVIOUR step returns CONFIRM instructions")
    @Test
    void explodeBehaviourFive() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        baseConfig.setCountSuccesses(false);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour 1");

        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/Confirm.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var responseArray = dc.configure(mockedMessage).split("------------------------------------------------------------------------------------------------------------------------\n");
        var actualResponse = responseArray[responseArray.length - 1];

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits any valid, non-1 value, EXPLODE_BEHAVIOUR step sets next Step to EXPLODE_ON")
    @ParameterizedTest
    @ValueSource(strings = {"2", "3", "4"})
    void explodeBehaviourSix(String input) {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour " + input);

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.EXPLODE_ON, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits any valid, non-1 value, EXPLODE_BEHAVIOUR step sets explodeBehaviour to correct Enum")
    @ParameterizedTest
    @ValueSource(strings = {"2", "3", "4"})
    void explodeBehaviourSeven(String input) {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour " + input);

        dc.configure(mockedMessage);

        assertEquals(correctExplodeBehaviourMapping.get(input), baseConfig.getExplodeBehaviour());
    }

    @DisplayName("When user submits any valid, non-1 value, EXPLODE_BEHAVIOUR step returns EXPLODE_ON instructions")
    @ParameterizedTest
    @ValueSource(strings = {"2", "3", "4"})
    void explodeBehaviourEight(String input) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour " + input);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ExplodeOn.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits invalid input, EXPLODE_BEHAVIOUR step returns standard error message")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", " banana"})
    void explodeBehaviourNine(String input) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_BEHAVIOUR);
        baseConfig.setExplodeBehaviour(ExplodeBehaviour.DOUBLE);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_behaviour " + input);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }
    //endregion

    //region EXPLODE_ON step
    @DisplayName("EXPLODE_ON step sets next Step to CRIT_FAIL_BEHAVIOUR")
    @Test
    void explodeOnOne() {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_on 101");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.CRIT_FAIL_BEHAVIOUR, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("When user submits one number, EXPLODE_ON step sets explodeOn to array containing only that number")
    @Test
    void explodeOnTwo() {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_on 101");

        dc.configure(mockedMessage);

        assertEquals(List.of(101), baseConfig.getExplodeOn());
    }

    @DisplayName("When user submits multiple numbers, EXPLODE_ON step sets explodeOn to array containing those numbers")
    @Test
    void explodeOnThree() {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_on 101 102 103");

        dc.configure(mockedMessage);

        assertEquals(List.of(101, 102, 103), baseConfig.getExplodeOn());
    }

    @DisplayName("EXPLODE_ON step returns CRIT_FAIL_BEHAVIOUR instructions")
    @Test
    void explodeOnStep_Response() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_on 101 102 103");
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/CritFail.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", " 101  102", " banana"})
    void explodeOnFive(String input) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.EXPLODE_ON);
        when(mockedMessage.getContent()).thenReturn("/xdice config explode_on" + input);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/ErrorMessage.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }
    //endregion

    //region CRIT_FAIL step
    @DisplayName("When user submits Yes, CRIT_FAIL_BEHAVIOUR step sets critFailBehaviour to ONE_NO_SUCCESSES")
    @ParameterizedTest
    @ValueSource(strings = {"crit_fail_behaviour yes", "crit_fail_behavior yes"})
    void critFailBehaviourOne(String command) {
        baseConfig.setCurrentConfigStep(ConfigStep.CRIT_FAIL_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command);

        dc.configure(mockedMessage);

        assertEquals(CritFailBehaviour.ONE_NO_SUCCESSES, baseConfig.getCritFailBehaviour());
    }

    @DisplayName("When user submits Yes, CRIT_FAIL_BEHAVIOUR step sets critFailBehaviour to NONE")
    @ParameterizedTest
    @ValueSource(strings = {"crit_fail_behaviour no", "crit_fail_behavior no"})
    void critFailBehaviourStep_No(String command) {
        baseConfig.setCurrentConfigStep(ConfigStep.CRIT_FAIL_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command);

        dc.configure(mockedMessage);

        assertEquals(CritFailBehaviour.NONE, baseConfig.getCritFailBehaviour());
    }

    @DisplayName("CRIT_FAIL_BEHAVIOUR step sets next step to CONFIRM")
    @ParameterizedTest
    @ValueSource(strings = {"crit_fail_behaviour yes", "crit_fail_behavior yes", "crit_fail_behaviour no", "crit_fail_behavior no"})
    void critFailBehaviourTwo(String command) {
        baseConfig.setCurrentConfigStep(ConfigStep.CRIT_FAIL_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command);

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.CONFIRM, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("CRIT_FAIL_BEHAVIOUR step returns CONFIRM instructions")
    @ParameterizedTest
    @ValueSource(strings = {"crit_fail_behaviour yes", "crit_fail_behavior yes", "crit_fail_behaviour no", "crit_fail_behavior no"})
    void critFailBehaviourStep_Yes_Response(String command) throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.CRIT_FAIL_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config " + command);
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/Confirm.txt")).toURI(); // TODO: This can be combined with below
        var expectedResponse = Files.readString(Path.of(resourceFile));

        // This response begins with a copy of the help-text, but that is tested elsewhere, so assume that's fine and check the last section
        var responseArray = dc.configure(mockedMessage).split("------------------------------------------------------------------------------------------------------------------------\n");
        var actualResponse = responseArray[responseArray.length - 1];

        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("When user submits invalid input, CRIT_FAIL_BEHAVIOUR step returns standard error message")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", " banana"})
    void critFailBehaviourStepInvalidInput(String input) {
        baseConfig.setCurrentConfigStep(ConfigStep.CRIT_FAIL_BEHAVIOUR);
        when(mockedMessage.getContent()).thenReturn("/xdice config crit_fail_behaviour" + input);

        String shouldBeErrorResponse = dc.configure(mockedMessage);

        assertEquals(ConfigInstructions.inputError, shouldBeErrorResponse);
    }
    //endregion

    //region CONFIRM step
    @DisplayName("CONFIRM step calls the SAVE method")
    @Test
    void confirmOne() throws SQLException {
        baseConfig.setCurrentConfigStep(ConfigStep.CONFIRM);
        when(mockedMessage.getContent()).thenReturn("/xdice config confirm");

        dc.configure(mockedMessage);

        verify(mockedRepo).saveConfig(baseConfig);
    }

    @DisplayName("CONFIRM step sets the next step to BEGIN")
    @Test
    void confirmTwo() {
        baseConfig.setCurrentConfigStep(ConfigStep.CONFIRM);
        when(mockedMessage.getContent()).thenReturn("/xdice config confirm");

        dc.configure(mockedMessage);

        assertEquals(ConfigStep.BEGIN, baseConfig.getCurrentConfigStep());
    }

    @DisplayName("CONFIRM step disables Config Mode")
    @Test
    void confirmThree() {
        baseConfig.setCurrentConfigStep(ConfigStep.CONFIRM);
        when(mockedMessage.getContent()).thenReturn("/xdice config confirm");

        dc.configure(mockedMessage);

        assertFalse(baseConfig.isConfigMode());
    }

    @DisplayName("CONFIRM step returns SignOff")
    @Test
    void confirmFour() throws IOException, URISyntaxException {
        baseConfig.setCurrentConfigStep(ConfigStep.CONFIRM);
        when(mockedMessage.getContent()).thenReturn("/xdice config confirm");
        var resourceFile = Objects.requireNonNull(DiscordConfiguratorTests.class.getResource("ExpectedConfigResponses/SignOff.txt")).toURI();
        var expectedResponse = Files.readString(Path.of(resourceFile));

        var actualResponse = dc.configure(mockedMessage);

        assertEquals(expectedResponse, actualResponse);
    }
    //endregion

    private boolean almostMatches(String str1, String str2, int maxDifferences) {
        int len1 = str1.length();
        int len2 = str2.length();
        if (len1 != len2) {
            return false;
        }

        int diff = 0;
        for (int i = 0; i < len1; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                diff++;
            }
        }

        return diff <= maxDifferences;
    }
}
