import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Indianrail{
    static int totalNumberOfPassenger;
    static int noOfFreeSeatsInTheTrain;
    static class Station {
        int passengersAtTheStation;
        int passengersInTrain;
        int totalBoarderPassengers;
        final Lock lock;
        final Condition passengerSeatedCondition;
        Station(Lock lock,Condition passengerSeatedCondition) {
            passengersAtTheStation = 0;
            totalBoarderPassengers = 0;
            passengersInTrain = 0;
            this.lock = lock;
            this.passengerSeatedCondition = passengerSeatedCondition;
        }
    }
    static class Train extends Thread {
        Station s;
        Train(Station s) {
            this.s = s;
        }
        public void run() {
            try {
                station_load_train(s,noOfFreeSeatsInTheTrain);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
     static class  Passenger extends Thread {
        Station s;
        Passenger(Station s) {
            this.s = s;
        }
        public void run() {
            try {
                station_wait_for_train(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static void station_wait_for_train(Station station) throws InterruptedException {
        station.lock.lock();
        station.passengersAtTheStation++;
        //System.out.println(station.passengersAtTheStation);
        synchronized (station.lock) {
            station.lock.wait(1);
        }
        station.lock.unlock();
    }
    static void station_load_train(Station station,int count) throws InterruptedException {
        station.lock.lock();
        while((count >0) && (station.passengersAtTheStation > 0))
        {
            station.passengersInTrain++;
           
            count--;
        }
        synchronized (station.passengerSeatedCondition) {
            station.passengerSeatedCondition.wait(1);
        }
        station.lock.unlock();
    }

    static void station_on_board(Station station ) {
        station.lock.lock();
        while(station.passengersInTrain >0 && station.totalBoarderPassengers < totalNumberOfPassenger)
        {
        	
            station.passengerSeatedCondition.signal();
            station.passengersInTrain--;
            System.out.println("|\tPassenger Seated");
            station.totalBoarderPassengers++;
            
        }
        station.passengerSeatedCondition.signalAll();
        station.lock.unlock();
    }
    public static void main(String[] args) throws InterruptedException, IOException {
        Lock lock = new ReentrantLock();
        Condition passengerSeatedCondition = lock.newCondition();
        Station s = new Station(lock,passengerSeatedCondition);
        Scanner scanner = new Scanner(System.in);
        try {
        	System.out.println(" __________INDIAN RAIL__________");
        	System.out.println("|                                                                         |");
        	System.out.print("|\tENTER THE NUMBER OF PASSENGERS AT THE STATION : ");
        	totalNumberOfPassenger = scanner.nextInt();
        	System.out.println("|_________________________|");
        	if(totalNumberOfPassenger<0) {
        		throw new NegativeArraySizeException();
        	}
        }
        catch(NegativeArraySizeException e) {
        	System.out.println("Error : value entered is not feasible as passengers cannot be negative");
        	System.exit(1);
        }
        int i = 1;
        System.out.println();
        while (noOfFreeSeatsInTheTrain != -1) {
            int totalPassengers = totalNumberOfPassenger;

            System.out.printf(" ___________ TRAIN %d ____________\n",i);
            System.out.println("|                                                                         |");
            System.out.println("|\tTHE NO OF WAITING PASSENGERS AT THE STATION : "+ (totalPassengers-s.totalBoarderPassengers));
            try{
                System.out.print("|\tENTER TOTAL NUMBER OF FREE SEATS IN THIS TRAIN : ");
                noOfFreeSeatsInTheTrain = scanner.nextInt();
                if(noOfFreeSeatsInTheTrain<0){
                    throw new Exception();
                }
               }
            catch(Exception e){
                System.out.println("              Error : free seats cannot be negative");
                System.exit(1);
            } 
            if (noOfFreeSeatsInTheTrain == 0) {
                System.out.printf("|\tTOTAL PASSENGERS BOARDED : %d\n", s.totalBoarderPassengers);
                System.out.printf("|\tPASSENGERS LEFT IN THE STATION : %d\n",(totalPassengers - s.totalBoarderPassengers));
                System.out.println("|_________________________|");
                break;
            
            }
            Passenger[] thread = new Passenger[totalNumberOfPassenger];
            for (int j = 0; j <totalPassengers ; j++) {
                thread[j] = new Passenger(s);
                thread[j].start();
            }
            System.out.printf("|\tTRAIN ARRIVING AT THE STATION WITH %d FREE SEATS\n", noOfFreeSeatsInTheTrain);
            Train newTrain = new Train(s);
            newTrain.start();
            station_load_train(s,noOfFreeSeatsInTheTrain);
            station_on_board(s);
            System.out.printf("|\tTOTAL PASSENGERS BOARDED : %d\n", s.totalBoarderPassengers);
            System.out.printf("|\tPASSENGERS LEFT IN THE STATION : %d\n",totalPassengers - s.totalBoarderPassengers);
            System.out.println("|_________________________|");
            i++;
            s.passengersAtTheStation = totalPassengers - s.totalBoarderPassengers;
            s.passengersInTrain = 0;
            System.out.println();
            if (totalPassengers-s.totalBoarderPassengers == 0) {
                System.out.println("ALL PASSENGERS BOARDED");
                System.exit(0);
            }
        }
    }
}


