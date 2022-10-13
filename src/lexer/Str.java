package lexer;
/*
 *字符串常量
 */
public class Str extends Token{

    /*
     *字符串常量值
     */
    public final String value;


    public String toString() {
        return "" + value;
    }

    public Str(String v) {
        super(Tag.STR);
        value = v;
    }
}
