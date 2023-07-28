# JANGL-alien-shooter

This is a game where you defeat waves of aliens.

Waves spawn periodically. The wave size and enemy speed increases exponentially per wave.

![image](https://github.com/AlexanderJCS/JANGL-alien-shooter/assets/98898166/40504161-c7cf-49af-860a-72d5b23ed926)

## Controls

| Key                | Action            |
|--------------------|-------------------|
| W, A, S, D         | Movement controls |
| Click or Space bar | Fire              |
| Esc or P           | Pause/unpause     |
| Tab                | Open Upgrade Shop |


## Tips
1. Constantly be on the move. Enemies will always be moving towards you. You can't let them catch up.
2. When you have enough coins (which you can see on the top left), buy upgrades. Upgrades can help you live much longer.
3. Have fun! You can configure the game to your liking by changing the values in the `settings.ini` file, found at `src/main/resources/settings.ini`.

## Made with JANGL

This project is made with my graphics library, JANGL. See more information [here](https://github.com/AlexanderJCS/JANGL).

## Support

Since macOS does not support OpenGL versions after 4.1, this game is not supported for macOS.

## Compiling

To run the project from the source code, first add the `libs` file as a dependency ([IntelliJ guide here](https://www.jetbrains.com/help/idea/library.html)). Then, run the `Main` class.
