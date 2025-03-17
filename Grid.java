// Student Name
// Period X
// Text Excel Project

import java.io.*;

/*
 * The Grid class will hold all the cells. It allows access to the cells via the
 * public methods. It will create a display String for the whole grid and process
 * many commands that update the cells. These command will include
 * sorting a range of cells and saving the grid contents to a file.
 *
 */
public class Grid extends GridBase {

    // These are called instance fields.
    // They are scoped to the instance of this Grid object.
    // Use them to keep track of the count of columns, rows and cell width.
    // They are initialized to the prescribed default values.
    // Notice that there is no "static" here.
    private int colCount = 7;
    private int rowCount = 10;
    private int cellWidth = 9;
    // TODO: Student must create this
    private Cell[][] matrix;
    
    // Constructor with matrix creation
    public Grid() {
        // Initialize matrix with empty cells
        createMatrix();
    }
    
    // Create a new matrix with empty cells
    private void createMatrix() {
        matrix = new Cell[rowCount][colCount];
        
        // Fill with EmptyCell objects (not null)
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                matrix[row][col] = new Cell();
            }
        }
    }

    /*
     * This method processes a user command.
     * 
     * Checkpoint #1 commands are: print : render a text based version of the matrix
     * width = [value] : set the cell width width : get the cell width rows =
     * [value] : set the row count cols = [value] : set the column count rows : get
     * the row count cols : get the column count
     *
     * Checkpoint #2 commands are: [cell] = [expression] : set the cell's
     * expression, for checkpoint # expressions may be... - a value such as 5.
     * Example: a2 = 5 - a string such as "hello". Example: a3 = "hello" [cell] :
     * get the cell's expression, NOT the cell's value value [cell] : get the cell
     * value expr [cell] : get the cell's expression, NOT the cell's value display
     * [cell] : get the string for how the cell wants to display itself clear :
     * empty out the entire matrix save [file] : saves to a file all the commands
     * necessary to regenerate the grid's contents
     *
     * Checkpoint #3 commands are: [cell] = [expression] : where the expression is a
     * complicated formula. Example: a1 = ( 3.141 * b3 + b1 - c2 / 4 )
     *
     * Final commands are: [cell] = [expression] : where the expression may contain
     * a single function, sum or avg: Example: a1 = ( sum a1 - a3 ) Example: b1 = (
     * avg a1 - d1 ) clear [cell] : empty out a single cell. Example: clear a1 sorta
     * [range] : sort the range in ascending order. Example: sorta a1 - a5 sortd
     * [range] : sort the range in descending order. Example: sortd b1 - e1
     * 
     *
     * 
     * Parameters: command : The command to be processed. Returns : The results of
     * the command as a string to be printed by the infrastructure.
     */
    public String processCommand(String command) {
        String result = null;
        
        // Handle basic commands
        if (command.equals("rows")) {
            result = String.valueOf(rows());
        }
        else if (command.equals("cols")) {
            result = String.valueOf(cols());
        }
        else if (command.equals("width")) {
            result = String.valueOf(width());
        }
        else if (command.equals("print")) {
            result = getGridText();
        }
        else if (command.equals("clear")) {
            // Clear all cells in the grid
            createMatrix();
            result = getGridText();
        }
        else if (command.startsWith("rows = ")) {
            try {
                int newRows = Integer.parseInt(command.substring(7).trim());
                int resultVal = rows(newRows);
                if (resultVal > 0) {
                    result = String.valueOf(resultVal);
                } else {
                    result = "Error: Rows must be positive";
                }
            } catch (NumberFormatException e) {
                result = "Error: Invalid row format";
            }
        }
        else if (command.startsWith("cols = ")) {
            try {
                int newCols = Integer.parseInt(command.substring(7).trim());
                int resultVal = cols(newCols);
                if (resultVal > 0) {
                    result = String.valueOf(resultVal);
                } else {
                    result = "Error: Columns must be positive";
                }
            } catch (NumberFormatException e) {
                result = "Error: Invalid column format";
            }
        }
        else if (command.startsWith("width = ")) {
            try {
                int newWidth = Integer.parseInt(command.substring(8).trim());
                int resultVal = width(newWidth);
                if (resultVal > 0) {
                    result = String.valueOf(resultVal);
                } else {
                    result = "Error: Width must be positive";
                }
            } catch (NumberFormatException e) {
                result = "Error: Invalid width format";
            }
        }
        // Cell assignment (e.g., A1 = 5, B2 = "text")
        else if (command.contains("=")) {
            String[] parts = command.split("=", 2);
            if (parts.length != 2) {
                result = "Error: Invalid assignment format";
            } else {
                String cellLocation = parts[0].trim();
                String expression = parts[1].trim();
                
                // Get the cell coordinates
                int[] coords = getCellCoordinates(cellLocation);
                if (coords == null) {
                    result = "Error: Invalid cell reference";
                } else {
                    // Handle different types of values
                    if (expression.startsWith("\"") && expression.endsWith("\"")) {
                        // Text value
                        String textValue = expression.substring(1, expression.length() - 1);
                        matrix[coords[0]][coords[1]] = new TextCell(textValue);
                    } else {
                        try {
                            // Numeric value
                            double numValue = Double.parseDouble(expression);
                            matrix[coords[0]][coords[1]] = new NumberCell(numValue);
                        } catch (NumberFormatException e) {
                            result = "Error: Invalid value format";
                        }
                    }
                    
                    if (result == null) {
                        result = "";  // Successfully set the cell value
                    }
                }
            }
        }
        // Cell inspection (e.g., A1)
        else if (isCellReference(command)) {
            int[] coords = getCellCoordinates(command);
            if (coords != null) {
                result = matrix[coords[0]][coords[1]].getExpression();
            }
        }
        // Get cell value (e.g., value A1)
        else if (command.startsWith("value ")) {
            String cellRef = command.substring(6).trim();
            int[] coords = getCellCoordinates(cellRef);
            if (coords != null) {
                Cell cell = matrix[coords[0]][coords[1]];
                if (cell instanceof NumberCell) {
                    result = String.valueOf(cell.getValue());
                } else if (cell instanceof TextCell) {
                    result = ((TextCell) cell).toString();
                } else {
                    result = "";
                }
            }
        }
        // Get cell display text (e.g., display A1)
        else if (command.startsWith("display ")) {
            String cellRef = command.substring(8).trim();
            int[] coords = getCellCoordinates(cellRef);
            if (coords != null) {
                result = matrix[coords[0]][coords[1]].toString();
            }
        }
        
        // If command is still not handled
        if (result == null) {
            result = "unknown or malformed command: " + command;
        }
        
        return result;
    }
    public  static String help(){
        return """
                TextExcel Help:
                  help : provides help to the user on how to use Text Excel
                  print : returns a string of the printed grid. The grid does this for itself!
                  rows : returns the number of rows currently in the grid. The grid knows this info.
                  cols : returns the number of columns currently in the grid
                  width : returns the width of an individual cell that is used when displaying the grid contents.
                  rows = <number> : resizes the grid to have <number> rows. The grid contents will be cleared.
                  cols = <number> : resizes the grid to have <number> columns. The grid contents will be cleared.
                  width = <number> : resizes the width of a cell to be <number> characters wide when printing the grid.
                  load <filename> : opens the file specified and processes all commands in it.
                """;
    }
    public String getGridText() {
        String grid = "";
        
        // First line with column headers
        grid += "    |";  // Empty corner cell
        
        // Add column headers (A, B, C, etc.)
        for (int col = 0; col < colCount; col++) {
            String columnLabel = String.valueOf((char)('A' + col));
            
            // Center the column letter
            int spacesBefore = (cellWidth - columnLabel.length()) / 2;
            int spacesAfter = cellWidth - columnLabel.length() - spacesBefore;
            
            grid += " ".repeat(spacesBefore);
            grid += columnLabel;
            grid += " ".repeat(spacesAfter);
            grid += "|";
        }
        grid += "\n";
        
        // Add separator line
        grid += "----+";
        for (int col = 0; col < colCount; col++) {
            grid += "-".repeat(cellWidth);
            grid += "+";
        }
        grid += "\n";
        
        // Add rows with cell content
        for (int row = 0; row < rowCount; row++) {
            // Row number
            String rowLabel = String.valueOf(row + 1);
            grid += String.format("%3s |", rowLabel);
            
            // Add cell contents
            for (int col = 0; col < colCount; col++) {
                Cell cell = matrix[row][col];
                String cellContent = formatCellForDisplay(cell);
                grid += cellContent;
                grid += "|";
            }
            grid += "\n";
            
            // Add separator after each row
            grid += "----+";
            for (int col = 0; col < colCount; col++) {
                grid += "-".repeat(cellWidth);
                grid += "+";
            }
            grid += "\n";
        }
        
        return grid;
    }

    /**
     * Format a cell's value for display in the grid
     * 
     * @param cell The cell to format
     * @return A string of exactly cellWidth characters
     */
    private String formatCellForDisplay(Cell cell) {
        if (cell.getExpression().isEmpty()) {
            // Empty cell
            return " ".repeat(cellWidth);
        }
        
        String cellValue = cell.toString();
        
        // Right-align all values (both numeric and text)
        if (cellValue.length() >= cellWidth) {
            return cellValue.substring(0, cellWidth);
        } else {
            return " ".repeat(cellWidth - cellValue.length()) + cellValue;
        }
    }
    /**
     * saveToFile
     *
     * This method will process the command: "save {filename}"
     * <p>
     * Ask the matrix for all formulas for all non-empty cells. Empty cells should
     * not be saved.
     *
     * Save all properties such as grid size and cell width (if available)
     * 
     * @param filename is the name of the file to save
     * @return A message to user about the success/failure of saving to a file.
     */
    private String saveToFile(String filename) {
        File file = new File(filename);
        String result = "Saved to file: " + file.getAbsolutePath();

        try {
            // Get the writer ready
            PrintStream writer = new PrintStream(file);
            saveGrid(writer);
        } catch (FileNotFoundException e) {
            result = "Cannot write to the file: " + file.getAbsolutePath();
        }

        return result;
    }

    /**
     * saveGrid will save the gride to a file.
     *
     * Ask the matrix for all formulas for all non-empty cells. Empty cells should
     * not be saved.
     *
     * Save all properties such as grid size and cell width (if available)
     * 
     * @param writer is the PrintStream to print to
     */
    public void saveGrid(PrintStream writer) {
        // save the rows, cols and width
        writer.println("rows = " + rowCount);
        writer.println("cols = " + colCount);
        writer.println("width = " + cellWidth);

        // save the grid formulas, for every cell that is not empty
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                String formula = matrix[row][col].getExpression();
                if (formula != null && formula.length() > 0) {
                    writer.println("" + (char) ('A' + col) + (row + 1) + " = " + formula);
                }
            }
        }
    }

    /**
     * Returns the current width of cells in the grid
     * 
     * @return The current cell width value
     */
    public int width() {
        return cellWidth;
    }

    /**
     * Sets a new width for cells in the grid
     * 
     * @param width The new width to set for cells
     * @return A string representation of the new width
     */
    public int  width(int width) {
        if (width < 1) {
            return 0;
        }
        this.cellWidth = width;
        return width;  // Just return the value, not "Width set to x"
    }

    /**
     * Returns the current number of rows in the grid
     * 
     * @return The current row count
     */
    public int rows() {
        return rowCount;
    }

    /**
     * Sets a new number of rows for the grid
     * 
     * @param rows The new row count to set
     * @return The new row count, or 0 if invalid
     */
    public int rows(int rows) {
        if (rows < 1) {
            return 0;
        }
        this.rowCount = rows;
        return rows;
    }

    /**
     * Returns the current number of columns in the grid
     * 
     * @return The current column count
     */
    public int cols() {
        return colCount;
    }

    /**
     * Sets a new number of columns for the grid
     * 
     * @param cols The new column count to set
     * @return The new column count, or 0 if invalid
     */
    public int cols(int cols) {
        if (cols < 1) {
            return 0;
        }
        this.colCount = cols;
        return cols;
    }

    /**
     * Helper method to convert cell location (e.g., "A1") to grid coordinates
     * 
     * @param cellLocation The cell location (e.g., "A1")
     * @return An int array with [row, col] indices or null if invalid
     */
    private int[] getCellCoordinates(String cellLocation) {
        // Validate the cell location format
        if (cellLocation == null || cellLocation.length() < 2) {
            return null;
        }
        
        // Extract column and row
        char colChar = Character.toUpperCase(cellLocation.charAt(0));
        
        // Validate column letter is within range
        if (colChar < 'A' || colChar >= 'A' + colCount) {
            return null;
        }
        
        try {
            // Extract row number and adjust to 0-based index
            int rowIndex = Integer.parseInt(cellLocation.substring(1)) - 1;
            
            // Validate row index is within range
            if (rowIndex < 0 || rowIndex >= rowCount) {
                return null;
            }
            
            return new int[] { rowIndex, colChar - 'A' };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Checks if a string is a valid cell reference (e.g., A1, B12)
     * 
     * @param str The string to check
     * @return true if the string is a valid cell reference
     */
    private boolean isCellReference(String str) {
        // Check length - must be at least 2 characters
        if (str == null || str.length() < 2) {
            return false;
        }
        
        // First character must be a letter (A-Z or a-z)
        char firstChar = str.charAt(0);
        if (!((firstChar >= 'A' && firstChar <= 'Z') || 
              (firstChar >= 'a' && firstChar <= 'z'))) {
            return false;
        }
        
        // Second character must be a digit 1-9
        char secondChar = str.charAt(1);
        if (secondChar < '1' || secondChar > '9') {
            return false;
        }
        
        // Any remaining characters must be digits 0-9
        for (int i = 2; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        
        return true;
    }
    // A simple implementation of EmptyCell as inner class
   
}


