package service;

import exception.CarAlreadyRegisteredException;
import model.Car;
import model.ParkingEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

class ParkingServiceTest {

    private static final String NUMBER = "ABC123";
    private static final Integer PARKING_SLOTS = 5;
    private ParkingService parkingService;
    private final LocalDateTime closeTime = LocalDateTime.of(
            2000, 2, 2, 2, 2, 2, 2);
    private Supplier<LocalDateTime> timeGenerator = () -> LocalDateTime.of(
            2000, 1, 1, 1, 1, 1, 1);


    @Test
    void registerCarTest() {
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
    void rejectSameCarTest() {
        var car = new Car(NUMBER);
        var map = new HashMap<Car, LinkedList<ParkingEvent>>();
        var events = new LinkedList<ParkingEvent>();
        events.add(new ParkingEvent(timeGenerator.get()));
        map.put(car, events);

        parkingService = new ParkingServiceImpl(map, PARKING_SLOTS, timeGenerator);

        assertThatExceptionOfType(CarAlreadyRegisteredException.class)
                .isThrownBy(() -> parkingService.register(car))
                .withMessage("Car is already registered!");
    }

    private LinkedList<ParkingEvent> getExpectedEvents() {
        LinkedList<ParkingEvent> parkingEvents = new LinkedList<>();
        ParkingEvent expectedParkingEvent = new ParkingEvent(timeGenerator.get());
        parkingEvents.add(expectedParkingEvent);
        return parkingEvents;
    }

    @Test
    void closeParkingEventTest() {
        var car = new Car(NUMBER);
        var map = getSourceCarEvent(car);
        var expectedEvents = expectedEventAfterClose();
        parkingService = new ParkingServiceImpl(map, PARKING_SLOTS, timeGenerator);

        parkingService.out(car);

        assertThat(map).containsExactly(
                entry(car, expectedEvents)
        );
    }

    private LinkedList<ParkingEvent> expectedEventAfterClose() {
        ParkingEvent expectedEvent = new ParkingEvent(timeGenerator.get());
        expectedEvent.close(closeTime);
        timeGenerator = () -> closeTime;
        LinkedList<ParkingEvent> parkingEvents = new LinkedList<>();
        parkingEvents.add(expectedEvent);
        return parkingEvents;
    }

    private HashMap<Car, LinkedList<ParkingEvent>> getSourceCarEvent(Car car) {
        HashMap<Car, LinkedList<ParkingEvent>> map = new HashMap<>();
        var events = new LinkedList<ParkingEvent>();
        ParkingEvent enterEvent = new ParkingEvent(LocalDateTime.of(
                2000, 1, 1, 1, 1, 1, 1));
        events.add(new ParkingEvent(timeGenerator.get()));
        map.put(car, events);
        return map;
    }

    @Test
    void busySlotsCountTest() {
        long expectedBusySlots = 2;
        var map = getCarEventsWithThreeEvents();
        timeGenerator = () -> LocalDateTime.of(
                2000,1,1,1,1,1,3);
        parkingService = new ParkingServiceImpl(map, 10, timeGenerator);
        long busySlotsCount = parkingService.getBusySlotCount();

        assertThat(busySlotsCount).isEqualTo(expectedBusySlots);
    }

    private HashMap<Car, LinkedList<ParkingEvent>> getCarEventsWithThreeEvents() {
        HashMap<Car, LinkedList<ParkingEvent>> carEvents = new HashMap<>();
        for (int i = 1; i < 4; i++) {
            var car = new Car(String.valueOf(i));
            var enterTime = LocalDateTime.of(
                    2000, 1, 1, 1, 1, 1, i);
            var event = new ParkingEvent(enterTime);
            event.close(enterTime.plusNanos(1));
            var secondEvent = new ParkingEvent(enterTime.plusNanos(2));
            var events = new LinkedList<ParkingEvent>();
            events.add(event);
            events.add(secondEvent);
            carEvents.put(car,events);
        }
        return carEvents;
    }

    @Test
    void getHitIntervalTest() {

    }
}