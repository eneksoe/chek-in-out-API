package exception;

public class CarNotRegisteredException extends IllegalArgumentException {
    public CarNotRegisteredException() {
        super("Car has not registered yet!");
    }
}
