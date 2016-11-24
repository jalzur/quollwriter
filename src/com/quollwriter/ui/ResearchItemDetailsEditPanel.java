package com.quollwriter.ui;

import java.awt.event.*;

import java.net.*;

import java.util.*;

import java.awt.event.*;

import javax.swing.*;

import com.quollwriter.*;

import com.quollwriter.data.*;

import com.quollwriter.ui.components.*;


public class ResearchItemDetailsEditPanel extends AssetDetailsEditPanel<ResearchItem>
{

    private JTextField urlEdit = null;

    public ResearchItemDetailsEditPanel (ResearchItem  r,
                                         ProjectViewer pv)
    {

        super (r,
               pv);

        this.urlEdit = UIUtils.createTextField ();

    }
    
    public Set<String> getObjectChangeEventTypes ()
    {

        Set<String> types = new HashSet ();
        types.add (ResearchItem.URL);

        return types;

    }

    public String getViewDescription ()
    {

        String d = super.getViewDescription ();

        ResearchItem r = this.object;

        String url = r.getUrl ();

        if ((url != null)
            &&
            (url.trim ().length () > 0)
           )
        {

            if (!url.toLowerCase ().trim ().startsWith ("http://"))
            {

                url = "http://" + url;

            }

            // Add a space after the url to ensure that the markup code finds the "end".
            d = url + " <br /><br />" + d;

        }

        if (d == null)
        {

            d = "<i>No description.</i>";

        }

        return d;

    }

    public String getEditHelpText ()
    {

        return null;

    }

    public void fillForSave ()
    {

        ResearchItem r = this.object;

        r.setUrl (this.urlEdit.getText ().trim ());

    }

    public void fillForEdit ()
    {

        ResearchItem r = this.object;

        this.urlEdit.setText (r.getUrl ());

    }

    @Override
    public boolean hasChanges ()
    {

        ResearchItem r = this.object;

        String u = r.getUrl ();

        if (u == null)
        {

            u = "";

        }

        return !this.urlEdit.getText ().equals (u);

    }

    public boolean canSave ()
    {

        return true;

    }

    public List<FormItem> getExtraEditItems (ActionListener onSave)
    {

        List<FormItem> items = new ArrayList ();
        items.add (new FormItem ("Web Page",
                                 this.urlEdit));

        UIUtils.addDoActionOnReturnPressed (this.urlEdit,
                                            onSave);

        return items;

    }

    public List<FormItem> getExtraViewItems ()
    {

        List<FormItem> items = new ArrayList ();

        return items;

    }

}
