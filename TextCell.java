// Student Name
// Period X

// This class represents a cell containing text data
public class TextCell extends Cell {
    private String text;
    
    /**
     * Constructor for TextCell
     * 
     * @param text The text content for this cell (without quotes)
     */
    public TextCell(String text) {
        super(); // Call parent constructor
        this.text = text;
        setExpression("\"" + text + "\""); // Set the expression with quotes
    }

    /**
     * Returns the raw text value of this cell
     */
    @Override
    public String toString() {
        return text;
    }
    
    /**
     * For text cells, getValue returns 0.0 as they don't have numeric value
     */
    @Override
    public double getValue() {
        return 0.0; // Text cells don't have numeric value
    }
}


