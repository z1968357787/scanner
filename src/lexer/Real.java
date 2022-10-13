package lexer;
/*
 *浮点数常量
 */
public class Real extends Token {
	public final float value;

	public Real(float v) {
		/*
		 *说明数据类型
		 */
		super(Tag.REAL);
		/*
		 *数据值
		 */
		value = v;
	}

	public String toString() {
		return "" + value;
	}
}
