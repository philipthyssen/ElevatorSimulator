import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Philip Thyssen on 8/5/14.
 */
public class Elevator implements ElevatorInterface {

    private final int id;

    private boolean suspended;

    private final boolean steppedApproach;

    private ElevatorState elevatorState;

    private List<Floor> floors;

    private List<Set<Person>> exitFloorPersons;

    // Datastructures that is used by elevator thread and main thread.
    private Queue<Integer> upQueue;
    private Queue<Integer> downQueue;

    private ElevatorControlSystemInterface elevatorControlSystemInterface;

    public Elevator(int id, int topFloor, List<Floor> floors, ElevatorControlSystemInterface elevatorControlSystemInterface) {
        this.id = id;

        this.suspended = false;
        this.steppedApproach = false;

        this.elevatorState = new ElevatorState();
        this.floors = floors;

        this.elevatorControlSystemInterface = elevatorControlSystemInterface;

        this.exitFloorPersons = new LinkedList<Set<Person>>();
        // Initializing exit-set per floor
        for(int i = 0; i < topFloor; i++) {
            this.exitFloorPersons.add(new HashSet<Person>());
        }

        this.upQueue = new PriorityBlockingQueue<Integer>(topFloor);
        this.downQueue = new PriorityBlockingQueue<Integer>(topFloor, Collections.reverseOrder());
    }

    public Elevator(int id, int topFloor, List<Floor> floors, ElevatorControlSystemInterface elevatorControlSystemInterface, boolean steppedApproach) {
        this.id = id;

        if(steppedApproach) {
            this.suspended = true;
        } else {
            this.suspended = false;
        }

        this.steppedApproach = steppedApproach;

        this.elevatorState = new ElevatorState();
        this.floors = floors;

        this.elevatorControlSystemInterface = elevatorControlSystemInterface;

        this.exitFloorPersons = new LinkedList<Set<Person>>();
        // Initializing exit-set per floor
        for(int i = 0; i < topFloor; i++) {
            this.exitFloorPersons.add(new HashSet<Person>());
        }

        this.upQueue = new PriorityBlockingQueue<Integer>(topFloor);
        this.downQueue = new PriorityBlockingQueue<Integer>(topFloor, Collections.reverseOrder());
    }


    /**
     *
     * @return elevator id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return elevatorState
     */
    @Override
    public ElevatorState getElevatorState() {
        return elevatorState;
    }

    public void setSuspended( boolean suspended) {
        this.suspended = suspended;
    }

    @Override
    public void step() {
        suspended = false;
    }

    /**
     * Models elevator button on a floor.
     * @param stopFloor
     * @param direction
     */
    @Override
    public void addStop(int stopFloor, Help.DIRECTIONAL_STATE direction) {
        int goalFloor = elevatorState.getGoalFloor();

        switch (elevatorState.getDirectionalState()) {
            case IDLE:
                elevatorState.setDirectionalState(direction);
                elevatorState.setGoalFloor(stopFloor);
                break;

            case UP:
                if (goalFloor > stopFloor) {
                    if (!upQueue.contains(goalFloor)) {
                        upQueue.add(goalFloor);
                    }
                    elevatorState.setGoalFloor(stopFloor);
                } else {
                    if (!upQueue.contains(stopFloor)) {
                        upQueue.add(stopFloor);
                    }
                }

                break;

            case DOWN:
                if (goalFloor < stopFloor) {
                    if (!downQueue.contains(goalFloor)) {
                        downQueue.add(goalFloor);
                    }
                    elevatorState.setGoalFloor(stopFloor);
                } else {
                    if (!downQueue.contains(stopFloor)) {
                        downQueue.add(stopFloor);
                    }
                }
                break;
        }
    }

    /**
     * this method is only called from within the loop when the lock is already acquired. Therefore,
     * there is no need to lock again.
     * @param person
     * @return
     */
    @Override
    public Boolean goTo(Person person) {
        // Validating input
        int currentFloor = elevatorState.getCurrentFloor();
        Help.DIRECTIONAL_STATE state = elevatorState.getDirectionalState();
        if (currentFloor > person.getGoalFloor() && state == Help.DIRECTIONAL_STATE.UP) {
            System.out.println("Elevator " + id + " is going up. Can not accept destination floor");
            return false;
        } else if (currentFloor < person.getGoalFloor() && state == Help.DIRECTIONAL_STATE.DOWN) {
            System.out.println("Elevator " + id + " is going down. Can not accept destination floor");
            return false;
        }

        // Adding goalFloor of person to queue, in case its not already there.
        if (state == Help.DIRECTIONAL_STATE.UP) {
            if (!upQueue.contains(person.getGoalFloor())) {
                upQueue.add(person.getGoalFloor());
            }
        } else if (state == Help.DIRECTIONAL_STATE.DOWN) {
            if (!downQueue.contains(person.getGoalFloor())) {
                downQueue.add(person.getGoalFloor());
            }
        }

        // Adding person to the set of persons exiting at his goalFloor.
        exitFloorPersons.get(person.getGoalFloor()).add(person);

//        // Visualizing entrance
//        System.out.println("Person " + person.getId() + " enters elevator " + id + " going to floor " + person.getGoalFloor());
        return true;
    }


    @Override
    public void run() {
        // Registering elevator at controller
        elevatorControlSystemInterface.registerElevator(this);

        while (true) {
            // Determining the next action.
            while(suspended) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            switch (elevatorState.getDirectionalState()) {
                case IDLE:
                    // Do nothing.
                    break;
                case UP:
                    // Special case. Go straight to goalFloor without Stopping.
                    if (elevatorState.compareCurrentWithGoal() > 0) {
                        elevatorState.setCurrentToGoal();
                    } else {
                        // Otherwise going one floor up.
                        elevatorState.increaseCurrentFloor();
//                        System.out.println(toString());
                    }

                    if (elevatorState.atGoal()) {
                        System.out.println("Elevator " + id + " is stopping at floor: " + elevatorState.getCurrentFloor());
                        personsExiting();
                        floors.get(elevatorState.getCurrentFloor()).notifyPersonsGoingUp(id);

                        // Checking elevator state. What is going to happen in the next round.
                        if (upQueue.isEmpty()) {
                            setToIdle();
                        } else {
                            elevatorState.setGoalFloor(upQueue.poll());
                            System.out.println("Elevator: " + id + " is going up to floor " + elevatorState.getGoalFloor());
                        }
                    }
                    break;

                case DOWN:
                    // Special case. Go straight to goalFloor without Stopping.
                    if (elevatorState.compareCurrentWithGoal() < 0) {
                        elevatorState.setCurrentToGoal();
                    } else {
                        // Otherwise going one floor down.
                        elevatorState.decreaseCurrentFloor();
//                        System.out.println(toString());
//                        System.out.println("Elevator: " + id + " upQueue: " + Arrays.toString(upQueue.toArray()) + " downQueue: " + Arrays.toString(downQueue.toArray()));
                    }


                    if (elevatorState.atGoal()) {
                        System.out.println("Elevator: " + id + " is stopping at floor: " + elevatorState.getCurrentFloor());
                        personsExiting();
                        floors.get(elevatorState.getCurrentFloor()).notifyPersonGoingDown(id);

                        // Getting next goal
                        if (downQueue.isEmpty()) {
                            setToIdle();
                        } else {
                            elevatorState.setGoalFloor(downQueue.poll());
                            System.out.println("Elevator: " + id + " is going down to floor " + elevatorState.getGoalFloor());
                        }
                    }
                    break;
            }

            // Sleeping
            try {
                Thread.sleep(100);
                if(steppedApproach == true) {
                    suspended = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Letting persons exit elevator.
     */
    private void personsExiting() {
        int currentFloor = elevatorState.getCurrentFloor();
        if(!exitFloorPersons.get(currentFloor).isEmpty()) {
            LinkedList<Person> temp = new LinkedList<Person>(exitFloorPersons.get(currentFloor));
            exitFloorPersons.get(currentFloor).clear();

            for(Person person: temp) {
                System.out.println("Exiting elevator "+person.toString() + ". Reached the its Destination: " + currentFloor);
            }
        }

    }

    private void setToIdle() {
        // Finished all ordered visits. Switching to idle now.
        elevatorState.setDirectionalState(Help.DIRECTIONAL_STATE.IDLE);
        elevatorControlSystemInterface.notifyIdle(this);
        System.out.println("Elevator: " + id + " is now in idle state");

//        for(Floor floor: floors) {
//            System.out.println("Overview persons waiting at each floor: " + floor.toString());
//        }
    }

    @Override
    public String toString() {
        String queueString = null;
        Help.DIRECTIONAL_STATE state = elevatorState.getDirectionalState();
        if(state == Help.DIRECTIONAL_STATE.DOWN) {
            queueString = Arrays.toString(downQueue.toArray());
        } else if( state == Help.DIRECTIONAL_STATE.UP) {
            queueString = Arrays.toString(upQueue.toArray());
        } else {
            queueString = "Empty";
        }
        return "Elevator id: " + id + " currentFloor: " + elevatorState.getCurrentFloor() + " goalFloor: " + elevatorState.getGoalFloor() + " queue: " + queueString;
    }
}
