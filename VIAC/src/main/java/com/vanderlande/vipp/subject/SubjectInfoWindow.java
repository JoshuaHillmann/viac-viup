package com.vanderlande.vipp.subject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import com.vanderlande.vipp.model.VIPPSubject;

/**
 * The Class SubjectInfoWindow.
 * 
 * @author dermic
 * 
 *         This class displays a notification-dialog. Instantiate the dialog once and then just use showMessage() to
 *         display the dialog.
 */
public class SubjectInfoWindow extends ModalWindow
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -121125320145627714L;

    /** The default width. */
    private final int DEFAULT_WIDTH = 500;

    /** The default height. */
    private final int DEFAULT_HEIGHT = 300;

    /**
     * Instantiates a new notification dialog.
     *
     * @param id
     *            the id
     */
    public SubjectInfoWindow(String id)
    {
        super(id);

        initialize();
    }

    /**
     * Initialize.
     */
    private void initialize()
    {
        this.showUnloadConfirmation(false);
        this.setInitialWidth(DEFAULT_WIDTH);
        this.setInitialHeight(DEFAULT_HEIGHT);

        this.setResizable(false);
        this.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
        {
            private static final long serialVersionUID = 5613838020934203498L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target)
            {
                return true;
            }
        });
    }

    /**
     * Show message.
     *
     * @param message
     *            the message
     * @param target
     *            the target
     */
    public void showMessage(VIPPSubject subject, IPartialPageRequestHandler target)
    {
        this.setContent(new SubjectInfoWindowContent(this.getContentId(), subject));
        this.show(target);

    }
}
