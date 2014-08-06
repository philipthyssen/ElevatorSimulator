import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Philip Thyssen on 8/5/14.
 */
public class ElevatorControlSystem implements ElevatorControlSystemInterface {
    private List<ElevatorInterface> elevators;

    private LinkedBlockingQueue<ElevatorInterface> idleElevators;
    private LinkedBlockingQueue<ElevatorInterface> inUseElevators;

    private Queue<PickUp> notHandledPickUps;

    public ElevatorControlSystem(List<ElevatorInterface> elevators) {
        this.elevators = elevators;
        this.idleElevators = new LinkedBlockingQueue<ElevatorInterface>();
        this.inUseElevators = new LinkedBlockingQueue<ElevatorInterface>();

        notHandledPickUps = new LinkedList<PickUp>();
    }

    @Override
    public List<ElevatorInterface> getStatus() {
        return elevators;
    }

    @Override
    public void pickUp(int floor, int direction) {
        /**
         * Strategy: The controller always takes the an idle elevator.
         * If all elevators are in use. The controller assigns an elevator that is going in the same direction
         * and is closest.
         */
        ElevatorInterface servicingElevator = null;
        Help.DIRECTIONAL_STATE enumDirection = direction > 0 ? Help.DIRECTIONAL_STATE.UP : Help.DIRECTIONAL_STATE.DOWN;

            if (!idleElevators.isEmpty()) {

                // Getting closest elevator of all idle elevators.
                for (ElevatorInterface elevator : idleElevators) {
                    if (servicingElevator == null) {
                        servicingElevator = elevator;
                    } else {
                        if (Math.abs(floor - servicingElevator.getElevatorState().getCurrentFloor()) > Math.abs(floor - elevator.getElevatorState().getCurrentFloor())) {
                            servicingElevator = elevator;
                        }
                    }
                }

                idleElevators.remove(servicingElevator);
                inUseElevators.add(servicingElevator);
            } else {
                // Looking for suitable elevator.
                for (ElevatorInterface elevator : inUseElevators) {

                        if (elevator.getElevatorState().getDirectionalState() == enumDirection) {
                            servicingElevator = getClosestElevator(floor, servicingElevator, elevator, enumDirection);
                        }

                }
            }

            // There is a suitable elevator
            if (servicingElevator != null) {
                servicingElevator.addStop(floor, enumDirection);
            } else {
                // Save request for later.
                notHandledPickUps.add(new PickUp(floor, direction));
            }
    }

    /**
     * Return which of the two suggested elevators is closest.
     * @param goalFloor
     * @param closestElevator
     * @param elevator
     * @param direction
     * @return
     */
    private ElevatorInterface getClosestElevator(int goalFloor, ElevatorInterface closestElevator, ElevatorInterface elevator, Help.DIRECTIONAL_STATE direction) {
        if (direction == Help.DIRECTIONAL_STATE.UP) {
            if (elevator.getElevatorState().getCurrentFloor() < goalFloor) {
                if (closestElevator == null) {
                    return elevator;
                } else {
                    if (goalFloor - closestElevator.getElevatorState().getCurrentFloor() > goalFloor - elevator.getElevatorState().getCurrentFloor()) {
                        return elevator;
                    }
                }
            }
        } else if (direction == Help.DIRECTIONAL_STATE.DOWN) {
            if (elevator.getElevatorState().getCurrentFloor() < goalFloor) {
                if (closestElevator == null) {
                    return elevator;
                } else {
                    if (goalFloor - closestElevator.getElevatorState().getCurrentFloor() > goalFloor - elevator.getElevatorState().getCurrentFloor()) {
                        return elevator;
                    }
                }
            }

        } else {
            System.out.println(" Should never come in this situation.");
        }
        return closestElevator;
    }


    @Override
    public boolean goTo(int elevator, Person person) {
        return elevators.get(elevator).goTo(person);
    }

    @Override
    public void step() {
        // Is taking one step with all elevators.
        for(ElevatorInterface elevator: elevators) {
            elevator.step();
            System.out.println(elevator.toString());
        }
    }

    @Override
    public void notifyIdle(ElevatorInterface elevator) {
        inUseElevators.remove(elevator);
        idleElevators.add(elevator);


        if(!notHandledPickUps.isEmpty()) {
            System.out.println(" Not all pickups are handled yet: " + Arrays.toString(notHandledPickUps.toArray()));
            PickUp pickUp = notHandledPickUps.poll();
            if(pickUp != null) {
                pickUp(pickUp.floor, pickUp.direction);
            }
        }
    }

    @Override
    public void registerElevator(Elevator elevator) {
        if(elevators.contains(elevator)) {
            System.out.println(" Elevator " + elevator.getId() + " came online and is now ready to use.");
            idleElevators.add(elevator);
        }
    }

    private class PickUp {
        int floor;
        int direction;

        public PickUp(int floor, int direction) {
            this.floor = floor;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "Pick-Up floor: " + floor + " direction: " + (direction > 0 ? "UP": "DOWN");
        }
    }
}
