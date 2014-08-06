import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Philip Thyssen on 8/5/14.
 */
public class Floor {

    /**
     * Every interaction with the sets personsGoingDown or personsGoingUp is synced.
     * Persons can still be added while the elevators are running.
     */

    private final int id;
    private final Set<Person> personsGoingUp;
    private Set<Person> personsGoingDown;
    private ElevatorControlSystemInterface elevatorControlSystemInterface;

    public Floor(int id, ElevatorControlSystemInterface elevatorControlSystemInterface) {
        this.id = id;
        this.elevatorControlSystemInterface = elevatorControlSystemInterface;
        this.personsGoingDown = new HashSet<Person>();
        this.personsGoingUp = new HashSet<Person>();
    }

    public void addPerson(Person person) {
        if(person.getCurrentFloor() < person.getGoalFloor()) {
            synchronized (personsGoingUp) {
                personsGoingUp.add(person);
                elevatorControlSystemInterface.pickUp(person.getCurrentFloor(), 1);
            }
        } else {
            synchronized (personsGoingDown) {
                personsGoingDown.add(person);
                elevatorControlSystemInterface.pickUp(person.getCurrentFloor(), -1);
            }
        }
    }

    public synchronized void notifyPersonsGoingUp(int elevator) {
        HashSet<Person> enteredElevator = new HashSet<Person>();
        synchronized (personsGoingUp) {
            for (Person person : personsGoingUp) {
                if (elevatorControlSystemInterface.goTo(elevator, person)) {
                    enteredElevator.add(person);
                }
            }
        }

        for(Person person: enteredElevator) {
            personsGoingUp.remove(person);
        }

        // Printing stats.
        if(enteredElevator.size() > 0) {
            String stateEnteredElevator = enteredElevator.size() + " entered the elevator " + elevator + " at floor " + id + " Persons: " + Arrays.deepToString(enteredElevator.toArray());
            System.out.println(stateEnteredElevator);
        }
        enteredElevator = null;
    }

    public synchronized void notifyPersonGoingDown(int elevator) {
        HashSet<Person> enteredElevator = new HashSet<Person>();
        synchronized (personsGoingDown) {
            for (Person person : personsGoingDown) {
                if (elevatorControlSystemInterface.goTo(elevator, person)) {
                    enteredElevator.add(person);
                }
            }
        }

        for(Person person: enteredElevator) {
            personsGoingDown.remove(person);
        }

        if(enteredElevator.size() > 0) {
            System.out.println(enteredElevator.size() + " entered the elevator " + elevator + " at floor " + id + " Persons: " + Arrays.deepToString(enteredElevator.toArray()));
        }
        enteredElevator = null;
    }

    @Override
    public String toString() {
        return "Floor: " + id + " personsGoingUp: " + Arrays.toString(personsGoingUp.toArray()) + " personsGoingDown: " + Arrays.toString(personsGoingDown.toArray());
    }
}
