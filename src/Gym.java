import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.HashMap;

import static java.lang.Thread.currentThread;

public class Gym {

    private static int totalGymMembers;
    private Map<MachineType, Integer> availableMachines;

    public Gym(int totalMembers, Map<MachineType,Integer> AvailableMachines){
        this.totalGymMembers = totalMembers;
        this.availableMachines = AvailableMachines;
    }

    public static synchronized void openForTheDay(){

        List<Thread> gymMembersRoutines = IntStream.rangeClosed(1, totalGymMembers).mapToObj((id)-> {
            Member member = new Member(id);
            return new Thread(() ->{
                try{
                    member.performRoutine();
                }catch (Exception e){
                    System.out.println("Exeption catched: " + e);
                }
            });
        }).collect(Collectors.toList());
        Thread supervisor = createSupervisor(gymMembersRoutines);
        supervisor.start();
        gymMembersRoutines.forEach(Thread::start);
    }

    private static synchronized Thread createSupervisor(List<Thread> threads){
        Thread supervisor = new Thread(() -> {
            while (true){
                List<String> runningThreads = threads.stream().filter(Thread::isAlive).map(Thread::getName).collect(Collectors.toList());
                System.out.println(currentThread().getName() + " - " + runningThreads.size() + " members currently exercising: " + runningThreads + "\n");
                if (runningThreads.isEmpty()){
                    break;
                }
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    System.out.println("There is an error: " + e);
                }
            }
            System.out.println(currentThread().getName() + "All the threads have completed");
        });
        supervisor.setName("Gym Staff");
        return supervisor;
    }

     public static void main(String[] args) {

        Gym globoGym = new Gym(5, new HashMap<>() {
            {
                put(MachineType.LEGPRESSMACHINE, 5);
                put(MachineType.BARBELL, 5);
                put(MachineType.SQUATMACHINE, 5);
                put(MachineType.LEGEXTENSIONMACHINE, 5);
                put(MachineType.LEGCURLMACHINE, 5);
                put(MachineType.LATPULLDOWNMACHINE, 5);
                put(MachineType.CABLECROSSOVERMACHINE, 5);
            }
        });

        openForTheDay();
    }


}
