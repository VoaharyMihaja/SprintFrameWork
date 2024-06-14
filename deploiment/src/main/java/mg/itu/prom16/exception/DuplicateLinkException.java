package mg.itu.prom16.exception;

public class DuplicateLinkException extends Exception{
    
    public DuplicateLinkException(String link){
        super("Duplicate link exception: " + link);
    }
}
