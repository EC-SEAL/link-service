package eu.seal.linking.exceptions;

public class UserNotAuthorizedException extends LinkApplicationException
{
    public UserNotAuthorizedException()
    {
        super();
    }

    public UserNotAuthorizedException(String error)
    {
        super(error);
    }
}
