package com.vanderlande.viac.dialog;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * The Class NotificationDialog.
 * 
 * @author desczu
 * 
 *         This class displays a notification-dialog. Instantiate the dialog once and then just use showMessage() to
 *         display the dialog.
 */
public class NotificationDialog extends ModalWindow
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -121125320145627714L;

    /** The default width. */
    private final int DEFAULT_WIDTH = 400;

    /** The default height. */
    private final int DEFAULT_HEIGHT = 120;

    /**
     * Instantiates a new notification dialog.
     *
     * @param id
     *            the id
     */
    public NotificationDialog(String id)
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
    public void showMessage(String message, IPartialPageRequestHandler target)
    {
        this.setContent(new NotificationDialogContent(this.getContentId(), message));
        this.show(target);
    }

    /**
     * Show message.
     *
     * @param message
     *            the message
     * @param target
     *            the target
     * @param responsePage
     *            the response page
     */
    public void showMessage(String message, IPartialPageRequestHandler target, final Component responsePage)
    {
        this.setContent(new NotificationDialogContent(this.getContentId(), message));
        this.show(target);
        this.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
        {
            private static final long serialVersionUID = 5613838020934203498L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target)
            {
                target.add(responsePage);
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
    public void showMessage(String message)
    {
        this.setContent(new NotificationDialogContent(this.getContentId(), message));
    }
}
