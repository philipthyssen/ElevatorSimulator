import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Philip Thyssen on 8/5/14.
 */
public class ElevatorState {
    /**
     * This is a container class for an elevators state.
     * Everything related to locking is handled inside this class.
     *
     * There might
     */
    ReentrantLock lock;
    private Help.DIRECTIONAL_STATE state;
    private int currentFloor;
    private int goalFloor;

    // Standard Values.
    public ElevatorState() {
        this.state = Help.DIRECTIONAL_STATE.IDLE;
        this.currentFloor = 0;
        this.goalFloor = -1;

        // A lock is needed for sync on state.
        this.lock = new ReentrantLock();
    }

    public Help.DIRECTIONAL_STATE getDirectionalState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentFloor() {
        lock.lock();
        try {
            return currentFloor;
        } finally {
            lock.unlock();
        }

    }

    public int getGoalFloor() {
        lock.lock();
        try {
            return goalFloor;
        } finally {
            lock.unlock();
        }
    }

    public void setDirectionalState(Help.DIRECTIONAL_STATE state) {
        lock.lock();
        try {
            this.state = state;
        } finally {
            lock.unlock();
        }
    }

    public void increaseCurrentFloor() {
        lock.lock();
        try {
            currentFloor++;
        } finally {
            lock.unlock();
        }
    }

    public void decreaseCurrentFloor() {
        lock.lock();
        try {
            currentFloor--;
        } finally {
            lock.unlock();
        }
    }

    public void setCurrentFloor(int floor) {
        lock.lock();
        try {
            currentFloor = floor;
        } finally {
            lock.unlock();
        }
    }

    public void setGoalFloor(int floor) {
        lock.lock();
        try {
            goalFloor = floor;
        } finally {
            lock.unlock();
        }
    }

    public int compareCurrentWithGoal() {
        lock.lock();
        try {
            return currentFloor - goalFloor;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Convenience method
     */
    public void setCurrentToGoal() {
        lock.lock();
        try {
            currentFloor = goalFloor;
        } finally {
            lock.unlock();
        }
    }

    public boolean atGoal() {
        lock.lock();
        try {
            return currentFloor == goalFloor;
        } finally {
            lock.unlock();
        }
    }
}
