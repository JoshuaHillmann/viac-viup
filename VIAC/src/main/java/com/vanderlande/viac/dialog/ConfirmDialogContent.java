package com.vanderlande.viac.dialog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.vanderlande.viac.command.Command;

/**
 * The Class ConfirmDialogContent.
 * 
 * @author desczu
 * 
 * Used in {@link ConfirmDeleteDialog}. Don't use it yourself.
 */
public class ConfirmDialogContent extends Panel
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2153887923466100167L;

    /**
     * Instantiates a new confirm dialog content.
     *
     * @param contentId the content id
     * @param message the message
     * @param confirmCommand the confirm command
     * @param declineCommand the decline command
     */
    public ConfirmDialogContent(String contentId,
                                String message,
                                final Command confirmCommand,
                                final Command declineCommand)
    {
        super(contentId);
        add(new Label("message", message));
        add(new AjaxLink("confirm")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                confirmCommand.execute();
            }
        });
        add(new AjaxLink("cancel")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                declineCommand.execute();
            }
        });
    }
}
