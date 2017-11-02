package com.vanderlande.vita.applicant.dialog;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import com.vanderlande.vita.applicant.ApplicantDetailPage;
import com.vanderlande.vita.model.VITAApplicant;

/**
 * The Class ConfirmCreateDialogContent.
 * 
 * @author dekscha
 * 
 *         Content of the applicant-creation-dialog. This dialog shows up when the user tries to create an applicant
 *         that already exists (same name, etc).
 */
public class ConfirmCreateDialogContent extends Panel
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5042187879408682326L;

    /** The default width. */
    public static int DEFAULT_WIDTH = 450;

    /** The default height. */
    public static int DEFAULT_HEIGHT = 300;

    /** The applicants. */
    private List<VITAApplicant> applicants;

    /** The last page. */
    private WebPage lastPage;

    /**
     * Instantiates a new confirm create dialog content.
     *
     * @param id
     *            the id
     * @param applicants
     *            the applicants
     */
    public ConfirmCreateDialogContent(String id, List<VITAApplicant> applicants, WebPage lastPage)
    {
        super(id);
        this.applicants = applicants;
        this.lastPage = lastPage;
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

        RepeatingView applicantContainer = new RepeatingView("applicantContainer");
        add(applicantContainer);

        for (final VITAApplicant vitaApplicant : applicants)
        {
            Fragment fragment = new Fragment(applicantContainer.newChildId(), "fragment", this);
            Label applicantNameLabel = new Label(
                "applicantItemLabel",
                vitaApplicant.getFirstname() + " " + vitaApplicant.getLastname() + "(" + vitaApplicant.getMail() + ")");

            Link applicantLink = new Link("applicantLink")
            {
                /** The Constant serialVersionUID. */
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick()
                {
                    setResponsePage(new ApplicantDetailPage(vitaApplicant, lastPage));
                }
            };

            applicantContainer.add(fragment);
            fragment.add(applicantLink);
            applicantLink.add(applicantNameLabel);
        }
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
