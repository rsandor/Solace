package solace.cmd.deprecated.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import solace.util.*;
import com.google.common.base.Joiner;

/**
 * Lists a character's skills and unlocked abilities therein.
 * @author Ryan Sandor Richards
 */
public class ListSkills extends PlayStateCommand {
  public ListSkills(solace.game.Character ch) {
    super("skills", ch);
  }

  public void run(Connection c, String []params) {
    StringBuffer b = new StringBuffer();
    Collection<Skill> skills = character.getSkills();

    for (Skill s : skills) {
      b.append(String.format(
        "[{y}%d/100{x}] {c}%s{x}\n\r",
        s.getLevel(),
        s.getName()
      ));

      b.append(Strings.toFixedWidth(
        "Passive Enhancements: " + Joiner.on(", ").join(s.getPassives())
      ));
      b.append("\n\r");
      b.append("\n\r");

      b.append(Strings.toFixedWidth(
        "Cooldown Actions: " + Joiner.on(", ").join(s.getCooldowns())
      ));
      b.append("\n\r");
    }

    character.sendln(b.toString());
  }
}
