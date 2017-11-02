package com.vanderlande.viac.controls;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;

/**
 * The Class VIACButton.
 * 
 * @author desczu
 * 
 *         The VIACButton is used for the VIACActionPanel. You can add as many buttons to it as you want. Every
 *         VIACButton can have a glyphicon and a text (using just one of these two is also possible with the correct
 *         constructor). To instantiate a VIACButton with a glyphicon (bootstrap glyphicons are used) you have to use a
 *         Type. See {@link Type} for more information.
 */
public class VIACButton extends AjaxLink
{

    /**
     * The Enum Type.
     * 
     * @author desczu
     * 
     *         This enumeration is used to display all VIACButtons with the same bootstrap-look-and-feel. If you need
     *         more Buttonicons simply have a look on the bootstrap glyphicons and add a new type for the icon that you
     *         need.
     */
    public enum Type
    {

        /** The edit. */
        EDIT("edit"),

        /** The details. */
        DETAILS("info-sign"),

        /** The delete. */
        DELETE("trash"),

        /** The remove from. */
        REMOVE_FROM("minus"),

        /** The add. */
        ADD("plus");

        /** The class attribute. */
        private final String classAttribute;

        /**
         * Instantiates a new type.
         *
         * @param classAttribute
         *            the class attribute
         */
        Type(String classAttribute)
        {
            this.classAttribute = classAttribute;
        }

        /**
         * Gets the class attribute.
         *
         * @return the class attribute
         */
        public String getClassAttribute()
        {
            return classAttribute;
        }
    }

    /** The Constant LINK_ID. */
    final private static String LINK_ID = "button";

    /** The Constant LABEL_ID. */
    final private static String LABEL_ID = "buttonText";

    /** The Constant GLYPH_ID. */
    final private static String GLYPH_ID = "glyph";

    /** The button text. */
    private Label buttonText;

    /** The text. */
    private String text;

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text.
     *
     * @param text
     *            the new text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Gets the button text.
     *
     * @return the button text
     */
    public Label getButtonText()
    {
        return buttonText;
    }

    /**
     * Sets the button text.
     *
     * @param buttonText
     *            the new button text
     */
    public void setButtonText(Label buttonText)
    {
        this.buttonText = buttonText;
    }

    /** The glyph. */
    private Label glyph;

    /** The button type. */
    private Type buttonType;

    /**
     * Instantiates a new VIAC button.
     *
     * @param type
     *            the type
     */
    public VIACButton(Type type)
    {
        super(LINK_ID);
        buttonType = type;
        glyph = new Label(GLYPH_ID);
        glyph.add(ClassAttributeModifier.append("class", "glyphicon glyphicon-" + type.getClassAttribute()));
        buttonText = new Label(LABEL_ID);
        add(buttonText);
        add(glyph);
    }

    /**
     * Instantiates a new VIAC button with a text next to it.
     *
     * @param type
     *            the type
     * @param text
     *            the text
     */
    public VIACButton(Type type, String text)
    {
        super(LINK_ID);
        buttonType = type;
        glyph = new Label(GLYPH_ID);
        glyph.add(ClassAttributeModifier.append("class", "glyphicon glyphicon-" + type.getClassAttribute()));
        buttonText = new Label(LABEL_ID, text);
        this.text = text;
        add(buttonText);
        add(glyph);
    }

    /**
     * Instantiates a new VIAC button without an icon (just text)
     *
     * @param text
     *            the text
     */
    public VIACButton(String text)
    {
        super(LINK_ID);
        glyph = new Label(GLYPH_ID);
        buttonText = new Label(LABEL_ID, text);
        this.text = text;
        add(buttonText);
        add(glyph);
    }

    /**
     * Gets the button type.
     *
     * @return the button type
     */
    public Type getButtonType()
    {
        return buttonType;
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.ajax.markup.html.AjaxLink#onClick(org.apache.wicket.ajax.AjaxRequestTarget)
     */
    @Override
    public void onClick(AjaxRequestTarget target)
    {
    }

}
