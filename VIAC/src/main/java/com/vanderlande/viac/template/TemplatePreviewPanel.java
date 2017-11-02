package com.vanderlande.viac.template;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * The class TemplatePreviewPanel
 * 
 * @author dedhor
 * 
 * Shows a preview of for a specific html content in a field.
 */
public class TemplatePreviewPanel extends Panel
{
	private static final long serialVersionUID = 6179771752963210448L;

	/**
     * Constructor for the preview panel.
     * @param id
     * @param content
     */
    public TemplatePreviewPanel(String id, String content)
    {
        super(id);
        add(new Label("info", content).setEscapeModelStrings(false));
    }
}