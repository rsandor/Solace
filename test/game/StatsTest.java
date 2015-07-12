import static org.junit.Assert.assertEquals;
import org.junit.Test;
import solace.game.Stats;

public class StatsTest {
  @Test
  public void majorAbilityCalculation() {
    int majorAbility = Stats.getAbility(1, Stats.AbilityType.MAJOR);
    assertEquals(10, majorAbility);
  }
}
