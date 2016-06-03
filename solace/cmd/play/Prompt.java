package solace.cmd.play;

import solace.util.*;
import solace.net.*;
import solace.cmd.*;
import java.util.*;

public class Prompt extends PlayCommand {
  public Prompt(solace.game.Character ch) {
    super("prompt", ch);
  }

  public boolean run(Connection c, String []params) {
    character.sendln(params[1]);
    return true;
  }
}
