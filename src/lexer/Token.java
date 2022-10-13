package lexer;
/*
 *词法单元
 */
public class Token {

	/*
	 *词法标记
	 */
	public final int tag;

	public Token(int t) {
		tag = t;
	}


	public String toString() {
		return "" + (char) tag;
	}
}
