// Student Name
// Period X

/*
 * In Checkpoint #1: this class is ignored.
 * In Checkpoint #2: the NumberCell will hold a number only.
 *    Example:  a1 = 5
 * In Checkpoint #3: the NumberCell may hold an expression.
 *    Example:  a1 = ( 2 + a2 / a3 * a4 )
 * In Final Submission: the NumberCell may hold functions.
 *    Example:  a1 = ( sum b1 - b5 )
 */
public class NumberCell extends Cell {
    private double value;
    
    /**
     * Constructor for NumberCell
     * 
     * @param value The numeric value for this cell
     */
    public NumberCell(double value) {
        super(); // Call parent constructor
        this.value = value;
        
        // Store the expression as an integer if it's a whole number
        if (value == (int)value) {
            setExpression(String.valueOf((int)value));
        } else {
            setExpression(String.valueOf(value));
        }
    }

    /*
     * This returns the string to be presented in the grid.
     */
    @Override
    public String toString() {
        // Format the value with one decimal place for display
        return String.format("%.1f", getValue());
    }

    /*
     * This will return the number for this cell.
     */
    @Override
    public double getValue() {
        return value;
    }
}


