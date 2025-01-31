package at.technikum.mtcg.dal;

public class DataAccessException extends RuntimeException{
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
