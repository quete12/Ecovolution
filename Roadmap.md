Ecovolution is meant to be a proof of concept for realisic environment simulations.

# Roadmap

## 2D Environment
### Step 1:
- Simulation of airflowcircles and temperature changes
- Water vaporation from ground tiles
- Simulation of rain
- Implementation of dev tools to display heatmaps for airflow, temperature, pressure etc.

### Step 2:
- Implement chemical reactions
- Introduce plants to the environment (6 CO2 + 6 H2O => 1 C6H12O6 + 6 O2)
- Define plant behavior (maybe neuroevolution or descision tree) - they should be able to react to changes in the CO2 or water level

### Step 3:
- Introduce herbivores to the environment (1 C6H12O6 + 6 O2 => 6 CO2 + 6 H2O)
- Define herbivore behavior (neuroevolution)

### Step 4:
- Same as Step 3 but with carnivores

### Step 5:
- Finetune settings
- Work on UI
- Enjoy watching the Ecovolution happening
- Depending on CPU and GPU usage: Implement more complex chemical processes like: http://biochemical-pathways.com/#/map/1

## Transition

After everything works in a 2d environment the plan is to take it to another dimension.
- Learn C++
- convert project to C++
- use openGL and openCL to improve performance

### To Descide
- usage of an engine for 3D? Unity / Unreal
... To be continued

