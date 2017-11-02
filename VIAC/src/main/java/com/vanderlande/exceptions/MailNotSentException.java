package com.vanderlande.exceptions;

/**
 * The Class MailNotSentException.
 * 
 * @author desczu
 * 
 *         This exception is thrown when a mail could not be sent.
 */
public class MailNotSentException extends Exception
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4263593087310696121L;

    /**
     * Instantiates a new mail not sent exception.
     */
    public MailNotSentException()
    {
        super();
    }

    /**
     * Instantiates a new mail not sent exception.
     *
     * @param message
     *            the message
     */
    public MailNotSentException(String message)
    {
        super(message);
    }
}
