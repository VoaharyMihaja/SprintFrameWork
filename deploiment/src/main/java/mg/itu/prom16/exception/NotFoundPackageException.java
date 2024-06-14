package mg.itu.prom16.exception;

public class NotFoundPackageException extends Exception
{
    public NotFoundPackageException(String packageName)
    {
        super("Package not found exception: " + packageName);
    }
}
