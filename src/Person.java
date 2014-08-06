/**
 * Created by Philip Thyssen on 8/5/14.
 */
public class Person {
    private final int id;
    private int currentFloor;
    private int goalFloor;

    public Person(int id, int currentFloor, int goalFloor) {
        this.id = id;
        this.currentFloor = currentFloor;
        this.goalFloor = goalFloor;
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getGoalFloor() {
        return goalFloor;
    }

    @Override
    public String toString() {
        return "Person id: " + id + " initialFloor: " + currentFloor + " goalFloor: " + goalFloor;
    }
}
