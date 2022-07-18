package validation;

import exception.ValidationException;

public class Validator {
    public static<T> T validateNotNull(T obj){
        if(obj == null){
            throw new ValidationException("Car must be not null");
        }
        return obj;

    }
}
