package com.vanderlande.viac.template;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * The class TemplateActionPanel.
 *
 * @author dedhor
 * 
 * Action panel for template table entrys.
 * @param <T> the generic type
 */
public class TemplateActionPanel<T> extends Panel
{
	
	/** The panel buttons. */
	private List<Object> panelButtons;
	
	/**
	 * Get panel buttons.
	 *
	 * @return panel buttons
	 */
	public List<Object> getPanelButtons() {
		return panelButtons;
	}

	/**
	 * Set panel buttons.
	 *
	 * @param panelButtons the new panel buttons
	 */
	public void setPanelButtons(List<Object> panelButtons) {
		this.panelButtons = panelButtons;
	}

	/**
	 * Action panel constructor for the template, init panel buttons.
	 *
	 * @param id the id
	 * @param model the model
	 * @param editLink the edit link
	 * @param previewLink the preview link
	 */
	public TemplateActionPanel(String id, IModel<T> model, Link editLink, AjaxLink previewLink)
    {
        super(id, model);
        panelButtons = new ArrayList<>();
        panelButtons.add(editLink);
        panelButtons.add(previewLink);
        add(editLink);
        add(previewLink);
        this.setOutputMarkupId(true);	        
    }
}