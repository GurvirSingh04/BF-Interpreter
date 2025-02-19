# Brainf*** Interpreter

This repository contains an implementation of a Brainf*** interpreter written in Scala, offering an exploration into the world of esoteric programming languages. Brainf***, developed by Urban Müller in 1993, is renowned for its extreme minimalism, consisting of only eight commands. Despite its simplicity, it is Turing complete, meaning it is capable of performing any computation, though it comes with significant challenges in both implementation and resource consumption.

## Background

Brainf*** operates with a minimal set of commands, each with a specific function:

- `>`: Move the memory pointer to the right.
- `<`: Move the memory pointer to the left.
- `+`: Increment the value at the current memory cell.
- `-`: Decrement the value at the current memory cell.
- `.`: Output the ASCII character corresponding to the value at the current memory cell.
- `,`: Input a single character and store it in the current memory cell.
- `[`: Jump forward to the matching `]` if the value at the current memory cell is zero.
- `]`: Jump back to the matching `[` if the value at the current memory cell is nonzero.

All other characters are ignored and considered as comments.

The interpreter uses a memory tape of cells, each storing an integer. The memory pointer (`mp`) points to the current cell, and the commands manipulate the pointer or modify the contents of the memory cell. 

## Features

- Supports all standard Brainf*** commands.
- Memory is dynamically allocated as needed.
- Capable of running and interpreting complex Brainf*** programs, including those that generate visual patterns or mathematical representations.

## Examples

Explore sophisticated Brainf*** programs included in this repository. Programs like the generation of the **Sierpinski triangle** and **Mandelbrot set** demonstrate Brainf***'s potential for creating complex visual patterns and solving mathematical problems. You can also find other fun examples, such as the **Collatz conjecture**.

Here are a few examples to try out:

```scala
run4(load_bff("benchmark.bf"))
run4(load_bff("sierpinski.bf"))
run4(load_bff("mandelbrot.bf"))
run4(load_bff("collatz.bf"))
```

To measure the time it takes to run each program, use the following commands:

```scala
time_needed(1, run4(load_bff("benchmark.bf")))
time_needed(1, run4(load_bff("sierpinski.bf")))
time_needed(1, run4(load_bff("mandelbrot.bf")))
time_needed(1, run4(load_bff("collatz.bf")))
```

## Usage

To run Brainf*** programs with this interpreter, ensure that your program is written in the Brainf*** language and saved with a `.bf` extension. Load and run the program with the following function:

```scala
run4(load_bff("your_program.bf"))
```

This will interpret and execute the Brainf*** code, outputting the results accordingly.

## Conclusion

Despite its minimalistic nature, Brainf*** offers a fascinating challenge for programmers interested in esoteric languages. This interpreter serves as a tool to explore Brainf***'s potential, allowing you to run complex programs, generate visual outputs, and dive deeper into this unique language.
