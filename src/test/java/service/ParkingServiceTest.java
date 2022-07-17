package service;

import exception.CarAlreadyRegisteredException;
import model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

class ParkingServiceTest {

    private static final String NUMBER = "ABC123";

    private ParkingService parkingService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void registerCar() {
        var car = new Car(NUMBER);

        parkingService.register(car);

        assertThatNoException().isThrownBy(() -> parkingService.register(car));
    }

    @Test
    void rejectSameCar(){
    var car = new Car(NUMBER);
    assertThatExceptionOfType(CarAlreadyRegisteredException.class)
            .isThrownBy(()->parkingService.register(car))
            .withMessage("Car Already Registered!");
    }
}