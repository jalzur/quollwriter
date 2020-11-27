package com.quollwriter.ui.fx.popups;

import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.text.*;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import com.gentlyweb.utils.*;

import com.quollwriter.*;
import com.quollwriter.data.*;
import com.quollwriter.editors.*;
import com.quollwriter.ui.fx.*;
import com.quollwriter.ui.fx.components.*;
import com.quollwriter.ui.fx.viewers.*;

import static com.quollwriter.uistrings.UILanguageStringsManager.getUILanguageStringProperty;
import static com.quollwriter.LanguageStrings.*;

public class WordCountTimerSelectPopup extends PopupContent
{

    public static final String POPUP_ID = "wordcounttimerselect";

    private int wordCount = Constants.DEFAULT_WORDS;
    private int mins = Constants.DEFAULT_MINS;

    public WordCountTimerSelectPopup (AbstractViewer               viewer,
                                      BiConsumer<Integer, Integer> onStart)
    {

        super (viewer);

        HBox b = new HBox ();

        ChoiceBox<StringProperty> words = UIUtils.getWordsOptions (() -> { return Constants.DEFAULT_WORDS; },
                                                                   (v) -> { this.wordCount = v; });
        ChoiceBox<StringProperty> times = UIUtils.getTimeOptions (() -> {return Constants.DEFAULT_MINS; },
                                                                  (v) -> { this.mins = v; });
        QuollLabel l = QuollLabel.builder ()
            .label (timer,labels,andor)
            .build ();

        QuollButton but = QuollButton.builder ()
            .tooltip (timer,buttons,start,tooltip)
            .iconName (StyleClassNames.START)
            .onAction (ev ->
            {

                onStart.accept (this.mins,
                                this.wordCount);

            })
            .build ();

        b.getChildren ().addAll (words, l, times, but);

        this.getChildren ().add (b);

    }

    @Override
    public QuollPopup createPopup ()
    {

        QuollPopup p = QuollPopup.builder ()
            .title (timer,LanguageStrings.popup,title)
            .styleClassName (StyleClassNames.TIMER)
            .styleSheet (StyleClassNames.TIMER)
            .hideOnEscape (true)
            .withClose (true)
            .content (this)
            .popupId (POPUP_ID)
            .withViewer (this.viewer)
            .build ();

        p.requestFocus ();

        return p;

    }

}
