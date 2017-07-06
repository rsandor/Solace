package solace.cmd.play;

import solace.cmd.AbstractStateCommand;

public abstract class PlayCommand extends AbstractStateCommand {
  solace.game.Character character;

  public PlayCommand(String name, solace.game.Character ch) {
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
