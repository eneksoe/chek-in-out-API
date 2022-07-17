package service;

import model.Car;

import java.time.LocalDateTime;

public class ParkingServiceImpl implements ParkingService{
    @Override
    public void register(Car car) {

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
