import java.io.Console;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Philip Thyssen on 8/5/14.
 */
public class ElevatorSimulation {
    // Standard Values [0]= NoOfElevators, [1] = Floors, [2] = persons
    static int[] settings = {8, 10, 500};
    static int noOfElevators = 8;
    static int floors = 10;
    static int persons = 100;
    static int sleep = 100;
    static boolean step = false;

    public static void main (String[] args) {


        if (args.length > 0 && args.length < 5) {
            // Automatic mode
            if (args[0].equals("auto")) {
                step = false;
            }

            if (args[0].equals("step")) {
                step = true;
            }

            for (int i = 1; i < args.length; i++) {
                settings[i - 1] = Integer.parseInt(args[i]);
            }

        }

        System.out.println("Running simulation with the following settings - mode: " + (step == false ? "Auto": " man") + " noOfElevators: " +  noOfElevators + " floors: " + floors + " persons: " + persons);

        Building building = new Building(noOfElevators, floors, step);

        Random random = new Random(System.currentTimeMillis());

        if(step) {
            int id = 0;

            String command;
            Console console = System.console();

            Scanner in = new Scanner(System.in);

            while(true) {
                System.out.print("Please enter a command: \n ");
                command = in.nextLine();
                command.toLowerCase();
                if(command.equals("p")) {
                    Person person = makeRandomPerson(id, random);
//                    System.out.println(" Adding another person now: "+ person);
                    building.addPerson(person);
                    id++;
                } else if(command.equals("s")) {
//                    System.out.println("Every elevator makes one step.");
                    building.elevatorControlSystem.step();
                } else if( command.equals("x")) {
                    System.out.println(" Exiting program! Good Bye, hope you had fun :)");
                    System.exit(0);
                } else {
                    System.out.println("The command: " + command + " does not exist. enter p: add person, s: Every elevator makes one action, x: exit program");
                }
            }
        }

        for(int i = 0; i < persons; i++) {
            building.addPerson(makeRandomPerson(i, random));
            try {
                // Sleeping a bit, such that all persons are not requesting the elevators at the same time.
                Thread.sleep(random.nextInt((int) sleep));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All persons have entered the building");
    }

    private static Person makeRandomPerson(int id, Random random) {
        int floor = random.nextInt(floors);
        int goalFloor = random.nextInt(floors);

        while(goalFloor == floor) {
            goalFloor = random.nextInt(floors);
        }


        return new Person(id, floor, goalFloor);
    }
}
