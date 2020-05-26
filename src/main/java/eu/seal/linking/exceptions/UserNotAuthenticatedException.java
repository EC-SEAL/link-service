package eu.seal.linking.exceptions;

public class UserNotAuthenticatedException extends LinkApplicationException
{
    public UserNotAuthenticatedException()
    {
        super();
    }

    public UserNotAuthenticatedException(String error)
    {
        super(error);
    }

}
