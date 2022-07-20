package service;

import exception.CarAlreadyRegisteredException;
import exception.CarNotRegisteredException;
import model.Car;
import model.ParkingEvent;
import validation.Validator;

import java.time.LocalDateTime;
import java.util.Collection;
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
        return carEvent.entrySet().stream()
                .map(Map.Entry::getValue)
                .map(LinkedList::getLast)
                .filter(this::beforeNowAndNotClosed)
                .count();
    }

    private boolean beforeNowAndNotClosed(ParkingEvent event) {
        LocalDateTime now = timeGenerator.get();
        LocalDateTime enterTime = event.getEnterTime();
        return now.isBefore(enterTime) && !event.isClosed();
    }

    @Override
    public long getHitInterval(LocalDateTime from, LocalDateTime to) {
var time = timeGenerator.get();
        return carEvent.entrySet().stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .map(ParkingEvent::getEnterTime)
                .filter(enterTime-> isBeforeInclusive(to, enterTime) && isAfterInclusive(from, enterTime))
                .count();
    }

    private boolean isAfterInclusive(LocalDateTime from, LocalDateTime enterTime) {
        return enterTime.compareTo(from) >= 0;
    }

    private boolean isBeforeInclusive(LocalDateTime to, LocalDateTime enterTime) {
        return enterTime.compareTo(to) <= 0;
    }
}
