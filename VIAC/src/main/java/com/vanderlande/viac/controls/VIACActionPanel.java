package com.vanderlande.viac.controls;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

/**
 * The Class VIACActionPanel.
 * 
 * @author desczu
 * 
 *         If you want to display buttons in a table you can do this with a VIACActionPanel. You can add as many
 *         VIACButtons to this as you want.
 */
public class VIACActionPanel extends Panel
{

    /** The button container. */
    RepeatingView buttonContainer;
    
    /** The panel buttons. */
    List<VIACButton> panelButtons;


	/**
     * Instantiates a new VIAC action panel.
     *
     * @param id
     *            the id
     * @param buttons
     *            the buttons
     */
    public VIACActionPanel(String id, VIACButton... buttons)
    {
        super(id);
        buttonContainer = new RepeatingView("buttonContainer");
        panelButtons = new ArrayList<>();
        for (VIACButton button : buttons)
        {
            Fragment fragment = new Fragment(buttonContainer.newChildId(), "fragment", this);
            fragment.add(button);
            panelButtons.add(button);
            buttonContainer.add(fragment);
        }
        add(buttonContainer);
    }
    
  
    /**
     * Gets the panel buttons.
     *
     * @return the panel buttons
     */
    public List<VIACButton> getPanelButtons() {
    	return panelButtons;
    }

}
