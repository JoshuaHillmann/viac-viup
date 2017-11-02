package com.vanderlande.vipp.subject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * The Class ConfirmCreateSubjectDialogContent.
 * 
 * @author dermic
 * 
 *         Content of the subject-creation-dialog. This dialog shows up when the user tries to create a subject
 *         that already exists (same name).
 */
public class ConfirmCreateSubjectContent extends Panel
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
     * @param subjects
     *            the subjects
     */
    public ConfirmCreateSubjectContent(String id, WebPage lastPage)
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
     * On cancel.
     */
    public void onCancel()
    {
    };
}
