@name(inspect)
@admin
@author(Ryan Sandor Richards)

# Admin Command: Inspect
Usage: `inspect` [name]

The inspect command will attempt to find full game engine information regarding
a room, item, mobile, or character in the game and display it in an easy-to-read
format to the admin.

When run without the [name] argument then the inspect data for the admin's
current room is displayed. Otherwise it first searches for an item in the room,
then looks for mobiles or characters in the room, and finally searches the
inventory of the admin for an object with the given name.
