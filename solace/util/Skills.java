package solace.util;

import solace.game.Skill;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

/**
 * Utility class for loading and referencing skills by name.
 * @author Ryan Sandor Richards
 */
public class Skills {
  private static final String SKILLS_DIR = "game/skills/";
  private static final Skills instance = new Skills();

  private final Hashtable<String, Skill> skills = new Hashtable<>();

  /**
   * @return The singleton instance for the skills utility.
   */
  public static Skills getInstance() {
    return instance;
  }

  /**
   * Initializes the skills helper by loading all the skills provided in the
   * game data directory.
   */
  public void reload() throws IOException {
    Log.info("Loading skill");
    skills.clear();
    Files.find(
      Paths.get(SKILLS_DIR),
      Integer.MAX_VALUE,
      (path, attr) -> attr.isRegularFile()
    ).forEach((path) -> {
      String name = path.getFileName().toString();
      try {
        String contents = new String(Files.readAllBytes(path));
        Skill skill = Skill.parseJSON(contents);
        skills.put(skill.getId(), skill);
      } catch (JSONException je) {
        Log.error(String.format("Malformed json in skill %s: %s", name, je.getMessage()));
      } catch (IOException ioe) {
        Log.error(String.format("Unable to load skill: %s", name));
        ioe.printStackTrace();
      }
    });
  }

  /**
   * @param id Id of the skill.
   * @return A clone of the skill with the given id.
   * @throws SkillNotFoundException If there is no skill with the given id.
   */
  public Skill cloneSkill(String id) throws SkillNotFoundException {
    if (!skills.containsKey(id)) {
      throw new SkillNotFoundException(id);
    }
    return skills.get(id).clone();
  }
}
