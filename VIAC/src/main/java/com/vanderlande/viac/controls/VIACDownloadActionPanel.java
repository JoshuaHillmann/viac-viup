package com.vanderlande.viac.controls;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * The Class VIACDownloadActionPanel.
 * 
 * @author desczu
 *
 * @param <T> the generic type
 * 
 *            If you want to download a file by clicking a downloadlink in a tablerow you can use this ActionPanel. You
 *            can also delete this file by clicking "(löschen)" next to the download-label.
 */
public class VIACDownloadActionPanel<T> extends Panel
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1527232132450109536L;

    /**
     * Instantiates a new VIAC download action panel.
     *
     * @param id
     *            the id
     * @param model
     *            the model
     * @param link
     *            the link
     * @param deleteLink
     *            the delete link
     */
    public VIACDownloadActionPanel(String id, IModel<T> model, DownloadLink link, AjaxLink deleteLink)
    {
        super(id, model);
        add(link);
        add(deleteLink);
        this.setOutputMarkupId(true);

    }
    
    
}
