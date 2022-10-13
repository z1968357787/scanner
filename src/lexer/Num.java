package lexer;
/*
 *整形常量
 */
public class Num extends Token {

	/*
	 *整型常量值
	 */
	public final int value;


	public Num(int v) {
		super(Tag.NUM);
		value = v;
	}

	public String toString() {
		return "" + value;
	}

}
