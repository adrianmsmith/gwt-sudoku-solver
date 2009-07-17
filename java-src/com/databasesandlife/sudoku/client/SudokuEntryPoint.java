package com.databasesandlife.sudoku.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Adrian Smith
 */
public class SudokuEntryPoint implements EntryPoint {

    public static class Link extends Widget {
        public Link(String url, Widget stuff) {
            AnchorElement e = Document.get().createAnchorElement();
            setElement(e);
            e.setHref(url);
            e.appendChild(stuff.getElement());
        }
    }

    /** Creates a new instance of SudokuEntryPoint */
    public SudokuEntryPoint() {
    }

    /** 
        The entry point method, called automatically by loading a module
        that declares an implementing class as an entry-point
    */
    public void onModuleLoad() {
        Grid g = new Grid(9,9);
        g.setStylePrimaryName("sudoku");
        for (int y = 0; y < 9; y++) {
            if (y%3==0) g.getRowFormatter().setStylePrimaryName(y, "start");
            if (y==8)   g.getRowFormatter().setStylePrimaryName(y, "end");
            for (int x = 0; x < 9; x++) {
                if (x == 1 && y == 1) {
                    FlowPanel p = new FlowPanel();   //  <div> 
                    p.add(new InlineLabel("text: "));
                    p.add(new Image("http://www.uboot.com/status-image/?nick=adi1011"));
                    Link link = new Link("http://www.uboot.com/", p);
                    g.setWidget(y, x, link);
                } else {
                    TextBox t = new TextBox();
                    t.setMaxLength(1);
                    g.setWidget(y, x, t);
                    if (x%3==0) g.getCellFormatter().setStylePrimaryName(y, x, "leftcol");
                    if (x==8)   g.getCellFormatter().setStylePrimaryName(y, x, "rightcol");
                }
            }
        }
        RootPanel.get().add(g);
        GWT.
    }
}
