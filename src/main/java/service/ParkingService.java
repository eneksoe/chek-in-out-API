package service;

import model.Car;

import java.time.LocalDateTime;

public interface ParkingService {

    void register(Car car);

    void out(Car car);

    long getBusySlotCount();

    long getHitInterval(LocalDateTime from, LocalDateTime to);
}
