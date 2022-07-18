package service;

import exception.CarAlreadyRegisteredException;
import exception.CarNotRegisteredException;
import model.Car;
import model.ParkingEvent;
import validation.Validator;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;

public class ParkingServiceImpl implements ParkingService {

    private final Map<Car, LinkedList<ParkingEvent>> carEvent;
    private final int parkingSlots; // TODO do not forget to check if more than available slots
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
        Validator.validateNotNull(car);

        carEvent.compute(car, (existingCar, parkingEvents) -> {
            var time = timeGenerator.get();
            if (parkingEvents == null) {
                parkingEvents = new LinkedList<>();
                parkingEvents.add(new ParkingEvent(time));
                return parkingEvents;
            }

            ParkingEvent last = parkingEvents.getLast();

            if (last.isClosed()) {
                parkingEvents.add(new ParkingEvent(time));
            } else {
                throw new CarAlreadyRegisteredException();
            }

            return parkingEvents;
        });
    }

    @Override
    public void out(Car car) {
        Validator.validateNotNull(car);

        carEvent.compute(car, (existingCar, parkingEvents) -> {
            var time = timeGenerator.get();
            if (parkingEvents == null) {
                throw new CarNotRegisteredException();
            }

            ParkingEvent last = parkingEvents.getLast();

            if (!last.isClosed()) {
                last.close(time);
            } else {
                throw new CarNotRegisteredException();
            }

            return parkingEvents;
        });
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
