# DopeSlope – TDDD78 Project

My project for the TDDD78 course (Object-Oriented Programming) at Linköping University. DopeSlope is a skiing game with two game modes, particle effects, and a highscore system.

## Project Structure
```
├── src/se/liu/simjolucul/dopeslope/   # Main source code
│   ├── game/                           # Game loop and components
│   ├── gameobjects/                     # Player, gates, obstacles
│   ├── effects/                          # Snow, tracks, spray particles
│   ├── handlers/                          # Collision, input, spawning
│   ├── highscore/                          # Highscore handling with Gson
│   ├── menu/                                  # Menu screens
│   ├── slopes/                                  # Game mode implementations
│   └── ui/                                        # Custom UI components
├── resources/                                    # Images, config, audio
└── libs/                                            # External libraries (Gson, MigLayout)
```

## How to Run
1. Open the project in IntelliJ IDEA.
2. Ensure the libraries in `libs/` are added as dependencies.
3. Run `Main.java`.

## About the Course
TDDD78 is an object-oriented programming course for Computer Science students at LiU. The project focuses on applying OOP principles in Java, including inheritance, polymorphism, and encapsulation.

[Course page](https://www.ida.liu.se/~TDDD78/index.sv.shtml)  
[Project guidelines](https://www.ida.liu.se/~TDDD78/labs/2026/project/)
