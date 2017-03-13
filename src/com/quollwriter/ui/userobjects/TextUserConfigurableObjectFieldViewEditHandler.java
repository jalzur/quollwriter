package com.quollwriter.ui.userobjects;

import java.awt.event.*;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

import javax.swing.*;

import com.gentlyweb.utils.*;

import com.quollwriter.*;
import com.quollwriter.ui.*;
import com.quollwriter.ui.forms.*;
import com.quollwriter.data.*;

public class TextUserConfigurableObjectFieldViewEditHandler extends AbstractUserConfigurableObjectFieldViewEditHandler<TextUserConfigurableObjectTypeField, StringWithMarkup>
{

    private JTextField editItem = null;
    //private MultiLineTextFormItem editItem = null;
    
    public TextUserConfigurableObjectFieldViewEditHandler (TextUserConfigurableObjectTypeField typeField,
                                                           UserConfigurableObject              obj,
                                                           UserConfigurableObjectField         field,
                                                           AbstractProjectViewer               viewer)
    {
        
        super (typeField,
               obj,
               field,
               viewer);
                
        //this.typeField = (TextUserConfigurableObjectTypeField) this.field.getUserConfigurableObjectTypeField ();

    }
     
    @Override
    public void grabInputFocus ()
    {
        
        if (this.editItem != null)
        {
        
            this.editItem.grabFocus ();
            
        }
     
    }
    
    @Override     
    public Set<FormItem> getInputFormItems (String         initValue,
                                            ActionListener formSave)
    {

        Set<FormItem> items = new LinkedHashSet ();

        this.editItem = UIUtils.createTextField ();
    
        UIUtils.addDoActionOnReturnPressed (this.editItem,
                                            formSave);
    
    
        StringWithMarkup text = this.getFieldValue ();
            
        if (text != null)
        {
            
            this.editItem.setText (text.getText ());
            
        } else {
            
            if (initValue != null)
            {
                
                this.editItem.setText (initValue);
                
            }
            
        }
        
        items.add (new AnyFormItem (this.typeField.getFormName (),
                                    this.editItem));
        
        return items;

    }
    
    @Override
    public Set<String> getInputFormItemErrors ()
    {
        
        return null;
        
    }
        
    @Override
    public StringWithMarkup getInputSaveValue ()
    {
        
        return new StringWithMarkup (this.editItem.getText ());
        
    }

    @Override
    public String valueToString (StringWithMarkup val)
                          throws GeneralException
    {
        
        try
        {
        
            return JSONEncoder.encode (val);
        
        } catch (Exception e) {
            
            throw new GeneralException ("Unable to encode value to a string",
                                        e);
            
        }
        
    }
    
    @Override
    public StringWithMarkup stringToValue (String s)
    {
        
        return JSONDecoder.decodeToStringWithMarkup (s);
        
    }
        
    @Override    
    public Set<FormItem> getViewFormItems ()
    {
        
        Set<FormItem> items = new LinkedHashSet ();
        
        StringWithMarkup text = this.getFieldValue ();
        
        if (text != null)
        {
        
            JComponent t = UIUtils.createObjectDescriptionViewPane (text,
                                                                    this.field.getParentObject (),
                                                                    this.viewer,
                                                                    null);
    
            t.setBorder (null);
            
            items.add (new AnyFormItem (this.typeField.getFormName (),
                                        t));

        } else {
            
            items.add (this.createNoValueItem ());

        }
        
        return items;
        
    }

}