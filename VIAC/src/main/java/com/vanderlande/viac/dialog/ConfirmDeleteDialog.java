package com.vanderlande.viac.dialog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import com.vanderlande.viac.command.Command;

/**
 * The Class ConfirmDeleteDialog.
 * 
 * @author desczu
 * 
 *         This class displays a confirm-dialog when the user wants to delete something. Instantiate the dialog once and
 *         then just use showMessage() to display the dialog.
 */
public class ConfirmDeleteDialog extends ModalWindow
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -121125320145627714L;

    /** The default width. */
    private final int DEFAULT_WIDTH = 400;

    /** The default height. */
    private final int DEFAULT_HEIGHT = 120;

    /**
     * Instantiates a new confirm delete dialog.
     *
     * @param id
     *            the id
     */
    public ConfirmDeleteDialog(String id)
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
     * @param confirmCommand
     *            the confirm command
     * @param declineCommand
     *            the decline command
     * @param target
     *            the target
     */
    public void showMessage(String message, Command confirmCommand, Command declineCommand,
                            IPartialPageRequestHandler target)
    {
        this.setContent(new ConfirmDeleteDialogContent(this.getContentId(), message, confirmCommand, declineCommand));
        this.show(target);
    }
}
