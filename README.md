## Overview 
This is a simple Elevator simulator. Its modelling a simple building that has a number of elevators, floors and persons using
the elevators. It has the following classes: 
 
 Building: 
 
    - List<Floors>
     
    - List<Elevators>
     
    - elevatorController
    
    
 Floor: 
 
    - Set<Person> (persons going up)
    
    - Set<Person> (persons going down)
    
    - elevatorController
 
ElevatorInterface:
 
 Defines necessary functions such that elevators with different strategies can be plug and played. 
 The implementation determines in which order order stops should be performed in. And whether a pick-up together with the corresponding goTo
 is handled isolated from other pickups and goTo's or they are interfering as much as possible. 
  
        
 Elevator implements ElevatorInterface:
  
    - ElevatorState
    
    - Queue<Int> stops going upwards
    
    - Queue<Int> stops going downwards
    
    - List<Set<Persons>> List of set of persons exiting at each floor.                                          
     
    - elevatorController                               
                                                       
    - Stop-strategy: After first pickup, all pickups and goto are allowed in the same direction. The next stop is always the closest stop.
      
    
 ElevatorControllerInterface:
  
  defines the functionality of the controller. It is designed such that all actions are going through the
  controller to an elevator. An interface is necessary, given that different type of controllers can give different strategies on which
  elevator to sent to which pickup.      
 
 ElevatorController implements ElevatorControllerInterface: 
 
    - List<Elevators>
     
    - Queue<Elevators> Idle elevators
    
    - Queue<Elevators> In Use elevators
    
    - Not handled pickups.
    
    - Pickup Strategy: Always give an idle elevator if one is available. Otherwise find the closest one. // (This is a simple strategy and can be optimized in different directions)   
        
 The general workflow is, that a person is created and put on a random floor with a random destination. This triggers an pickup request at the controller for the given floor with a given direction. An elevator is sent
 according to the pickup strategy. Once an elevator arrives at the floor, more persones might have arrived. Everyone who is going the same direction as the first person triggering the
  elevator enters the elvator and "presses a button". The elevator changes its state to empty, once its empty. At every stop, it accepts all ppl going the same direction. There is no capacity 
  limit for an elevator. 
  
  Every Elevator runs in its own thread to simulate real elevators. Every person is added by the main thread. Locks, blocking-queues and "synchronize" is used to handle concurrency.  

## Compile and Run
   At least java jdk 1.7 is required. 
    
   To Compile execute in the terminal: javac ElevatorSimulation.java
   
   When Executing there are several possibilities. Running the program without any arguments, lets it run in automatic mode. A standard configuration with 8 Elevators, 10 floors and 100 persons are used. 
   1. Argument: 
    - auto : Lets the program run in automatic mode. s
    - step : enables step by step execution. If this mode is choosen there are 3 possible commands: p (Add a person), s (Let all elevators execute one action), x (exit program)
    
   2. Argument: 
    - integer : Number of Elevators in the building. 
    
   3. Argument:
    - integer : Number of Floors in the building. 
   
   4. Argument:
    - integer : Number of Persons that should continuously be put in the building.   This option doesn't affect when running in step mode.  
    
    Example: 
    Running the program is straightforward:  
    
    java ElevatorSimulation step 4 12
    