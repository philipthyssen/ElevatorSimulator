import java.util.List;

/**
 * This interface is very simple. It has 5 functions.
 * Each described below
 * Created by Philip Thyssen on 8/5/14.
 */
public interface ElevatorControlSystemInterface {

    /**
     *
     * @return List of all elevators from where there status can be obtained.
     */
    public List<ElevatorInterface> getStatus();

    /**
     * Called by a person requesting an elevator.
     * This method is expected to find the best suitable elevator.
     * Note, the best elevator depends on different metrics.
     * @param floor
     * @param direction
     */
    public void pickUp(int floor, int direction);

    /**
     * Is called by a person that is inside an elevator.
     * This corresponds to pressing an button in an elevator.
     * @param elevator
     * @param person
     * @return true if request can be handled otherwise false.
     */
    public boolean goTo(int elevator, Person person);

    /**
     * Causes all elevators to execute one action.
     */
    public void step();

    /**
     * Called by an elevator when it reaches a state, that is Idle.
     * That is, there are no more "move orders".
     * @param elevator
     */
    public void notifyIdle(ElevatorInterface elevator);

    /**
     * Called when an elevator comes online.
     * @param elevator
     */
    public void registerElevator(Elevator elevator);
}
