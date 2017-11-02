package com.vanderlande.viac.command;

import java.io.Serializable;

/**
 * The Class Command.
 * 
 * @author desczu
 * 
 *         You have to create a new Command and implement execute() if you want to dialogs.
 *         This method will be executed when you click the correct button in the dialog.
 */
public class Command implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4620887635792047705L;

    /**
     * Execute.
     */
    public void execute()
    {
    };
}
