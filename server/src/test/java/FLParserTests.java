import net.xdice.enums.CommandType;
import net.xdice.behaviour.forbiddenlands.FLParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class FLParserTests {
    FLParser parser = new FLParser();

    @Test
    void Slash_Help_Emits_Command_Of_Type_HELP() {
        assertEquals(CommandType.HELP, parser.parseCommandString("/help", null).getCommandType());
    }

    @Test
    void Slash_NewDeck_Emits_Command_Of_Type_FL_CREATE_NEW_DECK() {
        assertEquals(CommandType.FL_CREATE_NEW_DECK, parser.parseCommandString("/newdeck", null).getCommandType());
    }

    @Test
    void Slash_Draw_Emits_Command_Of_Type_FL_DRAW_INITIATIVE() {
        assertEquals(CommandType.FL_DRAW_INITIATIVE, parser.parseCommandString("/draw", null).getCommandType());
    }

    @Test
    void Slash_Pride_Emits_Command_Of_Type_FL_PRIDE() {
        assertEquals(CommandType.FL_PRIDE, parser.parseCommandString("/pride", null).getCommandType());
    }

    @Test
    void Slash_Push_Emits_Command_Of_Type_FL_PUSH() {
        assertEquals(CommandType.FL_PUSH, parser.parseCommandString("/push", null).getCommandType());
    }

    @Test
    void FL_Roll_Emits_Command_Of_Type_FL_ROLL() {
        assertEquals(CommandType.FL_ROLL, parser.parseCommandString("/r 1 1 1", null).getCommandType());
    }

    @Test
    void FL_Roll_Emits_Artifact_8_When_User_Passes_8() {
        assertEquals(8, parser.parseCommandString("/r 1 1 1 8", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_8_When_User_Passes_d8() {
        assertEquals(8, parser.parseCommandString("/r 1 1 1 d8", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_8_When_User_Passes_m() {
        assertEquals(8, parser.parseCommandString("/r 1 1 1 m", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_10_When_User_Passes_10() {
        assertEquals(10, parser.parseCommandString("/r 1 1 1 10", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_10_When_User_Passes_d10() {
        assertEquals(10, parser.parseCommandString("/r 1 1 1 d10", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_10_When_User_Passes_e() {
        assertEquals(10, parser.parseCommandString("/r 1 1 1 e", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_12_When_User_Passes_12() {
        assertEquals(12, parser.parseCommandString("/r 1 1 1 12", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_12_When_User_Passes_d12() {
        assertEquals(12, parser.parseCommandString("/r 1 1 1 d12", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }

    @Test
    void FL_Roll_Emits_Artifact_12_When_User_Passes_l() {
        assertEquals(12, parser.parseCommandString("/r 1 1 1 l", null).getForbiddenLandsDice().getTypeOfArtifactDie());
    }
}
