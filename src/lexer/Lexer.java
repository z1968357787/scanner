package lexer;

import java.io.IOException;
import java.util.Hashtable;

public class Lexer {

	public static int line = 1;
	char peek = ' ';
	Word temp = null;
	/*
	 *保存关键字
	 */
	Hashtable words = new Hashtable();

	/*
	 *关键字插入，用于识别输入的标识是否为关键字
	 */
	void reserve(Word w) {
		words.put(w.lexeme, w);
	}

	public Lexer() {
		/*
		 *首先插入所有关键字
		 */
		reserve(new Word("if", Tag.IF));
		reserve(new Word("else", Tag.ELSE));
		reserve(new Word("while", Tag.WHILE));
		reserve(new Word("do", Tag.DO));
		reserve(new Word("break", Tag.BREAK));
		reserve(Word.True);
		reserve(Word.False);
		/*
		 *自己增加关键字,包括class，for，void，new
		 */

		reserve(new Word("static", Tag.KEY));
		reserve(new Word("New", Tag.KEY));
		reserve(new Word("void", Tag.KEY));
		reserve(new Word("int", Tag.KEY));
		reserve(new Word("double", Tag.KEY));
		reserve(new Word("bool", Tag.KEY));
		reserve(new Word("string", Tag.KEY));
		reserve(new Word("class", Tag.KEY));
		reserve(new Word("null", Tag.KEY));
		reserve(new Word("this", Tag.KEY));
		reserve(new Word("extends", Tag.KEY));
		reserve(new Word("for", Tag.KEY));
		reserve(new Word("else", Tag.KEY));
		reserve(new Word("return", Tag.KEY));
		reserve(new Word("new", Tag.KEY));
		reserve(new Word("NewArray", Tag.KEY));
		reserve(new Word("Print", Tag.KEY));
		reserve(new Word("ReadInteger", Tag.KEY));
		reserve(new Word("ReadLine", Tag.KEY));
	}

	public void readch() throws IOException {
		peek = (char) System.in.read();
		
	}

	boolean readch(char c) throws IOException {
		readch();
		if (peek != c) {
			return false;
		}
		peek = ' ';
		return true;
	}

	public Token scan() throws IOException {
		/*
		 *首先判断是否有遗留的E或e，作为下一个标识符的开端
		 */
		if(temp!=null){
			Word word=new Word(temp);
			temp=null;
			reserve(word);
			return word;
		}
		/*
		if(temp!=' '){
			StringBuilder b=new StringBuilder();
			b.append(temp);
			/*
			 *如果e后面跟着的是标识符中的合法字符

			if(Character.isLetterOrDigit(peek)||peek=='_'){
				do {
					b.append(peek);
					readch();
				} while (Character.isLetterOrDigit(peek)||peek=='_');
			}
			temp=' ';
			return new Word(b.toString(),Tag.ID);
		}*/
		/*
		 *首先消除空行以及前面的空格
		 */
		for (;; readch()) {
			if (peek == ' ' || peek == '\t')
				continue;
			else if (peek == '\n') {
				line += 1;
			} else {
				break;
			}
		}

		switch (peek) {
			/*
			 *处理与运算或&运算符
			 */
		case '&':
			if (readch('&'))
				return Word.and;
			else
				return new Token('&');
			/*
			 *处理或运算或单斜杠
			 */
		case '|':
			if (readch('|'))
				return Word.or;
			else
				return new Token('|');
			/*
			 *处理等于或赋值
			 */
		case '=':
			if (readch('='))
				return Word.eq;
			else
				return new Token('=');
			/*
			 *处理感叹号或不等于
			 */
		case '!':
			if (readch('='))
				return Word.ne;
			else
				return new Token('!');
			/*
			 *处理小于或小于等于
			 */
		case '<':
			if (readch('='))
				return Word.le;
			else
				return new Token('<');
			/*
			 *处理大于或者大于等于
			 */
		case '>':
			if (readch('='))
				return Word.ge;
			else
				return new Token('>');
			/*
			 *处理字符串常量
			 */
		case '"' :
			return processStringConstant();
			/*
			 *处理注释
			 */
		case '/' :
			readch();
			/*
			 *单行注释
			 */
			if(peek=='/'){
				processRowNote();
				return new Token(Tag.NOTE);
			}else if (peek=='*'){
				/*
				 *多行注释
				 */
				processSegmentNote();
				return new Token(Tag.NOTE);
			}else {
				/*
				 * 处理/运算符
				 */
				return new Token('/');
			}
			/*
			 *处理非法字符
			 */
		case '@' : peek=' ';return new Error(line,"@ is an illegal char");
		}
		/*
		 *处理数字常量
		 * 首先先判断是十进制数还是十六进制数
		 * 通过判断首字符是否是0以及下一个字符是否是X来判断是否是十六进制数
		 * 然后十六进制数通过processHexadecimalNumber方法进行处理
		 * 十进制数直接处理
		 * 通过Character类将字符转换成数字，然后通过v = 10 * v + Character.digit(peek, radix)来运算数字结果
		 * radix=10 十进制，radix=16 十六进制
		 * 然后判断是否有.字符输入，判断是否为浮点数，如果有则需要提取整数，然后小数部分另外计算，最终合并
		 * 最后再判断是否有e输入，判断是否为科学计数法
		 * 此时分类讨论
		 * 若e后方的字符是+，则表明要乘以相应的10^x
		 * 若e后方的字符是-，则表明要除以相应的10^x
		 * 若e后方无符号，则将其认为是下一个标识符的开头，用全局变量temp保存
		 */
		if (Character.isDigit(peek)) {
			int v = 0;
			v = 10 * v + Character.digit(peek, 10);
			readch();
			/*
			 *十六进制数
			 */
			if(v==0){
				if(peek=='X'||peek=='x'){
					/*
					 *十六进制数转化为十进制数
					 */
					return processHexadecimalNumber();
				}
			}
			/*
			 *十进制数
			 */
			else {
				while (Character.isDigit(peek)){
					v = 10 * v + Character.digit(peek, 10);
					readch();
				}
				if (peek != '.')
					return new Num(v);
				float x = v;
				float d = 10;
				for (;;) {
					readch();
					if (!Character.isDigit(peek)){
						if(peek=='e'||peek=='E'){
							return processScientificNotation(x);
						}
						break;
					}
					x = x + Character.digit(peek, 10) / d;
					d = d * 10;
				}
				return new Real(x);
			}

		}
		/*
		 *此方法用于识别标识符，这里通过添加或条件peek=='_'，表明标识符首字符可以是字母也可以是下划线
		 * 然后后面可以继续跟入数字或字母或下划线
		 * 然后在关键字字典中判断是否有相应的关键字
		 * 如果有，这说明该字符是一个关键字
		 * 否则，该字符是一个标识符
		 */
		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			/*
			 *顺序读取标识符，开头不能为数字
			 */
			do {
				b.append(peek);
				readch();
			} while (Character.isLetterOrDigit(peek)||peek=='_');
			String s = b.toString();
			/*
			 *判断标识符是否为关键字
			 */
			Word w = (Word) words.get(s);
			if (w != null)
				return w;
			w = new Word(s, Tag.ID);
			words.put(s, w);
			return w;
		}
		/*
		 *这里表示除以上情况以外的字符都识别为操作符
		 */
		Token tok = new Token(peek);
		peek = ' ';
		return tok;
	}
	/*
	 *输出字符字典中标识符与关键字的数量
	 */
	public void out() {
		System.out.println(words.size());
		
	}

	/*
	 *获取字符
	 */
	public char getPeek() {
		return peek;
	}

	public void setPeek(char peek) {
		this.peek = peek;
	}
	/*
	 *处理十六进制数，返回整形数据
	 */
	private Num processHexadecimalNumber() throws IOException {
		int v=0;
		readch();
		while (isHexadecimalNumber()){
			/*
			 *将十六进制数转化为十进制数
			 */
			v = 16 * v + Character.digit(peek, 16);
			readch();
		}
		return new Num(v);
	}
	/*
	 *判断是否为十六进制数
	 */
	private boolean isHexadecimalNumber(){
		return Character.isDigit(peek)||peek=='A'||peek=='a'||peek=='B'||peek=='b'||peek=='C'||peek=='c'||peek=='D'||peek=='d'||peek=='E'||peek=='e'||peek=='F'||peek=='f';
	}

	/*
	 *处理科学计数法
	 * 若e后方的字符是+，则表明要乘以相应的10^x
	 * 若e后方的字符是-，则表明要除以相应的10^x
	 * 若e后方无符号，则系统自动报错，表明缺少运算符
	 */
	private Token processScientificNotation(float x) throws IOException {
		int v=0;
		char e=peek;//保存提前的e
		readch();
		if(peek=='+'){
			readch();
			while (Character.isDigit(peek)){
				v = 10 * v + Character.digit(peek, 10);
				readch();
			}
			return new Real ((float) (x*Math.pow(10,v)));
		}else if (peek=='-'){
			readch();
			while (Character.isDigit(peek)){
				v = 10 * v + Character.digit(peek, 10);
				readch();
			}
			return new Real((float) (x/Math.pow(10,v)));
		}else{
			/*
			 *缺少指数符号,认定其为下一个标识符的开头
			 */
			StringBuffer b = new StringBuffer();
			/*
			 *顺序读取标识符，开头不能为数字
			 */
			b.append(e);
			while(Character.isLetterOrDigit(peek)||peek=='_') {
				b.append(peek);
				readch();
			}
			temp=new Word(b.toString(),Tag.ID);
			return new Real(x);
		}
	}

	/*
	 *处理字符串常量
	 * 当字符读到换行或者"符时停止
	 * 如果读到"，说明字符串常量合法结束
	 * 如果读到换行，说明字符串常量缺少"
	 */
	private Token processStringConstant() throws IOException {
		StringBuffer b = new StringBuffer();
		do {
			b.append(peek);
			readch();
		} while (peek!='"'&&peek!='\n');
		if(peek=='"'){
			b.append('"');
			peek=' ';
			return new Str(b.toString());
		}else {
			peek=' ';
			Error error=new Error(line,"\" is lost");
			line+=1;
			return error;
		}
	}

	/*
	 *处理单行注释，读到换行时结束
	 */
	private void processRowNote() throws IOException {
		do{
			readch();
		}while(!isRowNoteEnd());
		peek=' ';
		line+=1;
	}

	/*
	 *处理多行注释，读到*以及/之后结束,读到换行时要令行数加一
	 */
	private void processSegmentNote() throws IOException {
		do{
			readch();
			if(peek=='\n'){
				line+=1;
			}
		}while(!isSegmentNoteEnd());
		peek=' ';
	}

	/*
	 *判断是否读到换行，判断单行注释是否结束
	 */
	private boolean isRowNoteEnd(){
		return peek=='\n';
	}

	/*
	 *判断是否先读到*，然后读到/，判断多行注释是否结束
	 */
	private boolean isSegmentNoteEnd() throws IOException {
		if (peek=='*'){
			if(readch('/')){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}
}
