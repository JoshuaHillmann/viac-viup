package com.vanderlande.exceptions;

/**
 * The Class UserAlreadyExistsException.
 * 
 * @author dekscha
 * 
 *         This exception is throw when a VIACUser is changed or created and violates a unique constraint in the
 *         database.
 */
public class UserAlreadyExistsException extends RuntimeException
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4263593087310696121L;

    /**
     * Instantiates a new user already exists exception.
     */
    public UserAlreadyExistsException()
    {
        super();
    }

    /**
     * Instantiates a new user already exists exception.
     *
     * @param message
     *            the message
     */
    public UserAlreadyExistsException(String message)
    {
        super(message);
    }

    /**
     * Instantiates a new user already exists exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public UserAlreadyExistsException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Instantiates a new user already exists exception.
     *
     * @param cause
     *            the cause
     */
    public UserAlreadyExistsException(Throwable cause)
    {
        super(cause);
    }
}
