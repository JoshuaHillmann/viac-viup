package com.vanderlande.vipp.attendees;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * The Class ConfirmCreateDialogContent.
 * 
 * @author dermic
 * 
 *         Content of the apprentice-creation-dialog. This dialog shows up when the user tries to create an apprentice
 *         that already exists (same first- and lastname).
 */
public class ConfirmReactivateApprenticeContent extends Panel
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5042187879408682326L;

    /** The default width. */
    public static int DEFAULT_WIDTH = 450;

    /** The default height. */
    public static int DEFAULT_HEIGHT = 300;

    /**
     * Instantiates a new confirm create dialog content.
     *
     * @param id
     *            the id
     * @param applicants
     *            the applicants
     */
    public ConfirmReactivateApprenticeContent(String id, WebPage lastPage)
    {
        super(id);
        initialize();
    }

    /**
     * Initialize.
     */
    private void initialize()
    {
        add(new AjaxLink("confirm")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onConfirm();
            }
        });
        add(new AjaxLink("create")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onCreate();
            }
        });
        add(new AjaxLink("cancel")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onCancel();
            }
        });
    }

    /**
     * On confirm.
     */
    public void onConfirm()
    {
    };
    
    /**
     * On create.
     */
    public void onCreate()
    {
    };

    /**
     * On cancel.
     */
    public void onCancel()
    {
    };
}
