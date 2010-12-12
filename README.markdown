Planetarium
-----------

A simple simulation of the Solar system using Java's *Graphics2D*.

Running the app
===============

Comes in two modes:

- normal mode, which supports 8-bit alpha
- safe mode, which supports 1-bit alpha

To run the app either use the premade .bat files, or run it as regular jar:

- For the normal mode: `java -jar planetarium.jar`
- For safe mode: `java -jar planetarium.jar -safemode`.

How to use it
=============

The app is controlled using the mouse and the following keys:

- F1 opens the help window
- F2 adds a star
- F3 adds a planet
- F4 adds a satellite

- N switches visibility of names
- O switches visibility of orbits
- R switches visibility of radious lines between children and parents
- D switches debug mode

- PAUSE pauses the app
- ESC quits the app

Notes:

- After pressing an 'add object' key, all already added objects stop their movement and the cursor image changes, indicating that one is about to a new object. Then one should click with the mouse wherever they may like their new object to be or press ESC to cancel the operation
- The sources are a bit outdated compared to the binary release, but, alas, I couldn't find the latest ones
- *Always exit using the ESC key*, or one would have to close the app through the Task manager
