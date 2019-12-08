package com.quollwriter.text.rules;

import java.util.*;
import java.awt.event.*;

import javax.swing.*;

import javafx.beans.property.*;

import com.gentlyweb.xml.*;

import com.quollwriter.*;
import com.quollwriter.ui.*;
import com.quollwriter.ui.forms.*;
import com.quollwriter.text.*;

import com.quollwriter.ui.fx.components.Form;

// TODO import com.quollwriter.ui.fx.components.Form;

import org.jdom.*;


public abstract class AbstractRule<E extends TextBlock> implements Rule<E>
{

    public class XMLConstants
    {

        public static final String root = "rule";
        public static final String description = "description";
        public static final String id = "id";
        public static final String user = "user";
        public static final String summary = "summary";
        public static final String createType = "createType";

    }

    protected String  summary = null;
    protected String  desc = null;
    protected String  id = null;
    protected boolean userRule = false;
    private String defaultSummary = null;

    public AbstractRule ()
    {

    }

    public abstract String getEditFormTitle (boolean add);

    public abstract Set<FormItem> getFormItems ();

    public Set<Form.Item> getFormItems2 ()
    {

        return null;

    }

    public StringProperty getFormError2 ()
    {

        return null;

    }

    public abstract String getFormError ();

    public String getDefaultSummary ()
    {

        return this.defaultSummary;

    }

    @Override
    public void setSummary (String s)
    {

        this.summary = s;

    }

    @Override
    public String getSummary ()
    {

        return this.summary;

    }

    @Override
    public void setUserRule (boolean u)
    {

        this.userRule = u;

    }

    @Override
    public boolean isUserRule ()
    {

        return this.userRule;

    }

    public void setDescription (String d)
    {

        this.desc = d;

    }

    public void setId (String id)
    {

        this.id = id;

    }

    public String getId ()
    {

        return this.id;

    }

    public String getDescription ()
    {

        return this.desc;

    }

    public void init (Element root)
               throws JDOMException
    {

        this.id = JDOMUtils.getAttributeValue (root,
                                               XMLConstants.id);

        this.userRule = JDOMUtils.getAttributeValueAsBoolean (root,
                                                              XMLConstants.user,
                                                              false);

        this.desc = JDOMUtils.getChildElementContent (root,
                                                      XMLConstants.description,
                                                      false);

        this.summary = JDOMUtils.getChildElementContent (root,
                                                         XMLConstants.summary,
                                                         true);

        if (!this.userRule)
        {

            this.defaultSummary = this.summary;

        }

    }

    public Element getAsElement ()
    {

        Element root = new Element (XMLConstants.root);

        root.setAttribute (XMLConstants.id,
                           this.id);
        root.setAttribute (XMLConstants.createType,
                           this.getClass ().getName ());

        if (this.userRule)
        {

            root.setAttribute (XMLConstants.user,
                               Boolean.toString (this.userRule));

        }

        Element summ = new Element (XMLConstants.summary);

        root.addContent (summ);

        summ.addContent (this.summary);

        if ((this.desc != null)
            &&
            (this.desc.length () > 0)
           )
        {

            Element desc = new Element (XMLConstants.description);

            root.addContent (desc);

            desc.addContent (this.desc);

        }

        return root;

    }

    public com.quollwriter.ui.forms.Form getEditForm (final ActionListener        onSaveComplete,
                             final ActionListener        onCancel,
                             final AbstractProjectViewer viewer,
                             final boolean               add)
    {

        final AbstractRule _this = this;

        Set<FormItem> items = new LinkedHashSet<> ();

        final TextFormItem summary = new TextFormItem (Environment.getUIString (LanguageStrings.form,
                                                                                LanguageStrings.labels,
                                                                                LanguageStrings.summary),
                                                       //"Summary",
                                                       this.getSummary ());

        items.add (summary);

        items.addAll (this.getFormItems ());

        final MultiLineTextFormItem desc = new MultiLineTextFormItem (Environment.getUIString (LanguageStrings.form,
                                                                                               LanguageStrings.labels,
                                                                                               LanguageStrings.description),
                                                                      //"Description",
                                                                      viewer,
                                                                      5);
        desc.setText (this.getDescription ());
        desc.setCanFormat (false);

        items.add (desc);

        Map<com.quollwriter.ui.forms.Form.Button, ActionListener> buttons = new LinkedHashMap<> ();

        buttons.put (com.quollwriter.ui.forms.Form.Button.save,
                     new ActionListener ()
                     {

                        @Override
                        public void actionPerformed (ActionEvent ev)
                        {

                            com.quollwriter.ui.forms.Form f = (com.quollwriter.ui.forms.Form) ev.getSource ();

                            String error = _this.getFormError ();

                            if (error != null)
                            {

                                f.showError (error);

                                return;

                            }

                            _this.setDescription (desc.getText ().trim ());

                            String summ = summary.getText ();

                            if (summ == null)
                            {

                                summ = "";

                            } else {

                                summ = summ.trim ();

                            }

                            if (summ.length () == 0)
                            {

                                summ = _this.getSummary ();

                            }

                            if (summ == null)
                            {

                                summ = _this.getDefaultSummary ();

                            }

                            if (summ == null)
                            {

                                f.showError (Environment.getUIString (LanguageStrings.problemfinder,
                                                                      LanguageStrings.config,
                                                                      LanguageStrings.entersummaryerror));

                                return;

                            }

                            _this.setSummary (summ);

                            if (onSaveComplete != null)
                            {

                                onSaveComplete.actionPerformed (new ActionEvent (_this, 1, "saved"));

                            }

                        }

                     });

        buttons.put (com.quollwriter.ui.forms.Form.Button.cancel,
                     new ActionListener ()
                     {

                        @Override
                        public void actionPerformed (ActionEvent ev)
                        {

                            if (onCancel != null)
                            {

                                onCancel.actionPerformed (new ActionEvent (_this, 1, "cancelled"));

                            }

                            return;

                        }

                     });

        com.quollwriter.ui.forms.Form f = new com.quollwriter.ui.forms.Form (com.quollwriter.ui.forms.Form.Layout.stacked,
                           items,
                           buttons);

        return f;

    }

}
