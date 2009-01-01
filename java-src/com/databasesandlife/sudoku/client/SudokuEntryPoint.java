/*
 * SudokuEntryPoint.java
 *
 * Created on December 24, 2008, 6:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.databasesandlife.sudoku.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Adrian Smith
 */
public class SudokuEntryPoint implements EntryPoint {

    /** Creates a new instance of SudokuEntryPoint */
    public SudokuEntryPoint() {
    }

    /** 
        The entry point method, called automatically by loading a module
        that declares an implementing class as an entry-point
    */
    public void onModuleLoad() {
        final Label label = new Label("Hello, GWT!!!");
        final Button button = new Button("Click me!");
        
        button.addClickListener(new ClickListener(){
            public void onClick(Widget w) {
                label.setVisible(!label.isVisible());
            }
        });
        
        RootPanel.get().add(button);
        RootPanel.get().add(label);
        
        Grid g = new Grid(9,9);
        g.setStylePrimaryName("sudoku");
        for (int y = 0; y < 9; y++) {
            if (y%3==0) g.getRowFormatter().setStylePrimaryName(y, "start");
            if (y==8)   g.getRowFormatter().setStylePrimaryName(y, "end");
            for (int x = 0; x < 9; x++) {
                TextBox t = new TextBox();
                t.setMaxLength(1);
                g.setWidget(x, y, t);
                if (x%3==0) g.getCellFormatter().setStylePrimaryName(y, x, "leftcol");
                if (x==8)   g.getCellFormatter().setStylePrimaryName(y, x, "rightcol");
            }
        }
        RootPanel.get().add(g);
    }
}
