package solace.cmd.deprecated;

public abstract class PlayStateCommand extends AbstractStateCommand {
  solace.game.Character character;

  public PlayStateCommand(String name, solace.game.Character ch) {
    super(name);
    character = ch;
  }

  /**
   * @return The character associated with the command.
   */
  public solace.game.Character getCharacter() {
    return character;
  }
}
