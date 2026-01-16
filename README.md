# Example Gradle project for Jaylib

You can import this project into IntelliJ or Eclipse.

## Use it to run the included examples in IntelliJ

Right-click on the example and select `run`.

## Use it to run the included examples from the command line

    ./gradlew run -Pmain=examples.HeightMap
    ./gradlew run -Pmain=examples.CubicMap

## Use it as the basis of your own game.

Edit `Main.java` with your own code.  (You can delete the examples.)  Then

## Sharing your game

To package your game for sharing with others:

1.  Run the following command:
    ```powershell
    ./gradlew distZip
    ```
2.  The resulting file will be located at `build/distributions/TanXX-Release.zip`.
3.  You can share this ZIP file. Anyone who receives it just needs to extract it and run:
    ```powershell
    java -jar TanXX.jar
    ```
    *(Note: They will need Java 17 or newer installed on their system.)*

## Running from source
To run the game during development:
```powershell
./gradlew run
```
