import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philip Thyssen on 8/5/14.
 */
public class Building {
   List<Floor> floors;
   List<ElevatorInterface> elevators;
   ElevatorControlSystemInterface elevatorControlSystem;

    public Building(int noOfElevators, int noOfFloors, boolean steppedApproach) {
        elevators = new ArrayList<ElevatorInterface>(noOfElevators);
        floors = new ArrayList<Floor>(noOfFloors);
        elevatorControlSystem = new ElevatorControlSystem(elevators);

        // Initializing elevators
        for(int i = 0; i < noOfElevators; i++) {
            elevators.add(new Elevator(i, noOfFloors, floors, elevatorControlSystem, steppedApproach));
        }


        // Initializing floors
        for(int i = 0; i < noOfFloors; i++) {
            floors.add(new Floor(i, elevatorControlSystem));
        }

        // Starting elevator threads
        for(ElevatorInterface elevator: elevators) {
            (new Thread(elevator)).start();
        }
    }

    /**
     * Putting a person on a floor with specified use of elevator.
     * @param person
     */
    public void addPerson(Person person) {
        // Person is waiting at the floor where its currently residing.
        System.out.println("Another person is being thrown in to the building: " + person.toString());
        floors.get(person.getCurrentFloor()).addPerson(person);
    }
}
