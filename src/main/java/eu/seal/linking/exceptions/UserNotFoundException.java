package eu.seal.linking.exceptions;

public class UserNotFoundException extends LinkApplicationException
{
    public UserNotFoundException()
    {
        super();
    }

    public UserNotFoundException(String error)
    {
        super(error);
    }
}
