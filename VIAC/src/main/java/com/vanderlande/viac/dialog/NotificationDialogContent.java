package com.vanderlande.viac.dialog;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * The Class NotificationDialogContent.
 * 
 * @author desczu
 * 
 * Used in {@link NotificationDialog}. Don't use it yourself.
 */
public class NotificationDialogContent extends Panel
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5692237700953217611L;

    /**
     * Instantiates a new notification dialog content.
     *
     * @param contentId the content id
     * @param message the message
     */
    public NotificationDialogContent(String contentId, String message)
    {
        super(contentId);
        add(new Label("text", message));
    }
}
