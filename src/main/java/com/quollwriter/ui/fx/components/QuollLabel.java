package com.quollwriter.ui.fx.components;

import java.util.*;

import javafx.beans.property.*;
import javafx.beans.binding.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.*;

import com.quollwriter.*;
import com.quollwriter.ui.fx.*;

import static com.quollwriter.uistrings.UILanguageStringsManager.getUILanguageStringProperty;

public class QuollLabel extends Label
{

    private Pane icon = null;

    private QuollLabel (Builder b)
    {

        if (b.label != null)
        {

            this.textProperty ().bind (b.label);

        }

        if (b.tooltip != null)
        {

            Tooltip t = new Tooltip ();
            t.textProperty ().bind (b.tooltip);

            this.setTooltip (t);

        }

        if (b.styleName != null)
        {

            this.getStyleClass ().add (b.styleName);

        }

        this.graphicProperty ().addListener ((pr, oldv, newv) ->
        {

            if (newv != null)
            {

                newv.managedProperty ().unbind ();
                newv.managedProperty ().bind (newv.visibleProperty ());

            }

        });

        this.managedProperty ().bind (this.visibleProperty ());

        HBox h = new HBox ();
        h.getStyleClass ().add (StyleClassNames.ICONBOX);
        this.icon = new Pane ();

        if (b.styleName != null)
        {

            this.icon.getStyleClass ().add (b.styleName + "-" + StyleClassNames.ICON);

        }
        this.icon.getStyleClass ().add (StyleClassNames.ICON);
        h.getChildren ().add (this.icon);
        h.managedProperty ().bind (h.visibleProperty ());
        this.setGraphic (h);

    }

    public void setIconClassName (String c)
    {

        String r = null;

        for (String s : this.icon.getStyleClass ())
        {

            if (s.endsWith (StyleClassNames.ICON_SUFFIX))
            {

                r = s;
                break;

            }

        }

        if (r != null)
        {

            this.icon.getStyleClass ().remove (r);

        }

        if (!c.endsWith (StyleClassNames.ICON_SUFFIX))
        {

            c += StyleClassNames.ICON_SUFFIX;

        }

        this.icon.getStyleClass ().add (c);

    }

    /**
     * Get a builder to create a new label.
     *
     * Usage: QuollLabel.builder ().styleName ("hello").build ();
     * @returns A new builder.
     */
    public static QuollLabel.Builder builder ()
    {

        return new Builder ();

    }

    public static class Builder implements IBuilder<Builder, QuollLabel>
    {

        private StringProperty label = null;
        private String styleName = null;
        private StringProperty tooltip = null;

        private Builder ()
        {

        }

        @Override
        public QuollLabel build ()
        {

            return new QuollLabel (this);

        }

        @Override
        public Builder _this ()
        {

            return this;

        }

        public Builder styleClassName (String n)
        {

            this.styleName = n;

            return this;

        }

        public Builder tooltip (StringProperty prop)
        {

            this.tooltip = prop;

            return this;

        }

        public Builder tooltip (List<String> prefix,
                                String...    ids)
        {

            this.tooltip = getUILanguageStringProperty (Utils.newList (prefix, ids));
            return this;

        }

        public Builder tooltip (String... ids)
        {

            this.tooltip = getUILanguageStringProperty (ids);
            return this;

        }

        public Builder label (StringProperty prop)
        {

            this.label = prop;
            return this;

        }

        public Builder label (List<String> prefix,
                              String...    ids)
        {

            return this.label (getUILanguageStringProperty (Utils.newList (prefix, ids)));

        }

        public Builder label (String... ids)
        {

            return this.label (getUILanguageStringProperty (ids));

        }

    }

}