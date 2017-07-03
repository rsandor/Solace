package solace.util;

import solace.game.Skill;
import java.io.*;
import java.util.*;
import org.json.*;

/**
 * Utility class for loading and referencing skills by name.
 * @author Ryan Sandor Richards
 */
public class Skills {
  /**
   * Path to the skills directory.
   */
  protected static final String SKILLS_DIR = "data/skills/";

  /**
   * Maps skill ids to skills.
   */
  protected static Hashtable<String, Skill> skills;

  /**
   * Initializes the skills helper by loading all the skills provided in the
   * game data directory.
   */
  public static void initialize() {
    File dir = new File(SKILLS_DIR);
    String[] names = dir.list();
    skills = new Hashtable<String, Skill>();

    Log.info("Loading skills");

    for (String name : names) {
      try {
        Log.trace("Loading skill " + name);
        String path = SKILLS_DIR + name;
        StringBuffer contents = new StringBuffer("");
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = in.readLine();
        while (line != null) {
          contents.append(line + "\n");
          line = in.readLine();
        }
        Skill skill = Skill.parseJSON(contents.toString());
        skills.put(skill.getId(), skill);
      }
      catch (JSONException je) {
        Log.error(String.format(
          "Malformed json in skill %s: %s", je.getMessage()
        ));
      }
      catch (IOException ioe) {
        Log.error("Unable to load skill: " + name);
        ioe.printStackTrace();
      }
    }
  }

  /**
   * @param id Id of the skill.
   * @return The skill with the given id.
   * @throws SkillNotFoundException If there is no skill with the given id.
   */
  public static Skill getSkill(String id) throws SkillNotFoundException {
    if (!skills.containsKey(id)) {
      throw new SkillNotFoundException(id);
    }
    return skills.get(id);
  }

  /**
   * @param id Id of the skill.
   * @return A clone of the skill with the given id.
   * @throws SkillNotFoundException If there is no skill with the given id.
   */
  public static Skill cloneSkill(String id) throws SkillNotFoundException {
    return getSkill(id).clone();
  }
}
