package lexer;

public class Error extends Token{

    public final String error;
    public final int line;

    public String toString() {
        return "In Line "+line+" have a syntax error that "+error;
    }

    public Error(int line,String erroe) {
        super(Tag.ERROR);
        this.error=erroe;
        this.line=line;
    }
}
