package solace.script;
import solace.cmd.play.PlayCommand;
import solace.net.Connection;
import solace.game.Character;
import java.util.function.BiPredicate;

public class ScriptedPlayCommand {
  private String name;
  private BiPredicate<Character, String[]> lambda;

  public ScriptedPlayCommand(String name) {
    setName(name);
    setLambda((character, params) -> false);
  }

  public ScriptedPlayCommand(
    String name,
    BiPredicate<Character, String[]> lambda
  ) {
    setName(name);
    setLambda(lambda);
  }

  public String getName() {
    return name;
  }

  public void setName(String n) {
    name = n;
  }

  public BiPredicate<Character, String[]> getLambda() {
    return lambda;
  }

  public void setLambda(BiPredicate<Character, String[]> l) {
    lambda = l;
  }

  public PlayCommand getInstance(Character ch) {
    return new PlayCommand(name, ch) {
      public boolean run(Connection c, String[] params) {
        return lambda.test(ch, params);
      }
    };
  }
}
