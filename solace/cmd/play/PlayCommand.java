package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;
import solace.cmd.AbstractCommand;

public abstract class PlayCommand extends AbstractCommand {
    solace.game.Character character;

    public PlayCommand(String name, solace.game.Character ch) {
        super(name);
        character = ch;
    }
}