package solace.script;

public class PassiveNotFoundException extends Exception {
  public PassiveNotFoundException(String name) {
    super(String.format("Passive with name '%s' not found", name));
  }
}
