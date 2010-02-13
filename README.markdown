Solace MUD Engine
================================================================================

By Ryan Sandor Richards

Introduction
--------------------------------------------------------------------------------

In 1998 two things happened... well more than two things, but two things 
happened to me, anyway. The first was that I entered High-school as a doe-eyed
freshman, and the second was that I discovered the wonderful world of Multi-user
Dungeons (MUDs).

Shortly after discovering MUDs I knew I wanted to make one. So I set out on a
short but educational journey and convinced my school's systems administrator
to allow me to learn linux and run a MUD on our school server. This did not 
last very long and I was set back a bit. When I finally got "okay" at linux I 
figured out how to install it at home and got a simple Rom 2.4 derivative 
running on my home machine.

But it was high-school, and like other folks I got caught up in all the 
hormones, band-practices, soccer-games, and new projects like one is supposed
to. My MUD was not to be, well not to be any time soon...

Before college I decided to pick up Java so I'd be prepared for my classes and
every time I used the wonderful language I found myself wanting to go back to
my old high school dream of building a MUD. I learned other languages and
build many other projects. But I didn't really give making a MUD a good go.

Ten years later in 2008 I came to Boston for interviews, to keep myself sharp
with java I decided to start a simple project on my aged and wise Powerbook G4.
When I couldn't immediately come up with something clever to code I thought 
back long and hard (but hardly long). Within a moment it came to me: "Damn it, 
Ryan!" my inner voice exclaimed "It's about time you started working on that 
MUD!" And thus was born the Solace MUD Engine. A dream from ten years ago, now 
packed with experienced goodness!

The project is still not very well developed but a lot of the key features are
in place and come heaven or high-water I intend to at least get a single server
running the game up before I am dust.

So check it out, use it, play with it (it doesn't do too much), extend it,
start with it, scrap it, rewrite it, or ignore it. I don't mind to much but I
felt like it was worth getting out into the interwebs! The project is a labor
of love and I took great care while I was coding it on those cold November days
in Somerville.

  
The Original Mission Statement
--------------------------------------------------------------------------------
The intent of this project is two fold: create a MUD code base, and do it in
the most elegant way possible. This means I am not only creating a "game 
engine", but an elegant and nice game engine. I should try to use as much of the 
object oriented paradigm to make it easy to extend the game and add 
functionality.

The problem with older code bases is that they were a hard to mutate. If you
wanted to add/change ANSI color code, for instance, you had to change about six
different files in three places each. This made being a MUD coder quite a pain
in the keester.

Also most of the older code bases are in C/C++, which is like a double whammy.
Instead of focusing of improving game play, may coders spend their time tracking
down seg faults, bus errors, and a slew of other oddities that come from using
a system level programming language.

Also, in recent years, universities have began to teach Java more prevalently
than they do C/C++. This means that the current and next generation of would-be
MUD coders are probably not going to be too familiar with C. We need a code base
that is up to snuff and will provide a rich framework upon which modern coders
can create newer and better MUDs.

By utilizing Java and sound OOD principals, along with newer technologies such
as XML and MySQL/Hibernate it is my hope that Solice will be a code base that 
brings MUD programming out of the "dark ages" and into the modern coding world.

Ryan Sandor Richards
Friday November 28th, 2008
Somerville, MA

Requirements
--------------------------------------------------------------------------------

Solace requires two things to compile and run:

1. Java Runtime Environment and Compiling tools (version >= 1.4.2)
2. Apache Ant

If you are running a newer version of Mac OSX you're in luck because both of 
these requirements are already met, if you aren't then you can download java
at http://java.sun.com/, and download ant at http://ant.apache.org/

Project Directory Structure
--------------------------------------------------------------------------------
So all those fancy things I mention in the mission statement aren't quite done
yet. For the time being I am using flat files to store user accounts and game
messages.

Here is a list of the directories and files in the root of the repository and
what they all contain/do/r4:

* Accounts - holds user account flat files
* Messages - holds static game messages such as the MOTD
* README - This file
* README.markdown - A copy of this file
* build.xml - The ant build script
* solace - Root source directory

Compiling and Running
--------------------------------------------------------------------------------

From the root directory of the repository type `ant run`. This will run the
ant build script (build.xml), compile the java source files, package them into
a jar file (build/jar/Solace.jar) and execute the jar.

Easy peasy! Here are the 4 major build targets defined in the build.xml file:

* clean - Cleans all build files including the jar
* compile - Compiles all java source files
* jar - Builds the jar file
* run - Executes the game server

By default the server beings running on port 4000, you can pass a custom port
number by executing the following command:

	java -jar build/jar/Solace.jar <your port>
	
Please note that the port must be all numeric and it is suggested that you use
a port greater than 2000 to avoid conflicts with standard system ports.

To connect and "play" the game use any mud client or even telnet. To use telnet
just type `telnet localhost 4000` and you'll connect to the game.

License and Legal Beagle Speak
--------------------------------------------------------------------------------

Copyright (c) 2008-2010 Ryan Sandor Richards

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.