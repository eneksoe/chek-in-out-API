package service;

import model.Car;
import model.ParkingEvent;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;

public class ParkingServiceImpl implements ParkingService {

    private final Map<Car, LinkedList<ParkingEvent>> carEvent;
    private final int parkingSlots;
    private final Supplier<LocalDateTime> timeGenerator;

    public ParkingServiceImpl(Map<Car, LinkedList<ParkingEvent>> carEvent,
                              int parkingSlots,
                              Supplier<LocalDateTime> timeGenerator) {
        this.carEvent = carEvent;
        this.parkingSlots = parkingSlots;
        this.timeGenerator = timeGenerator;
    }

    @Override
    public void register(Car car) {

        carEvent.compute(car, (existingCar, parkingEvents) -> {
            var time = timeGenerator.get();
            if (parkingEvents == null || parkingEvents.size() == 0) {
                parkingEvents = new LinkedList<>();
                parkingEvents.add(new ParkingEvent(time));
                return parkingEvents;
            }

            ParkingEvent last = parkingEvents.getLast();
            if(last.isClosed()){
              parkingEvents.add(new ParkingEvent(time));
            }else{
                last.close(time);
            }

            return parkingEvents;
        });
    }

    @Override
    public void out(Car car) {

    }

    @Override
    public long getBusySlotCount() {
        return 0;
    }

    @Override
    public long getHitInterval(LocalDateTime from, LocalDateTime to) {
        return 0;
    }
}
