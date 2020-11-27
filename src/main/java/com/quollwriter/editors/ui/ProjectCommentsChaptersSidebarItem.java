package com.quollwriter.editors.ui;

import java.util.*;
import java.util.function.*;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.*;

import com.quollwriter.data.*;
import com.quollwriter.*;
import com.quollwriter.ui.fx.components.*;
import com.quollwriter.ui.fx.sidebars.*;
import com.quollwriter.ui.fx.viewers.*;
import com.quollwriter.ui.fx.*;

import static com.quollwriter.LanguageStrings.*;
import static com.quollwriter.uistrings.UILanguageStringsManager.getUILanguageStringProperty;

// TODO: Make abstract ProjectSentReceivedChaptersAccordionItem, maybe move into ProjectSentReceivedSideBar.
public class ProjectCommentsChaptersSidebarItem extends ProjectObjectsSidebarItem<ProjectSentReceivedViewer>
{

    private QuollTreeView tree = null;
    private IntegerProperty countProp = null;
    private StringProperty title = null;

    public ProjectCommentsChaptersSidebarItem (ProjectSentReceivedViewer pv,
                                               StringProperty            title,
                                               IPropertyBinder           binder)
    {

        super (pv,
               binder);

        this.title = title;
        this.countProp = new SimpleIntegerProperty (0);

        this.tree = NamedObjectTree.builder ()
            .project (pv.getProject ())
            .root (this.createTree ())
            .withViewer (this.viewer)
            .contextMenuItemSupplier (obj ->
            {

                Set<MenuItem> its = new LinkedHashSet<> ();

                List<String> prefix = Arrays.asList (editors,projectcomments,sidebar,comments,treepopupmenu);

                if (obj instanceof Note)
                {

                    Note n = (Note) obj;

                    // Is this a project we have sent?
                    if (!this.viewer.getMessage ().isSentByMe ())
                    {

                        its.add (QuollMenuItem.builder ()
                            .label (getUILanguageStringProperty (Utils.newList (prefix,comment,items,(n.isDealtWith () ? undealtwith : dealtwith))))
                            .iconName (StyleClassNames.VIEW)
                            .onAction (ev ->
                            {

                                n.setDealtWith (n.isDealtWith () ? null : new Date ());

                            })
                            .build ());

                    }

                    its.add (QuollMenuItem.builder ()
                        .label (getUILanguageStringProperty (Utils.newList (prefix,comment,items,view)))
                        .iconName (StyleClassNames.VIEW)
                        .onAction (ev ->
                        {

                            this.viewer.viewObject (n);

                        })
                        .build ());

                }

                if (obj instanceof Chapter)
                {

                    Chapter c = (Chapter) obj;

                    String chapterObjTypeName = Environment.getObjectTypeName (c).getValue ();

                    // Is this a project we have sent?
                    if (!this.viewer.getMessage ().isSentByMe ())
                    {

                        int nc = 0;

                        final Set<Note> notes = c.getNotes ();

                        for (Note n : notes)
                        {

                            if (n.isDealtWith ())
                            {

                                nc++;

                            }

                        }

                        if (nc > 0)
                        {

                            its.add (QuollMenuItem.builder ()
                                .label (getUILanguageStringProperty (Utils.newList (prefix,Chapter.OBJECT_TYPE,items,undealtwith)))
                                .iconName (StyleClassNames.SETUNDEALTWITH)
                                .onAction (ev ->
                                {

                                    for (Note n : notes)
                                    {

                                        n.setDealtWith (null);

                                    }

                                })
                                .build ());

                        }

                        if (nc != notes.size ())
                        {

                            its.add (QuollMenuItem.builder ()
                                .label (getUILanguageStringProperty (Utils.newList (prefix,Chapter.OBJECT_TYPE,items,dealtwith)))
                                .iconName (StyleClassNames.SETDEALTWITH)
                                .onAction (ev ->
                                {

                                    Date d = new Date ();

                                    for (Note n : notes)
                                    {

                                        n.setDealtWith (d);

                                    }

                                })
                                .build ());

                        }

                    }

                    its.add (QuollMenuItem.builder ()
                        .label (getUILanguageStringProperty (Utils.newList (prefix,Chapter.OBJECT_TYPE,items,view)))
                        .iconName (StyleClassNames.EDIT)
                        .onAction (ev ->
                        {

                            this.viewer.editChapter (c,
                                                     null);

                        })
                        .build ());

                    its.add (QuollMenuItem.builder ()
                        .label (getUILanguageStringProperty (Utils.newList (prefix,Chapter.OBJECT_TYPE,items,close)))
                        .iconName (StyleClassNames.CLOSE)
                        .onAction (ev ->
                        {

                            this.viewer.closePanel (c,
                                                    null);

                        })
                        .build ());

                }

                return its;

            })
            .viewObjectOnClick (true)
            .build ();

    }

    private TreeItem<NamedObject> createTree ()
    {

        List<Chapter> objs = new ArrayList<> (this.viewer.getProject ().getBooks ().get (0).getChapters ());

        TreeItem<NamedObject> root = new TreeItem<> (this.viewer.getProject ());

        for (Chapter c : objs)
        {

            TreeItem<NamedObject> cit = new TreeItem<> (c);
            root.getChildren ().add (cit);

            c.getNotes ().stream ()
                .forEach (n -> cit.getChildren ().add (new TreeItem<> (n)));

        }

        this.countProp.setValue (objs.size ());

        return root;

    }

    @Override
    public Node getContent ()
    {

        return this.tree;

    }

    @Override
    public String getId ()
    {

        return "projectcommentschapters";

    }

    @Override
    public StringProperty getTitle ()
    {

        return this.title;

    }

    @Override
    public IntegerProperty getItemCount ()
    {

        return this.countProp;

    }

    @Override
    public Supplier<Set<MenuItem>> getHeaderContextMenuItemSupplier ()
    {

        return () ->
        {

            return new HashSet<> ();

        };

    }

    @Override
    public List<String> getStyleClassNames ()
    {

        return Arrays.asList (StyleClassNames.COMMENTS);

    }

    @Override
    public BooleanProperty showItemCountOnHeader ()
    {

        return new SimpleBooleanProperty (true);

    }

    @Override
    public boolean canImport (NamedObject o)
    {

        return false;

    }

    @Override
    public void importObject (NamedObject o)
    {

        // Do nothing.

    }

/*
TODO
    protected void handleViewObject (TreePath tp,
                                     Object   obj)
    {

        if (obj instanceof Chapter)
        {

            Chapter c = (Chapter) obj;

            if (c.getNotes ().size () > 0)
            {

                Note n = c.getNotes ().iterator ().next ();

                this.viewer.viewObject (n);

                this.tree.expandPath (UIUtils.getTreePathForUserObject ((DefaultMutableTreeNode) this.tree.getModel ().getRoot (),
                                                                        c));

                return;

            }

        }

        this.viewer.viewObject ((DataObject) obj);

    }
*/
/*
    @Override
    public int getViewObjectClickCount (Object d)
    {

        return 1;

    }

    @Override
    public boolean isAllowObjectPreview ()
    {

        return true;

    }

    @Override
    public boolean isTreeEditable ()
    {

        return false;

    }

    @Override
    public boolean isDragEnabled ()
    {

        return false;

    }
*/
}
