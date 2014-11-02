package ie.markomeara.irelandtraintimes.exceptions;

/**
 * Created by Mark on 02/11/2014.
 */
public class DBNotAvailableException extends Exception {

    // TODO Confirm this custom exception is actually necessary

    public DBNotAvailableException(String msg){
        super(msg);
    }
}
