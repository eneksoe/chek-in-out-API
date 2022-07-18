package service;

import exception.CarAlreadyRegisteredException;
import model.Car;
import model.ParkingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

class ParkingServiceTest {

    private static final String NUMBER = "ABC123";
    private static final Integer PARKING_SLOTS = 5;
    private ParkingService parkingService;
    private Supplier<LocalDateTime> timeGenerator = () -> LocalDateTime.of(
            2000,1,1,1,1,1,1);

    @BeforeEach
    void setUp() {
    }

    @Test
    void registerCar() {
        var car = new Car(NUMBER);
        var map = new HashMap<Car, LinkedList<ParkingEvent>>();
        var expectedList = getExpectedEvents();

        parkingService = new ParkingServiceImpl(map, PARKING_SLOTS, timeGenerator);

        assertThatNoException().isThrownBy(() -> parkingService.register(car));
        assertThat(map).containsExactly(
                entry(car, expectedList)
        );
    }

    @Test
    void rejectSameCar() {
        var car = new Car(NUMBER);
        var map = new HashMap<Car, LinkedList<ParkingEvent>>();
        var events = new LinkedList<ParkingEvent>();
        events.add(new ParkingEvent(timeGenerator.get()));
        map.put(car, events);

        parkingService = new ParkingServiceImpl(map, PARKING_SLOTS, timeGenerator);

        assertThatExceptionOfType(CarAlreadyRegisteredException.class)
                .isThrownBy(() -> parkingService.register(car))
                .withMessage("Car Already Registered!");
    }

    private LinkedList<ParkingEvent> getExpectedEvents() {
        LinkedList<ParkingEvent> parkingEvents = new LinkedList<>();
        ParkingEvent expectedParkingEvent = new ParkingEvent(timeGenerator.get());
        parkingEvents.add(expectedParkingEvent);
        return parkingEvents;
    }
}