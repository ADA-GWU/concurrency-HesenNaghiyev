# ImageProcessing in Multi-Single Modes

l used Java for this project.

## Installation

JDK installation is required for compiling and running the project


## Commands for compiling project
```bash
javac ImageProcessor.java
```
This ImageProcessor class contains the entry point(main method) for running the project. l have two other classes(Parallel and CustomException) as well, but compiling the main class(ImageProcessor) will compile and generate the required bytecode for all of the classes.

## Commands for running project

```bash
java ImageProcessor [imagePath] [squareSize] [Mode]
```

For Mode, if the user enters "S", it will execute the application in Single-Threaded Mode. Any other parameters except "S" will be considered as Multi-Threaded mode.

## For example
```bash
java ImageProcessor test.jpeg 25 S
```
## Logic of program(How it works)
In Single threaded mode, it will go from left to right by square size, in MultiThreaded Mode, it will divide the image according to the available processor of the computer, and each thread will start from its part from left to right.
