package com.quollwriter.ui.fx.panels;

import java.io.*;

import java.text.*;

import java.util.*;

import org.josql.*;

import com.gentlyweb.utils.*;
//import com.gentlyweb.properties.*;

import com.quollwriter.*;
import com.quollwriter.data.*;
import com.quollwriter.data.editors.*;
import com.quollwriter.editors.*;
import com.quollwriter.data.comparators.*;
import com.quollwriter.ui.fx.*;
import com.quollwriter.ui.fx.viewers.*;
import com.quollwriter.ui.fx.components.*;

import com.quollwriter.uistrings.*;

import javafx.beans.property.*;
import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.application.*;

import com.quollwriter.db.*;

import static com.quollwriter.LanguageStrings.*;
import static com.quollwriter.uistrings.UILanguageStringsManager.getUILanguageStringProperty;
import static com.quollwriter.uistrings.UILanguageStringsManager.getUIString;

public class ProjectsPanel<E extends AbstractViewer> extends PanelContent<E>
{

    public static final String STATUS_TAG = "{s}";
	public static final String WORDS_TAG = "{wc}";
	public static final String CHAPTERS_TAG = "{ch}";
	public static final String LAST_EDITED_TAG = "{le}";
	public static final String EDIT_COMPLETE_TAG = "{ec}";
	public static final String READABILITY_TAG = "{r}";
	public static final String EDITOR_TAG = "{ed}";

    public static final String PANEL_ID = "allprojects";

    private SimpleStringProperty titleProp = null;

    private FlowPane tiles = null;

    public ProjectsPanel (E viewer)
    {

        super (viewer);

        this.titleProp = new SimpleStringProperty (this, "title");

        this.titleProp.bind (Bindings.createStringBinding (() ->
        {

            return String.format (getUIString (LanguageStrings.allprojects, LanguageStrings.title), Environment.formatNumber (Environment.allProjectsProperty ().size ()));

        },
        Environment.uilangProperty (),
        Environment.allProjectsProperty ()));

        this.tiles = new FlowPane ();

        this.fillProjects ();

        ScrollPane tsp = new ScrollPane ();
		tsp.setContent (this.tiles);

        this.getChildren ().add (tsp);

    }

    @Override
    public Panel createPanel ()
    {

        StringProperty t = new SimpleStringProperty (this, "title");

        t.bind (Bindings.createStringBinding (() ->
        {

            return String.format (getUIString (allprojects,title), Environment.formatNumber (Environment.allProjectsProperty ().size ()));

        },
        UILanguageStringsManager.uilangProperty (),
        Environment.allProjectsProperty ()));

        Panel panel = Panel.builder ()
            .title (t)
            .content (this)
            .styleClassName (StyleClassNames.ALLPROJECTS)
            .panelId (PANEL_ID)
            // TODO .headerControls ()
            .build ();

        return panel;

    }

    @Override
    public void init (State state)
               throws GeneralException
    {

    }

    private void showRemoveProject (final ProjectInfo p,
									final Runnable    onRemove)
    {
/*
        java.util.List<String> prefix = new ArrayList<> ();
        prefix.add (LanguageStrings.allprojects);
        prefix.add (LanguageStrings.actions);
        prefix.add (LanguageStrings.removeproject);

        final Landing _this = this;

        Map<String, ActionListener> buts = new LinkedHashMap ();
        buts.put (Environment.getUIString (prefix,
                                           LanguageStrings.popup,
                                           LanguageStrings.buttons,
                                           LanguageStrings.confirm),
                //"Yes, remove it",
                  new ActionListener ()
                  {

                     public void actionPerformed (ActionEvent ev)
                     {

                        try
                        {

							Environment.deleteProject (p,
													   onRemove);

                        } catch (Exception e) {

                            Environment.logError ("Unable to remove project: " +
                                                  p.getName (),
                                                  e);

                            UIUtils.showErrorMessage (_this,
                                                      Environment.getUIString (prefix,
                                                                               LanguageStrings.actionerror));
                                                      //"Unable to remove project, please contact Quoll Writer support for assistance.");

                            return;

                        }

                     }

                  });

        buts.put (Environment.getUIString (prefix,
                                           LanguageStrings.popup,
                                           LanguageStrings.buttons,
                                           LanguageStrings.cancel),
                //"No, keep it",
                  new ActionListener ()
                  {

                     public void actionPerformed (ActionEvent ev)
                     {

                        // Don't do anything...

                     }

                  });

        String reason = Environment.canOpenProject (p);

		String message = String.format (Environment.getUIString (prefix,
                                                                 LanguageStrings.popup,
                                                                 LanguageStrings.text),
                                        //"Sorry, {project} <b>%s</b> cannot be opened for the following reason:<br /><br /><b>%s</b><br /><br />This can happen if your projects file gets out of sync with your hard drive, for example if you have re-installed your machine or if you are using a file syncing service.<br /><br />Do you want to remove it from your list of {projects}?",
                                        p.getName (),
                                        reason);

        //message = message + "<br /><br />Note: this will <b>only</b> remove the {project} from the list it will not remove any other data.";

        JComponent mess = UIUtils.createHelpTextPane (message,
                                                      null);
        mess.setBorder (null);
        mess.setSize (new Dimension (UIUtils.getPopupWidth () - 20,
                                     500));

        UIUtils.createQuestionPopup (this,
                                     Environment.getUIString (prefix,
                                                              LanguageStrings.popup,
                                                              LanguageStrings.title),
                                    //"Unable to open {project}",
                                     Constants.ERROR_ICON_NAME,
                                     mess,
                                     buts,
                                     null,
                                     null);
*/
    }

    private boolean handleOpenProject (final ProjectInfo p,
									   final Runnable    onOpen)
    {

        StringProperty reason = Environment.canOpenProject (p);

        if (reason != null)
        {

            this.showRemoveProject (p,
									null);

            return false;

        }

		if (p.isOpening ())
		{

			return false;

		}

		// Is the project already open?
		AbstractViewer pv = Environment.getProjectViewer (p);

		if (pv != null)
		{

            pv.toFront ();
            pv.setIconified (false);

			return true;

		}

		final ProjectsPanel _this = this;

		Runnable _onOpen = new Runnable ()
		{

			@Override
			public void run ()
			{

				if (UserProperties.getAsBoolean (Constants.CLOSE_PROJECTS_WINDOW_WHEN_PROJECT_OPENED_PROPERTY_NAME))
				{

                    // TODO _this.close (true, null);

				}

				if (onOpen != null)
				{

                    Platform.runLater (onOpen);

				}

			}

		};

        try
        {

            Environment.openProject (p,
						 			  _onOpen);

            return true;

        } catch (Exception e)
        {

            // Check for encryption.
            if ((ObjectManager.isEncryptionException (e))
                &&
                (!p.isEncrypted ())
               )
            {

                // Try with no credentials.
                try
                {

                    p.setNoCredentials (true);

					this.handleOpenProject (p,
											_onOpen);

                    return true;

                } catch (Exception ee) {

                    p.setNoCredentials (false);

                    // Check for encryption.
                    if (ObjectManager.isEncryptionException (e))
                    {

                        p.setEncrypted (true);

                        this.handleOpenProject (p,
												_onOpen);

                        return true;

                    }

                    Environment.logError ("Unable to open project: " +
                                          p.getName (),
                                          ee);

                  java.util.List<String> prefix = new ArrayList<> ();
                  prefix.add (LanguageStrings.project);
                  prefix.add (LanguageStrings.actions);
                  prefix.add (LanguageStrings.openproject);
                  prefix.add (LanguageStrings.openerrors);

                  ComponentUtils.showErrorMessage (this,
                                                   getUILanguageStringProperty (Utils.newList (prefix,general),
                                                                                            p.getName (),
                                                                                            getUILanguageStringProperty (prefix,unspecified)));
/*
TODO Remove
                                                   String.format (Environment.getUIString (prefix,
                                                                                    LanguageStrings.general),
                                                                     //"Unable to open project: " +
                                                           p.getName (),
                                                           Environment.getUIString (prefix,
                                                                                    LanguageStrings.unspecified)));
*/
                  return false;

                }

            }

            Environment.logError ("Unable to open project: " +
                                  p.getName (),
                                  e);

          java.util.List<String> prefix = new ArrayList ();
          prefix.add (LanguageStrings.project);
          prefix.add (LanguageStrings.actions);
          prefix.add (LanguageStrings.openproject);
          prefix.add (LanguageStrings.openerrors);

          ComponentUtils.showErrorMessage (this,
                                           getUILanguageStringProperty (Utils.newList (prefix,general),
                                                                        p.getName (),
                                                                        getUILanguageStringProperty (prefix,unspecified)));

        }

        return false;

    }

    private void fillProjects ()
	{

		// Get how we should sort.
		String sortBy = UserProperties.get (Constants.SORT_PROJECTS_BY_PROPERTY_NAME);

		if (sortBy == null)
		{

			sortBy = "ifThenElse (lastEdited = null, 0, lastEdited) DESC, name";

		}

		if (sortBy.equals ("lastEdited"))
		{

			sortBy = "ifThenElse (lastEdited = null, 0, lastEdited) DESC, name, status";

		}

		if (sortBy.equals ("status"))
		{

			sortBy = "status, ifThenElse (lastEdited = null, 0, lastEdited) DESC, name";

		}

		if (sortBy.equals ("name"))
		{

			sortBy = "name, ifThenElse (lastEdited = null, 0, lastEdited) DESC, status";

		}

		if (sortBy.equals ("wordCount"))
		{

			sortBy = "wordCount DESC, ifThenElse (lastEdited = null, 0, lastEdited) DESC, name, status";

		}

		java.util.List<ProjectInfo> infos = null;

		try
		{

			infos = new ArrayList<> (Environment.allProjectsProperty ().getValue ());

		} catch (Exception e) {

			Environment.logError ("Unable to get all project infos",
								  e);

			ComponentUtils.showErrorMessage (this,
                                             getUILanguageStringProperty (allprojects,actionerror));
									         //"Unable to get {project} information, please contact Quoll Writer support for assistance.");

			return;

		}

		try
		{

			String sql = String.format ("SELECT * FROM %s ORDER BY %s",
										ProjectInfo.class.getName (),
										sortBy);

			Query q = new Query ();

			q.parse (sql);

			QueryResults qr = q.execute (infos);

			infos = (java.util.List<ProjectInfo>) qr.getResults ();

		} catch (Exception e) {

			Collections.sort (infos,
							  new ProjectInfoSorter ());

		}

		this.tiles.getChildren ().clear ();

		for (ProjectInfo p : infos)
		{

			ProjectBox pb = new ProjectBox (p,
											this);

			pb.setMaxHeight (Control.USE_PREF_SIZE);
			pb.getStyleClass ().add ("project");

			this.tiles.getChildren ().add (pb);

		}
		/*
		if (this.currentCard != null)
		{

			if ((!NO_PROJECTS_CARD.equals (this.currentCard))
				&&
				(!MAIN_CARD.equals (this.currentCard))
			   )
			{

				return;

			}

		}

		if (infos.size () == 0)
		{

			this.showCard (NO_PROJECTS_CARD);

		} else {

			this.showMainCard ();

		}
		*/
	}

    @Override
    public State getState ()
    {

        return super.getState ();

    }

    public void fillToolBar (ToolBar toolBar,
                             boolean  fullScreen)
    {

    }

  public class ProjectBox extends VBox
  {

		private ProjectInfo project = null;
		private ProjectsPanel parent = null;
		private Label name = null;
		private Label info = null;
		private boolean interactive = true;

		public ProjectInfo getProjectInfo ()
		{

			return this.project;

		}

		public void setInteractive (boolean v)
		{

			this.interactive = v;

		}

      public ProjectBox (ProjectInfo   p,
						 ProjectsPanel parent)
      {

          super (0);

			this.parent = parent;

			final ProjectBox _this = this;

			this.project = p;

			this.name = new Label ("");

            this.name.textProperty ().bind (new SimpleStringProperty (this.project.getName ()));

            //TODO this.project.nameProperty());
			this.name.getStyleClass ().add ("name");
            this.name.getStyleClass ().add (this.getIconName ());

			this.info = new Label ("");
            this.info.textProperty ().bind (Bindings.createStringBinding (() ->
            {

                return this.getFormattedProjectInfo ();

            },
            UILanguageStringsManager.uilangProperty (),
            UserProperties.projectInfoFormatProperty (),
            // TODO: Add other props.
            this.project.nameProperty ()));
			this.info.getStyleClass ().add ("info");

			this.getChildren ().addAll (this.name, this.info);

            this.setOnMouseEntered (ev ->
			{

				StringProperty tip = null;
/*
TODO
				tip = Environment.canOpenProject (_this.project);

				if (tip != null)
				{

					tip = "This {project} cannot be opened for the following reason:<br /><br />" + tip + "<br /><br />Right click to remove this from your list of {projects}.";

				}

				if ((tip == null)
					&&
					(_this.project.isEncrypted ())
				   )
				{

					tip = "This {project} is encrypted and needs a password to access it.  Click to open it.";

				}

				if ((tip == null)
					&&
					(_this.project.isEditorProject ())
				   )
				{

					EditorEditor ed = _this.project.getForEditor ();

					if (ed != null)
					{

						String name = ed.getMainName ();

						ed = EditorsEnvironment.getEditorByEmail (ed.getEmail ());

						if (ed != null)
						{

							name = ed.getShortName ();

						}

						tip = String.format ("You are editing this {project} for <b>%s</b>.  Click to open it.",
											 name);

					}

				}

				if ((tip == null)
					&&
					(_this.project.isWarmupsProject ())
				   )
				{

					tip = "This is your {warmups} {project}.  Click to open it.";

				}

				if (tip == null)
				{

					tip = "Click to open the {project}.";

				}

				Tooltip.install (_this,
								 new Tooltip (String.format ("<html>%s</html>",
															 tip)));//Environment.replaceObjectNames (tip))));
*/
			});

            this.setOnMouseClicked (ev ->
			{

				if (ev.isPopupTrigger ())
				{

					ContextMenu cm = new ContextMenu ();

					MenuItem m = null;

					StringProperty reason = Environment.canOpenProject (_this.project);

					if (reason != null)
					{

						m = new MenuItem ("Remove {project}");
						cm.getItems ().add (m);
						m.setOnAction ((mev) ->
						{

/*
								_this.parent.showRemoveProject (_this.project,
																new ActionListener ()
								{

									public void actionPerformed (ActionEvent ev)
									{

										_this.parent.remove (_this);

										_this.parent.validate ();

										_this.parent.repaint ();

									}

								});
								*/

						});

					} else {

						m = new MenuItem ("Open");
						cm.getItems ().add (m);
						m.setOnAction ((mev) ->
						{

							_this.parent.handleOpenProject (_this.project,
															null);

						});

						Menu mm = new Menu ("Set Status");

						cm.getItems ().add (mm);
/*
						// Get the project statuses.
						Set<String> statuses = Environment.getUserPropertyHandler (Constants.PROJECT_STATUSES_PROPERTY_NAME).getTypes ();

						for (String s : statuses)
						{

							m.add (_this.createStatusMenuItem (s));

						}

						m.add (_this.createStatusMenuItem (null));

						m.add (UIUtils.createMenuItem ("Add a new Status",
													   Constants.EDIT_ICON_NAME,
													   new ActionListener ()
													   {

															public void actionPerformed (ActionEvent ev)
															{

																_this.showAddNewProjectStatus ();

															}

														}));
								*/
						cm.getItems ().add (new SeparatorMenuItem ());
															/*
						if (!_this.project.isEncrypted ())
						{

							popup.add (UIUtils.createMenuItem ("Create a Backup",
															   Constants.SNAPSHOT_ICON_NAME,
															   new ActionListener ()
															   {

																	public void actionPerformed (ActionEvent ev)
																	{

																		UIUtils.showCreateBackup (_this.project,
																								  null,
																								  _this.parent);

																	}

																}));

						}
								*/
															/*
						popup.add (UIUtils.createMenuItem ("Manage Backups",
														   Constants.EDIT_ICON_NAME,
														   new ActionListener ()
														   {

																public void actionPerformed (ActionEvent ev)
																{

																	UIUtils.showManageBackups (_this.project,
																							   _this.parent);

																}

															}));

						popup.addSeparator ();

						popup.add (UIUtils.createMenuItem ("Show Folder",
														   Constants.FOLDER_ICON_NAME,
														   new ActionListener ()
														   {

																public void actionPerformed (ActionEvent ev)
																{

																	UIUtils.showFile (null,
																					  _this.project.getProjectDirectory ());

																}

															}));

						popup.add (UIUtils.createMenuItem ("Delete",
														   Constants.DELETE_ICON_NAME,
														   new ActionListener ()
														   {

																public void actionPerformed (ActionEvent ev)
																{

																	_this.parent.showDeleteProject (_this.project,
																									new ActionListener ()
																	{

																		public void actionPerformed (ActionEvent ev)
																		{

																			_this.parent.remove (_this);

																			_this.parent.validate ();

																			_this.parent.repaint ();

																		}

																	});

																}

															}));
						*/
					}

					cm.show (_this, ev.getScreenX (), ev.getScreenY ());

					return;

				}

				try
				{

					_this.parent.handleOpenProject (_this.project,
													null);

				} catch (Exception e) {

					Environment.logError ("Unable to open project: " +
										  _this.project,
										  e);
/*
					UIUtils.showErrorMessage (_this.parent,
											  "Unable to open {project}, please contact Quoll Writer support for assistance.");
*/
				}

			});
			/*
			this.add (new JLayer<JComponent> (this.content, new LayerUI<JComponent> ()
			{

				public String getToolTipText()
				{

					if (!_this.interactive)
					{

						return null;

					}

					//final ProjectBox _this = this;

					String tip = null;

					tip = Environment.canOpenProject (_this.project);

					if (tip != null)
					{

						tip = "This {project} cannot be opened for the following reason:<br /><br />" + tip + "<br /><br />Right click to remove this from your list of {projects}.";

					}

					if ((tip == null)
						&&
						(_this.project.isEncrypted ())
					   )
					{

						tip = "This {project} is encrypted and needs a password to access it.  Click to open it.";

					}

					if ((tip == null)
						&&
						(_this.project.isEditorProject ())
					   )
					{

						EditorEditor ed = _this.project.getForEditor ();

						if (ed != null)
						{

							String name = ed.getMainName ();

							ed = EditorsEnvironment.getEditorByEmail (ed.getEmail ());

							if (ed != null)
							{

								name = ed.getShortName ();

							}

							tip = String.format ("You are editing this {project} for <b>%s</b>.  Click to open it.",
												 name);

						}

					}

					if ((tip == null)
						&&
						(_this.project.isWarmupsProject ())
					   )
					{

						tip = "This is your {warmups} {project}.  Click to open it.";

					}

					if (tip == null)
					{

						tip = "Click to open the {project}.";

					}

					return String.format ("<html>%s</html>",
										  Environment.replaceObjectNames (tip));

				}

				@Override
				public void installUI(JComponent c) {
					super.installUI(c);
					// enable mouse motion events for the layer's subcomponents
					((JLayer) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK);
				}

				@Override
				public void uninstallUI(JComponent c) {
					super.uninstallUI(c);
					// reset the layer event mask
					((JLayer) c).setLayerEventMask(0);
				}

				@Override
				public void processMouseEvent (MouseEvent                   ev,
											   JLayer<? extends JComponent> l)
				{

					if (_this.interactive)
					{

						l.getView ().setToolTipText (this.getToolTipText ());

						if (ev.getID () == MouseEvent.MOUSE_RELEASED)
						{

							if (ev.isPopupTrigger ())
							{

								ev.consume ();

								JPopupMenu popup = new JPopupMenu ();

								String reason = Environment.canOpenProject (_this.project);

								if (reason != null)
								{

									popup.add (UIUtils.createMenuItem ("Remove {project}",
																	   Constants.ERROR_ICON_NAME,
																	   new ActionListener ()
																	   {

																			public void actionPerformed (ActionEvent ev)
																			{

																				_this.parent.showRemoveProject (_this.project,
																												new ActionListener ()
																				{

																					public void actionPerformed (ActionEvent ev)
																					{

																						_this.parent.remove (_this);

																						_this.parent.validate ();

																						_this.parent.repaint ();

																					}

																				});

																			}

																		}));

								} else {

									popup.add (UIUtils.createMenuItem ("Open",
																	   Constants.OPEN_PROJECT_ICON_NAME,
																	   new ActionListener ()
																	   {

																			public void actionPerformed (ActionEvent ev)
																			{

																				_this.parent.handleOpenProject (_this.project,
																												null);

																			}

																		}));

									JMenu m = new JMenu ("Set Status");

									popup.add (m);

									// Get the project statuses.
									Set<String> statuses = Environment.getUserPropertyHandler (Constants.PROJECT_STATUSES_PROPERTY_NAME).getTypes ();

									for (String s : statuses)
									{

										m.add (_this.createStatusMenuItem (s));

									}

									m.add (_this.createStatusMenuItem (null));

									m.add (UIUtils.createMenuItem ("Add a new Status",
																   Constants.EDIT_ICON_NAME,
																   new ActionListener ()
																   {

																		public void actionPerformed (ActionEvent ev)
																		{

																			_this.showAddNewProjectStatus ();

																		}

																	}));

									popup.addSeparator ();

									if (!_this.project.isEncrypted ())
									{

										popup.add (UIUtils.createMenuItem ("Create a Backup",
																		   Constants.SNAPSHOT_ICON_NAME,
																		   new ActionListener ()
																		   {

																				public void actionPerformed (ActionEvent ev)
																				{

																					UIUtils.showCreateBackup (_this.project,
																											  null,
																											  _this.parent);

																				}

																			}));

									}

									popup.add (UIUtils.createMenuItem ("Manage Backups",
																	   Constants.EDIT_ICON_NAME,
																	   new ActionListener ()
																	   {

																			public void actionPerformed (ActionEvent ev)
																			{

																				UIUtils.showManageBackups (_this.project,
																										   _this.parent);

																			}

																		}));

									popup.addSeparator ();

									popup.add (UIUtils.createMenuItem ("Show Folder",
																	   Constants.FOLDER_ICON_NAME,
																	   new ActionListener ()
																	   {

																			public void actionPerformed (ActionEvent ev)
																			{

																				UIUtils.showFile (null,
																								  _this.project.getProjectDirectory ());

																			}

																		}));

									popup.add (UIUtils.createMenuItem ("Delete",
																	   Constants.DELETE_ICON_NAME,
																	   new ActionListener ()
																	   {

																			public void actionPerformed (ActionEvent ev)
																			{

																				_this.parent.showDeleteProject (_this.project,
																												new ActionListener ()
																				{

																					public void actionPerformed (ActionEvent ev)
																					{

																						_this.parent.remove (_this);

																						_this.parent.validate ();

																						_this.parent.repaint ();

																					}

																				});

																			}

																		}));

								}

								popup.show (_this,
											ev.getX (),
											ev.getY ());

								return;

							} else {

								try
								{

									_this.parent.handleOpenProject (_this.project,
																	null);

								} catch (Exception e) {

									Environment.logError ("Unable to open project: " +
														  _this.project,
														  e);

									UIUtils.showErrorMessage (_this.parent,
															  "Unable to open {project}, please contact Quoll Writer support for assistance.");

								}

							}

							return;

						}

					}

					if (ev.getID () == MouseEvent.MOUSE_EXITED)
					{

						_this.setBorder (UIUtils.createPadding (1, 1, 1, 1));

						_this.showBackground = false;

						_this.validate ();

						_this.repaint ();

						return;

					}

					if (ev.getID () == MouseEvent.MOUSE_ENTERED)
					{

						_this.setBorder (new LineBorder (UIUtils.getBorderColor (), 1, false));
						_this.showBackground = true;

						_this.validate ();

						_this.repaint ();

						return;

					}

				}

			}));
			*/
			/*
			this.icon.setAlignmentX (Component.LEFT_ALIGNMENT);
			this.icon.setAlignmentY (Component.TOP_ALIGNMENT);
			this.icon.setBorder (UIUtils.createPadding (5, 0, 0, 0));

			Box detail = new Box (BoxLayout.Y_AXIS);
			detail.setAlignmentX (Component.LEFT_ALIGNMENT);
			detail.setAlignmentY (Component.TOP_ALIGNMENT);

			this.content.add (this.icon);
			this.content.add (Box.createHorizontalStrut (5));
			this.content.add (detail);

          //Header h = new Header ();
          //h.setTitle (p.getName ());
          this.name.setAlignmentX (Component.LEFT_ALIGNMENT);
          this.name.setAlignmentY (Component.TOP_ALIGNMENT);
			this.name.setFont (this.name.getFont ().deriveFont (UIUtils.getScaledFontSize (14)).deriveFont (Font.PLAIN));
          //h.setPaintProvider (null);
          this.name.setForeground (UIUtils.getTitleColor ());
			this.name.setBorder (UIUtils.createPadding (0, 0, 0, 0));
			detail.add (this.name);

			this.content.setBorder (UIUtils.createPadding (3, 5, 5, 5));

			this.info.setBorder (UIUtils.createPadding (2, 0, 0, 5));
			this.info.setAlignmentX (Component.LEFT_ALIGNMENT);
			this.info.setAlignmentY (Component.TOP_ALIGNMENT);

			detail.add (this.info);

			this.update ();

			this.project.addPropertyChangedListener (this);
			*/
      }

      private String getIconName ()
      {

          String n = Project.OBJECT_TYPE;

          if (!this.project.getProjectDirectory ().exists ())
          {

              // Return a problem icon.
              n = Constants.ERROR_ICON_NAME;

              return n;

          }

          if (this.project.isEncrypted ())
          {

              // Return the lock icon.
              n = Constants.LOCK_ICON_NAME;

          }

          if (this.project.isEditorProject ())
          {

              // Return an editor icon.
              n = Constants.EDITORS_ICON_NAME;

          }

          if (this.project.isWarmupsProject ())
          {

              // Return an editor icon.
              n = Constants.WARMUPS_ICON_NAME;

          }

          return n;

      }

        public String getFormattedProjectInfo ()
		{

            java.util.List<String> prefix = new ArrayList ();
            prefix.add (LanguageStrings.allprojects);
            prefix.add (LanguageStrings.project);
            prefix.add (LanguageStrings.view);
            prefix.add (LanguageStrings.labels);

			String lastEd = "";

			if (project.getLastEdited () != null)
			{

				lastEd = String.format (getUIString (Utils.newList (prefix, LanguageStrings.lastedited)),
                                        //"Last edited: %s",
										Environment.formatDate (project.getLastEdited ()));

			} else {

				lastEd = getUIString (Utils.newList (prefix, LanguageStrings.notedited));
                                                //"Not yet edited.";

			}

			String text = UserProperties.projectInfoFormatProperty ().getValue ();

			String nl = String.valueOf ('\n');

			while (text.endsWith (nl))
			{

				text = text.substring (0,
									   text.length () - 1);

			}

			text = text.toLowerCase ();

			text = StringUtils.replaceString (text,
											  " ",
											  "&nbsp;");
			text = StringUtils.replaceString (text,
											  nl,
											  "<br />");

			text = StringUtils.replaceString (text,
											  STATUS_TAG,
											  (this.project.getStatus () != null ? this.project.getStatus () : getUIString (LanguageStrings.project,status,novalue)));
                                              //"No status"));

			text = StringUtils.replaceString (text,
											  WORDS_TAG,
											  String.format (getUIString (prefix, LanguageStrings.words),
                                                            //"%s words",
															 Environment.formatNumber (this.project.getWordCount ())));

            text = StringUtils.replaceString (text,
                                              CHAPTERS_TAG,
                                              String.format (getUIString (prefix, LanguageStrings.chapters),
                                                //"%s ${objectnames.%s.chapter}",
                                                             Environment.formatNumber (this.project.getChapterCount ())));

			text = StringUtils.replaceString (text,
											  LAST_EDITED_TAG,
											  lastEd);
			text = StringUtils.replaceString (text,
											  EDIT_COMPLETE_TAG,
											  String.format (getUIString (prefix, LanguageStrings.editcomplete),
                                                            //"%s%% complete",
															 Environment.formatNumber (Utils.getPercent (this.project.getEditedWordCount (), project.getWordCount ()))));
			text = StringUtils.replaceString (text,
											  READABILITY_TAG,
											  String.format (getUIString (prefix, LanguageStrings.readability),
                                                            //"GL: %s, RE: %s, GF: %s",
															 Environment.formatNumber (Math.round (this.project.getFleschKincaidGradeLevel ())),
															 Environment.formatNumber (Math.round (this.project.getFleschReadingEase ())),
															 Environment.formatNumber (Math.round (this.project.getGunningFogIndex ()))));

			return text;

		}

    }

}
