package com.databasesandlife.sudoku.client;

import com.databasesandlife.sudoku.client.SudokuSolver.Result;
import com.databasesandlife.sudoku.client.config.SudokuConfig;
import com.databasesandlife.sudoku.client.icons.SudokuIcons;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates and manages the Sudoku table.
 *
 * <p>Host page must have a [div id="sudokucontainer"]
 *
 * <p>
 * Layout is:
 * <ul>
 *   <li>[tr class=statusrow] with a [span class=error] or [span class=success]
 *   <li>9x [tr class=sudokurow].
 *        In data entry state, all cells are [input].
 *        In result display state, all cells are [span class=result] or [span class=userentered]
 *   <li>[tr] with a [td class=links] (clear board etc. and a [td class=submit] (calc. submit button)
 * </ul>
 *
 * <p>
 * Glossary:
 * <ul>
 *   <li>"y" is the vertical coordinate in the Sudoku i.e. 0 is the highest row
 *   <li>"row" is the vertical coordinate in the HTML table i.e. 0 is the message row, 1 is the first sudoku row, etc.
 * </ul>
 *
 * @author Adrian Smith
 */
public class SudokuPanelController implements EntryPoint {

    enum CalculateContainerState { calculate, editAgain };

    enum MessageType {
        success { AbstractImagePrototype getImg() { return ((SudokuIcons) GWT.create(SudokuIcons.class)).success(); } },
        error   { AbstractImagePrototype getImg() { return ((SudokuIcons) GWT.create(SudokuIcons.class)).error();   } };

        abstract AbstractImagePrototype getImg();
    };

    protected FlexTable table;
    protected int firstSudokuRow;
    protected SimplePanel messageContainer;
    protected TextBox[][] gridFields = new TextBox[9][9];  // x,y
    protected DeckPanel calculateButtonContainer;

    // ----------------------------------------------------------------------------------------------------------------
    // Generation of UI
    // ----------------------------------------------------------------------------------------------------------------

    protected Widget newGridModificationLinks() {
        FlowPanel result = new FlowPanel();

        InlineHyperlink clearGridLink = new InlineHyperlink("Clear grid", "");
        clearGridLink.addClickHandler(new ClickHandler() { public void onClick(ClickEvent event) { clearGridClicked(); } });
        result.add(clearGridLink);

        result.add(new InlineLabel(" | "));

        InlineHyperlink loadExampleLink = new InlineHyperlink("Load example", "");
        loadExampleLink.addClickHandler(new ClickHandler() { public void onClick(ClickEvent event) { loadExampleClicked(); } });
        result.add(loadExampleLink);

        return result;
    }

    protected DeckPanel newCalculateButtonContainer() {
        Button calculateButton = new Button("Calculate", new ClickHandler() {
            public void onClick(ClickEvent event) { calculateClicked(); } });

        InlineHyperlink toEditButton = new InlineHyperlink("\u00AB Edit again", "");   // \u00AB is "<<"
        toEditButton.addClickHandler(new ClickHandler() { public void onClick(ClickEvent event) { toEditClicked(); } });

        DeckPanel result = new DeckPanel();
        result.add(calculateButton);
        result.add(toEditButton);
        return result;
    }

    protected void selectCell(final int x, final int y) {
        final TextBox newField = gridFields[x][y];
        // if (newField.getText().length() > 0)   // the IF is an IE hack; if text field is empty then selectAll simply loses the focus
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    newField.setFocus(true);
                    newField.selectAll();
                }
            });
    }

    protected KeyDownHandler newCellKeyDownHandler(final int x, final int y) {
        return new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_UP:    if (y > 0) selectCell(x, y-1); break;
                    case KeyCodes.KEY_DOWN:  if (y < 8) selectCell(x, y+1); break;
                    case KeyCodes.KEY_LEFT:  if (x > 0) selectCell(x-1, y); break;
                    case KeyCodes.KEY_RIGHT: if (x < 8) selectCell(x+1, y); break;
                    case KeyCodes.KEY_ENTER: calculateClicked(); break;
                }
            }
        };
    }

    protected ClickHandler newTableCellClickHandler() {
        return new ClickHandler() {
            public void onClick(ClickEvent event) {
                Cell cell = table.getCellForEvent(event);
                int y = cell.getRowIndex() - firstSudokuRow;
                if (y >= 0 && y < 9) {
                    convertTableToDataEntry();
                    selectCell(cell.getCellIndex(), y);
                }
            }
        };
    }

    public SudokuPanelController() {
        table = new FlexTable();
        table.setStylePrimaryName("sudoku");

        // Create message container
        int row = 0;
        table.getFlexCellFormatter().setColSpan(row, 0, 9);
        table.getCellFormatter().setStylePrimaryName(row, 0, "statusrow");
        table.setWidget(row, 0, messageContainer = new SimplePanel());

        // Create table with grid element containers
        firstSudokuRow = row + 1;
        for (int y = 0; y < 9; y++) {
            row++;
            if (y%3==0) table.getRowFormatter().setStylePrimaryName(row, "start");
            if (y==8)   table.getRowFormatter().setStylePrimaryName(row, "end");
            table.getRowFormatter().addStyleName(row, "sudokurow");
            for (int x = 0; x < 9; x++) {
                gridFields[x][y] = new TextBox();
                gridFields[x][y].setStylePrimaryName("number");
                gridFields[x][y].setMaxLength(1);
                gridFields[x][y].addKeyDownHandler(newCellKeyDownHandler(x, y));
                table.setWidget(row, x, new InlineLabel());  // JIT expand table if necessary, so that setStyle(row,x) doesn't throw
                if (x%3==0) table.getCellFormatter().setStylePrimaryName(row, x, "leftcol");
                if (x == 8) table.getCellFormatter().setStylePrimaryName(row, x, "rightcol");
            }
        }
        table.addClickHandler(newTableCellClickHandler());

        // Create grid modification links
        row++;
        table.getFlexCellFormatter().setColSpan(row, 0, 6);
        table.getFlexCellFormatter().setStylePrimaryName(row, 0, "submitinfo");
        table.setWidget(row, 0, newGridModificationLinks());

        // Create calculate button & container
        table.getFlexCellFormatter().setColSpan(row, 1, 3);
        table.getFlexCellFormatter().setStylePrimaryName(row, 1, "submit");
        table.setWidget(row, 1, calculateButtonContainer = newCalculateButtonContainer());
        
        convertTableToDataEntry();
    }

    protected void convertTableToDataEntry() {
//        // IE hack: on the click event, if we swap in the data (that's already swapped in), text field loses focus
//        if (calculateButtonContainer.getVisibleWidget() == CalculateContainerState.calculate.ordinal()) return;

        calculateButtonContainer.showWidget(CalculateContainerState.calculate.ordinal());
        messageContainer.setWidget(new InlineLabel());
        for (int y = 0; y < 9; y++) 
            for (int x = 0; x < 9; x++) 
                table.setWidget(firstSudokuRow+y, x, gridFields[x][y]);
    }

    protected void convertTableToResults(Result result, boolean[][] fromUser) {
        calculateButtonContainer.showWidget(CalculateContainerState.editAgain.ordinal());
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)  {
                InlineLabel contents = new InlineLabel("" + result.board[y*9 + x]);
                contents.setStylePrimaryName(fromUser[x][y] ? "userentered" : "result");
                contents.addClickHandler(newTableCellClickHandler());
                table.setWidget(firstSudokuRow+y, x, contents);
            }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // implements EntryPoint
    // ----------------------------------------------------------------------------------------------------------------

    public void onModuleLoad() {
        RootPanel.get("sudokucontainer").add(table);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Clicks & Actions
    // ----------------------------------------------------------------------------------------------------------------

    void clearGridClicked() {
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)
                gridFields[x][y].setText("");
        convertTableToDataEntry();
    }

    void loadExampleClicked() {
        int[] board = new int[] {
            0, 0, 0,  6, 0, 4,  0, 5, 7,
            0, 0, 7,  0, 1, 0,  8, 0, 0,
            5, 0, 0,  0, 7, 0,  0, 0, 6,

            9, 0, 0,  0, 0, 0,  0, 2, 1,
            0, 0, 0,  2, 0, 3,  0, 0, 0,
            4, 7, 0,  0, 0, 0,  0, 0, 8,

            7, 0, 0,  0, 5, 0,  0, 0, 4,
            0, 0, 6,  0, 4, 0,  9, 0, 0,
            1, 4, 0,  3, 0, 9,  0, 0, 0
        };

        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++) {
                int val = board[y*9 + x];
                gridFields[x][y].setText(val == 0 ? "" : ""+val);
            }

        convertTableToDataEntry();
    }

    void calculateClicked() {
        int board[] = new int[9*9];  // 9*y + x; 0 = unknown
        boolean fromUser[][] = new boolean[9][9]; // x,y

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                try {
                    board[9*y + x] = (int) NumberFormat.getDecimalFormat().parse(gridFields[x][y].getText());
                    fromUser[x][y] = true;
                }
                catch (NumberFormatException e) {
                    board[9*y + x] = 0;
                    fromUser[x][y] = false;
                }
            }
        }

        int timeoutMillis = ((SudokuConfig) GWT.create(SudokuConfig.class)).getTimeoutMillis();
        Result result = new SudokuSolver().setTimeoutInMilliseconds(timeoutMillis).solve(board);
        String msg;
        MessageType msgType;
        switch (result.type) {
            case ERR_NONE:      msgType = MessageType.error; msg = "No solutions found"; break;
            case ERR_TIMEOUT:   msgType = MessageType.error; msg = "This is not a reasonable Sudoku"; break;
            case WARN_MULTIPLE: msgType = MessageType.error; msg = "Multiple solutions found"; break;
            case UNIQUE:
                msgType = MessageType.success;
                msg = "Unique solution found";
                convertTableToResults(result, fromUser);
                break;
            default: throw new RuntimeException("unreachable");
        }

        InlineLabel msgLabel = new InlineLabel(msg);
        msgLabel.setStylePrimaryName("message");
        
        FlowPanel msgPanel = new FlowPanel();
        msgPanel.setStylePrimaryName(msgType.toString());
        msgPanel.add(msgType.getImg().createImage());
        msgPanel.add(msgLabel);
        messageContainer.setWidget(msgPanel);
    }

    void toEditClicked() {
        convertTableToDataEntry();
    }
}
