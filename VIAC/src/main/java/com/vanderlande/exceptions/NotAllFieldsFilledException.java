package com.vanderlande.exceptions;

/**
 * The Class NotAllFieldsFilledException.
 * 
 * @author desczu
 * 
 *         This exception is thrown when not fields are filled.
 */
public class NotAllFieldsFilledException extends Exception
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4263593087310696121L;

    /**
     * Instantiates a new not all fields filled exception.
     */
    public NotAllFieldsFilledException()
    {
        super();
    }

    /**
     * Instantiates a new not all fields filled exception.
     *
     * @param message
     *            the message
     */
    public NotAllFieldsFilledException(String message)
    {
        super(message);
    }
}
