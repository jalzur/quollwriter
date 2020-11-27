package com.quollwriter.ui.fx.components;

import java.util.*;
import java.util.function.*;

import javafx.beans.property.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.beans.value.*;
import javafx.collections.*;

import com.quollwriter.*;
import com.quollwriter.ui.fx.*;
import com.quollwriter.ui.fx.viewers.*;
import com.quollwriter.ui.fx.sidebars.*;

import static com.quollwriter.uistrings.UILanguageStringsManager.getUILanguageStringProperty;
import static com.quollwriter.LanguageStrings.*;

public class SideBar extends BaseVBox implements Stateful
{

    private AbstractViewer viewer = null;
    private Button otherSideBarsShowButton = null;
    private Header header = null;
    private SideBarContent content = null;
    private String sidebarId = null;
    private StringProperty titleProp = null;
    private StringProperty activeTitleProp = null;
    private BooleanProperty canCloseProp = null;
    private String styleName = null;
    private ScrollPane scrollPane = null;

    private SideBar (Builder b)
    {

        final SideBar _this = this;

        if (b.viewer == null)
        {

            throw new IllegalArgumentException ("A viewer must be provided.");

        }

        if (b.sidebarId == null)
        {

            throw new IllegalArgumentException ("A sidebar id must be provided.");

        }

        this.sidebarId = b.sidebarId;
        this.viewer = b.viewer;

        UIUtils.addStyleSheet (this,
                               Constants.COMPONENT_STYLESHEET_TYPE,
                               StyleClassNames.SIDEBAR);
        if (b.styleSheet != null)
        {

            UIUtils.addStyleSheet (this,
                                   Constants.SIDEBAR_STYLESHEET_TYPE,
                                   b.styleSheet);

        }

        this.getStyleClass ().add (StyleClassNames.SIDEBAR);

        if (b.styleName != null)
        {

            this.getStyleClass ().add (b.styleName);

        }

        this.styleName = b.styleName;

        this.otherSideBarsShowButton = QuollButton.builder ()
            .tooltip (sidebars,othersidebarselect,tooltip)
            .iconName (StyleClassNames.OTHER)
            .onAction (ev ->
            {

                ContextMenu n = viewer.getShowOtherSideBarsSelector ();

                n.show (this.otherSideBarsShowButton, Side.BOTTOM, 0, 0);
/*
                n.show (_this.otherSideBarsShowButton,
                        ev.getX (),
                        ev.getY ());
*/
            })
            .build ();

        Set<Node> hcontrols = new LinkedHashSet<> ();

        if (b.headerCons != null)
        {

            hcontrols.addAll (b.headerCons);

        }

        hcontrols.add (this.otherSideBarsShowButton);

        this.canCloseProp = new SimpleBooleanProperty (b.canClose);

        if (b.canClose)
        {

            QuollButton but = QuollButton.builder ()
                .tooltip (sidebars,close,tooltip)
                .iconName (StyleClassNames.CLOSE)
                .onAction (ev ->
                {

                    _this.viewer.closeSideBar ();

                })
                .build ();

            hcontrols.add (but);

        }

        this.header = Header.builder ()
            .controls (hcontrols)
            .title (b.title != null ? b.title : null)
            .contextMenu (b.contextMenuItemSupplier)
            .iconClassName (b.headerIconStyleName)
            .build ();

        this.header.managedProperty ().bind (this.header.visibleProperty ());

        this.titleProp = this.header.titleProperty ();

        this.activeTitleProp = b.activeTitle;
        this.otherSideBarsShowButton.managedProperty ().bind (this.otherSideBarsShowButton.visibleProperty ());
        this.otherSideBarsShowButton.setVisible (false);

        this.getChildren ().addAll (this.header);

        this.addEventHandler (SideBarEvent.SHOW_EVENT,
                              ev ->
        {

            _this.otherSideBarsShowButton.setVisible (ev.getViewer ().getActiveSideBarCount () > 1 && ev.getViewer ().getActiveOtherSideBar () != null);

        });

        this.addEventHandler (SideBarEvent.HIDE_EVENT,
                              ev ->
        {

            _this.otherSideBarsShowButton.setVisible (ev.getViewer ().getActiveSideBarCount () > 1 && ev.getViewer ().getActiveOtherSideBar () != null);

        });

        if (b.content != null)
        {

            b.content.getStyleClass ().add (StyleClassNames.CONTENT);
            this.content = b.content;

            if (b.wrapInScrollPane)
            {

                this.scrollPane = new ScrollPane (b.content);

                VBox.setVgrow (this.scrollPane,
                               Priority.ALWAYS);

                this.getChildren ().add (this.scrollPane);

            } else {

                VBox.setVgrow (b.content,
                               Priority.ALWAYS);

                this.getChildren ().add (b.content);

            }

            this.content.managedProperty ().bind (this.content.visibleProperty ());
            VBox.setVgrow (this.content,
                           Priority.ALWAYS);

        }

        this.managedProperty ().bind (this.visibleProperty ());

    }

    public Header getHeader ()
    {

        return this.header;

    }

    public SideBarContent getContent ()
    {

        return this.content;

    }

    public ScrollPane getScrollPane ()
    {

        return this.scrollPane;

    }

    @Override
    public void init (State state)
    {

        if (state == null)
        {

            state = new State ();

        }

        if (this.scrollPane != null)
        {

            this.scrollPane.setVvalue (state.getAsInt (State.Key.scrollpanev,
                                                       0));

        }

        if (this.content != null)
        {

            try
            {

                this.content.init (state.getAsState (State.Key.content));

            } catch (Exception e) {

                Environment.logError ("Unable to init content with state: " + state,
                                      e);

            }

        }

    }

    @Override
    public State getState ()
    {

        State s = new State ();

        if (this.scrollPane != null)
        {

            s.set (State.Key.scrollpanev,
                   this.scrollPane.getVvalue ());

        }

        if (this.content != null)
        {

            s.set (State.Key.content,
                   this.content.getState ());

        }

        return s;

    }

    /**
     * Get a builder to create a new SideBar.
     *
     * Usage: SideBar.builder ().styleName ("hello").build ();
     * @returns A new builder.
     */
    public static Builder builder ()
    {

        return new Builder ();

    }

    public static class Builder implements IBuilder<Builder, SideBar>
    {

        private AbstractViewer viewer = null;
        private StringProperty title = null;
        private StringProperty activeTitle = null;
        private String styleName = null;
        private SideBarContent content = null;
        private Set<Node> headerCons = null;
        private boolean canClose = false;
        private String sidebarId = null;
        private boolean wrapInScrollPane = true;
        private Supplier<Set<MenuItem>> contextMenuItemSupplier = null;
        private String styleSheet = null;
        private String headerIconStyleName = null;

        private Builder ()
        {

        }

        @Override
        public SideBar build ()
        {

            return new SideBar (this);

        }

        @Override
        public Builder _this ()
        {

            return this;

        }

        public Builder headerIconClassName (String s)
        {

            this.headerIconStyleName = s;
            return _this ();

        }

        public Builder styleSheet (String s)
        {

            this.styleSheet = s;
            return this;

        }

        public Builder contextMenu (final Set<MenuItem> items)
        {

            this.contextMenuItemSupplier = new Supplier<Set<MenuItem>> ()
            {

                @Override
                public Set<MenuItem> get ()
                {

                    return items;

                }

            };

            return this;

        }

        public Builder contextMenu (Supplier<Set<MenuItem>> items)
        {

            this.contextMenuItemSupplier = items;
            return this;

        }

        public Builder styleClassName (String name)
        {

            this.styleName = name;
            return this;

        }

        public Builder content (SideBarContent n)
        {

            this.content = n;
            return this;

        }

        public Builder withScrollPane (boolean v)
        {

            this.wrapInScrollPane = v;
            return this;

        }

        public Builder title (String... ids)
        {

            return this.title (getUILanguageStringProperty (ids));

        }

        public Builder title (List<String> prefix,
                              String... ids)
        {

            return this.title (getUILanguageStringProperty (Utils.newList (prefix,ids)));

        }

        public Builder title (StringProperty prop)
        {

            this.title = prop;
            return this;

        }

        public Builder activeTitle (String... ids)
        {

            return this.activeTitle (getUILanguageStringProperty (ids));

        }

        public Builder activeTitle (List<String> prefix,
                                    String... ids)
        {

            return this.activeTitle (getUILanguageStringProperty (Utils.newList (prefix, ids)));

        }

        public Builder activeTitle (StringProperty prop)
        {

            this.activeTitle = prop;
            return this;

        }

        public Builder headerControls (Set<Node> cons)
        {

            this.headerCons = cons;
            return this;

        }

        public Builder canClose (boolean v)
        {

            this.canClose = v;
            return this;

        }

        public Builder withViewer (AbstractViewer v)
        {

            this.viewer = v;
            return this;

        }

        public Builder sideBarId (String id)
        {

            this.sidebarId = id;
            return this;

        }

    }

    public StringProperty titleProperty ()
    {

        return this.titleProp;

    }

    public ReadOnlyBooleanProperty canCloseProperty ()
    {

        return this.canCloseProp.readOnlyBooleanProperty (this.canCloseProp);

    }

    public StringProperty activeTitleProperty ()
    {

        if (this.activeTitleProp == null)
        {

            return this.titleProperty ();

        }

        return this.activeTitleProp;

    }

    public String getSideBarId ()
    {

        return this.sidebarId;

    }

    public AbstractViewer getViewer ()
    {

        return this.viewer;

    }

    public String getStyleClassName ()
    {

        return this.styleName;

    }

    public static class SideBarEvent extends Event
    {

        public static final EventType<SideBarEvent> HIDE_EVENT = new EventType<> ("hide");
        public static final EventType<SideBarEvent> SHOW_EVENT = new EventType<> ("show");
        public static final EventType<SideBarEvent> CLOSE_EVENT = new EventType<> ("close");

        private SideBar sidebar = null;
        private AbstractViewer viewer = null;

        public SideBarEvent (AbstractViewer          viewer,
                             SideBar                 sidebar,
                             EventType<SideBarEvent> type)
        {

            super (type);

            this.viewer = viewer;
            this.sidebar = sidebar;

        }

        public AbstractViewer getViewer ()
        {

            return this.viewer;

        }

        public SideBar getSideBar ()
        {

            return this.sidebar;

        }

    }

}
