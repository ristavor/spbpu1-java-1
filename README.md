# Artificial Life Simulation

A Java-based artificial life modeling system inspired by Chris Langton's work, featuring neural network-controlled agents competing for survival in a toroidal grid environment.

## Overview

This project simulates an ecosystem where herbivorous and carnivorous agents evolve through neural network-based decision making. Agents sense their local environment, make decisions through simple neural networks, and adapt their behavior through mutation during reproduction.

## Features

### Environment
- **Toroidal Grid**: When agents reach the edge, they appear on the opposite side
- **Dynamic Ecosystem**: Plants, herbivores, and carnivores interact in real-time
- **Configurable Parameters**: Adjustable grid size, population counts, and energy settings

### Agents

Each agent consists of three main components:

#### 1. Sensors
Agents perceive their local environment divided into four areas:
- **Nearness**: The immediately adjacent cell where the agent can act (move or eat)
- **Front**: Two cells ahead in the current direction
- **Left**: One cell to the left
- **Right**: One cell to the right

In each area, sensors detect:
- Number of plants
- Number of herbivores
- Number of carnivores

#### 2. Brain (Neural Network)
- **Architecture**: 14 inputs, 4 outputs (fully connected)
- **Input Features**:
  - Nearness area: plants, herbivores, carnivores (3 inputs)
  - Front area: plants, herbivores, carnivores (3 inputs)
  - Left area: plants, herbivores, carnivores (3 inputs)
  - Right area: plants, herbivores, carnivores (3 inputs)
  - Current energy level (normalized) (1 input)
  - Bias term (1 input)
- **Output Actions** (winner-takes-all):
  - Move forward
  - Turn left
  - Turn right
  - Eat

Formula: `Out(i) = bias(i) + Σ(w(i,j) * u(j))` where i=1,2,3,4 and j=1,2,...,14

#### 3. Actions
Based on neural network output, agents can:
- Move to the next cell in the current direction
- Turn left (90 degrees)
- Turn right (90 degrees)
- Eat a plant (herbivores) or attack prey (carnivores)

### Energy and Metabolism

- **Energy Depletion**: Agents spend 1 energy unit per simulation step
- **Death Condition**: Agent dies when energy reaches 0
- **Food Sources**:
  - **Herbivores**: Gain 10 energy units by eating plants
  - **Carnivores**: Gain 20 energy units by eating herbivores

### Reproduction

- **Threshold**: When agent's energy reaches 90% of maximum capacity
- **Mechanism**: 
  - Parent's energy is split equally with offspring
  - Offspring inherits parent's neural network with small random mutations
  - Child is placed in an adjacent free cell
  - Offspring faces a random direction

### Evolution

- **Competition**: Carnivores evolve strategies to hunt herbivores
- **Survival**: Herbivores evolve strategies to find plants and avoid carnivores
- **Adaptation**: Neural network weights mutate during reproduction, leading to behavioral evolution

## Technical Details

### Implementation
- **Language**: Java
- **GUI Framework**: Swing
- **Architecture**: Single-file implementation (`Main.java`)
- **Neural Network**: Custom implementation with Gaussian initialization and mutation

### Configuration Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| Grid Size | 20×20 | Size of the simulation environment |
| Initial Herbivores | 22 | Starting number of herbivorous agents |
| Initial Carnivores | 5 | Starting number of carnivorous agents |
| Initial Plants | 90 | Starting number of plants |
| Plant Growth Rate | 4.5% | Probability per cell per step |
| Max Energy (Herbivore) | 100 | Maximum energy capacity |
| Max Energy (Carnivore) | 130 | Maximum energy capacity |
| Initial Energy (Herbivore) | 60 | Starting energy |
| Initial Energy (Carnivore) | 90 | Starting energy |

## Usage

### Prerequisites
- Java 17 or higher

### Running the Simulation

```bash
javac src/Main.java
java -cp src Main
```

Or using an IDE (IntelliJ IDEA, Eclipse, etc.):
1. Open the project
2. Run `Main.java`

### Controls

- **Start/Pause**: Begin or pause the simulation
- **Step**: Execute one simulation step
- **Reset**: Restart with new parameters
- **Speed Slider**: Adjust simulation speed

### Configuration

All parameters can be adjusted through the UI:
- Top panel contains spinners for all simulation parameters
- Click "Reset" to apply changes and restart the simulation

## Visualization

- **Grid**: Light gray cells with borders
- **Plants**: Green circles
- **Herbivores**: Blue circles with white directional triangles
- **Carnivores**: Red circles with white directional triangles
- **Energy Display**: Black numbers showing current energy level

## Project Structure

```
spbpu1-java-1/
├── src/
│   └── Main.java          # Complete simulation implementation
├── spbpu1-java-1.iml      # IntelliJ IDEA module file
└── README.md              # This file
```

## Key Classes

### Main
The main application window with UI controls

### Environment
Manages the grid, agents, and simulation logic

### Agent
Represents individual organisms with species, position, direction, energy, and neural network

### NeuralNetwork
Implements the decision-making system with forward propagation and mutation

### Cell
Represents a single grid cell (can contain a plant and/or an agent)

## Simulation Dynamics

1. **Each Step**:
   - Agents are processed in random order
   - Each agent loses 1 energy
   - Agent senses environment (14 inputs)
   - Neural network decides action
   - Action is executed
   - Reproduction check (if energy ≥ 90% max)
   - Plants grow randomly (based on growth rate)

2. **Evolution Process**:
   - Successful agents survive longer and reproduce more
   - Offspring inherit parent's neural network with mutations
   - Over generations, effective hunting/survival strategies emerge

## Observations

With default parameters, you can observe:
- **Population Cycles**: Predator-prey dynamics similar to Lotka-Volterra equations
- **Emergent Behaviors**: Agents develop hunting patterns and evasion strategies
- **Extinction Events**: Poor starting parameters can lead to species extinction
- **Stable States**: Well-balanced populations can sustain for many generations

## License

This project is created for educational purposes as part of SPbPU coursework.

## Author

Created as part of SPbPU (Peter the Great St. Petersburg Polytechnic University) Java programming course.

## References

- Chris Langton - Artificial Life
- Winner-Takes-All Neural Networks
- Evolutionary Algorithms
- Agent-Based Modeling

