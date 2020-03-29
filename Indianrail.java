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

