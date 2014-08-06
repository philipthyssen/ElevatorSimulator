/**
 *  This is a very simple interface that models what functions
 *  is expected of elevator.
 *
 * Created by Philip Thyssen on 8/5/14.
 */
public interface ElevatorInterface extends Runnable {
    /**
     * Returns the current state of this elevator.
     * This contains current floor, next goal floor and direction.
     * @return
     */
    public ElevatorState getElevatorState();

    /**
     * This will make an elevator execute one action. That is
     *  Move once and execute everything this action brings along.
     */
    public void step();

    /**
     * Adds a stop/pickup to the elevator. Its called
     * stop, because an elevator doesn't differentiate between types of stop.
     * @param stopFloor
     * @param direction
     */
    public void addStop(int stopFloor, Help.DIRECTIONAL_STATE direction);

    /**
     * A person pressing a button. The person container
     * has all necessary functions.
     * @param person
     * @return
     */
    public Boolean goTo(Person person);
}
