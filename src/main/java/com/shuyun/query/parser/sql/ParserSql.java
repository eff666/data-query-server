package com.shuyun.query.parser.sql;

import java.util.ArrayList;
import java.util.List;

public class ParserSql {

	private String sql;
	private int index = 0;

	Type type;
	String tableName;
	List<String> columns = new ArrayList<>();
	List<Object> values = new ArrayList<>();
	String idColumnName;
	Object idColumnValue;
	List<Pair<String, Object>> sets = new ArrayList<>();
	List<Filter> filters = new ArrayList<>();
	List<String> orderBys = new ArrayList<>();
	Long limit;
	Long offset;

	public List<String> getOrderBys() {
		return orderBys;
	}

	public Type getType() {
		return type;
	}

	public Long getLimit() {
		return limit;
	}

	public Long getOffset() {
		return offset;
	}

	public String getTableName() {
		return tableName;
	}

	public List<Object> getValues() {
		return values;
	}

	public List<Pair<String, Object>> getSets() {
		return sets;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public Object getIdColumnValue() {
		return idColumnValue;
	}

	public List<String> getColumns() {
		return columns;
	}

	public ParserSql(String sql) {
		this.sql = sql;
	}

	public void parse() {

		skipWhitespace();

		char c = sql.charAt(index);

		switch (c) {
		case 'I':
		case 'i':
			type = Type.INSERT;
			readIgnore("INSERT");
			readWhite();
			parseInsert();
			break;
		case 'D':
		case 'd':
			type = Type.DELETE;
			type = Type.DELETE;
			readIgnore("delete");
			readWhite();
			parseDelete();
			break;
		case 'U':
		case 'u':
			type = Type.UPDATE;
			readIgnore("update");
			readWhite();
			parseUpdate();
			break;
		case 'S':
		case 's':

			readIgnore("select");
			readWhite();

			if (tryReadIgnore("count")) {
				type = Type.SELECT_COUNT;
				parseSelectCount();
			} else {
				type = Type.SELECT;
				parseSelect();
			}

			break;
		default:
			throw new RuntimeException("error sql: " + sql);
		}

		skipWhitespace();
		if (index != sql.length()) {
			throw new RuntimeException("error at: " + sql.substring(index));
		}
	}

	private void parseSelectCount() {
		skipWhitespace();
		readIgnore("(");
		skipWhitespace();
		readIgnore("*");
		skipWhitespace();
		readIgnore(")");
		readWhite();
		readIgnore("from");
		readWhite();
		tableName = readIdentify();
	}

	private void parseSelect() {
		readColumnList();
		backWhite();
		readWhite();
		readIgnore("from");
		readWhite();
		tableName = readIdentify();
		readWhite();
		readIgnore("where");
		readWhite();
		readFilterList();
		backWhite();
		readWhite();

		if (tryReadIgnore("order")) {
			readWhite();
			readIgnore("by");

			readWhite();
			readValues3();
		}

		readIgnore("limit");
		readWhite();
		limit = readLong();
		readWhite();
		readIgnore("offset");
		readWhite();
		offset = readLong();

	}

	private void readValues3() {

		String col = readIdentify();
		orderBys.add(col);

		while (true) {
			skipWhitespace();

			if (tryReadIgnore(",")) {
				skipWhitespace();
				col = readIdentify();
				orderBys.add(col);
			} else {
				break;
			}
		}
	}

	private void backWhite() {
		if (index - 1 > 0 && Character.isWhitespace(sql.charAt(index - 1))) {
			index--;
		}
	}

	private void readFilterList() {
		while (true) {
			Filter f = readFilter();
			filters.add(f);

			skipWhitespace();

			if (tryReadIgnore("and")) {
				skipWhitespace();
			} else {
				break;
			}
		}
	}

	private Filter readFilter() {
		String col = readIdentify();
		skipWhitespace();
		
		OP op = OP.EQUAL;
		if (tryReadIgnore("like")) {
			op = OP.LIKE;
		} else if (tryReadIgnore("=")) {
			op = OP.EQUAL;
		} else if (tryReadIgnore("!=")) {
			op = OP.NOT_EQUAL;
		} else if (tryReadIgnore("<>")) {
			op = OP.NOT_EQUAL;
		} else if (tryReadIgnore(">=")) {
			op = OP.GREAT_EQUAL;
		} else if (tryReadIgnore("<=")) {
			op = OP.LESS_EQUAL;
		} else if (tryReadIgnore(">")) {
			op = OP.GREAT_THAN;
		} else if (tryReadIgnore("<")) {
			op = OP.LESS_THAN;
		} else if (tryReadIgnore("in")) {
			skipWhitespace();
			return readOrFilter(col);
		} else if (tryReadIgnore("is")) {
			readWhite();
			if (tryReadIgnore("not")) {
				readWhite();
				readIgnore("null");
				return new BasicFilter(col, OP.NOT_EQUAL, null);
			} else {
				readIgnore("null");
				return new BasicFilter(col, OP.EQUAL, null);
			}
		} else {
			throw new RuntimeException("error op");
		}

		skipWhitespace();

		Object v = readValue();
		return new BasicFilter(col, op, v);
	}

	private OrFilter readOrFilter(String col) {
		List<Filter> filters = new ArrayList<>();
		List<Object> vs = readValues1();
		for (Object o : vs) {
			filters.add(new BasicFilter(col, OP.EQUAL, o));
		}
		return new OrFilter(filters);
	}

	private void parseUpdate() {
		tableName = readIdentify();
		readWhite();
		readIgnore("set");
		readWhite();

		parseSetList();
		backWhite();
		readWhite();
		readIgnore("where");
		readWhite();

		idColumnName = readIdentify();
		skipWhitespace();
		readIgnore("=");
		skipWhitespace();

		idColumnValue = readValue();
	}

	private void parseSetList() {
		while (index < sql.length()) {
			parseSet();
			skipWhitespace();

			if (tryReadIgnore(",")) {
				skipWhitespace();
			} else {
				break;
			}
		}
	}

	private void parseSet() {

		String name = readIdentify();
		skipWhitespace();
		readIgnore("=");
		skipWhitespace();

		Object v = readValue();
		Pair<String, Object> a = new Pair<String, Object>(name, v);
		sets.add(a);
	}

	private void parseDelete() {
		readIgnore("from");
		readWhite();
		tableName = readIdentify();
		readWhite();
		readIgnore("where");
		readWhite();
		idColumnName = readIdentify();
		skipWhitespace();
		readIgnore("=");
		skipWhitespace();

		idColumnValue = readValue();
	}

	private void parseInsert() {
		readIgnore("INTO");
		readWhite();

		tableName = readIdentify();
		skipWhitespace();
		readColumnGroup();
		readWhite();

		readIgnore("VALUES");
		skipWhitespace();

		readValues();
	}

	private Long tryReadLong() {
		int indexPre = index;
		char c = sql.charAt(index);
		StringBuilder b = new StringBuilder();

		if (Character.isDigit(c)) {
			b.append(c);

			index++;
			while (index < sql.length()) {
				c = sql.charAt(index);
				if (Character.isDigit(c)) {
					index++;
					b.append(c);
				} else {
					if(c == 'L' || c == 'l'){
						index++;
						break;
					}else{
						break;
					}

				}
			}

			if(c == '.'){
				index = indexPre;
				return null;
			}
			return Long.parseLong(b.toString());
		} else {
			index = indexPre;
			return null;
		}
	}

	private Double tryReadDouble() {
		int indexPre = index;
		char c = sql.charAt(index);
		StringBuilder b = new StringBuilder();

		if (Character.isDigit(c)) {
			b.append(c);
			index++;
			while (index < sql.length()) {
				c = sql.charAt(index);
				if (Character.isDigit(c) || c == '.') {
					index++;
					b.append(c);
				} else {
					if(c == 'f' || c == 'F' || c == 'd' || c == 'D'){
						index++;
						break;
					}else{
						break;
					}

				}
			}

			return Double.parseDouble(b.toString());
		} else {
			index = indexPre;
			return null;
		}
	}

	private Boolean tryReadBoolean() {
		int indexPre = index;
		char c = sql.charAt(index);
		StringBuilder b = new StringBuilder();

		if (c == 'T' || c == 't' || c == 'F' || c == 'f') {
			b.append(c);
			index++;
			while (index < sql.length()) {
				c = sql.charAt(index);
				if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
					index++;
					b.append(c);
				} else {
					break;
				}
			}

			return Boolean.parseBoolean(b.toString());
		} else {
			index = indexPre;
			return null;
		}
	}

	private Object readValue() {
		Object v = tryReadLong();
		if (v != null) {
			return v;
		}

		v = tryReadDouble();
		if (v != null) {
			return v;
		}

		v = tryReadBoolean();
		if (v != null) {
			return v;
		}

		v = tryReadString();
		if (v != null) {
			return v;
		}

		if (tryReadIgnore("null")) {
			return null;
		}

		throw new RuntimeException("expect number, string or null");
	}

	private Long readLong() {
		Long l = tryReadLong();
		if (l == null) {
			throw new RuntimeException("expect number");
		}
		return l;
	}

	private String tryReadString() {
		int indexPre = index;
		StringBuilder b = new StringBuilder();
		char c = sql.charAt(index);

		if (c == '\'') {
			index++;
			while (index < sql.length()) {
				c = sql.charAt(index++);
				if (c == '\'') {
					if (peek("'")) {
						index++;
						b.append("'");
					} else {
						return b.toString();
					}
				} else if (c == '\\') {
					if (peek("'")) {
						index++;
						b.append("'");
					} else if (peek("\\")) {
						index++;
						b.append("\\");
					} else if (peek("n")) {
						index++;
						b.append("\n");
					} else if (peek("r")) {
						index++;
						b.append("\r");
					} else if (peek("0")) {
						index++;
						b.append("\0");
					} else if (peek("\"")) {
						index++;
						b.append("\"");
					} else if (peek("b")) {
						index++;
						b.append("\b");
					} else if (peek("t")) {
						index++;
						b.append("\t");
					} else {
						b.append(sql.charAt(index));
						index++;
					}
				} else {
					b.append(c);
				}
			}

			index = indexPre;
			return null;
		} else {
			index = indexPre;
			return null;
		}
	}

	private boolean peek(String str) {
		int i = index;
		if (i + str.length() > sql.length()) {
			return false;
		}

		for (int j = 0; j < str.length(); ++j, ++i) {
			if (sql.charAt(i) != str.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	private void readValues() {
		this.values = readValues1();
	}

	private List<Object> readValues1() {
		List<Object> vs = new ArrayList<>();
		if (sql.charAt(index) == '(') {

			index++;

			while (true) {

				vs.add(readValue());

				skipWhitespace();

				if (tryReadIgnore(",")) {
					skipWhitespace();
				} else {
					skipWhitespace();
					readIgnore(")");
					break;
				}
			}

		} else {
			throw new RuntimeException("expect (");
		}

		return vs;
	}

	private void readColumnList() {

		while (true) {
			String col = readIdentify();
			columns.add(col);

			skipWhitespace();

			if (tryReadIgnore(",")) {
				skipWhitespace();
			} else {
				break;
			}
		}

	}

	private void readColumnGroup() {
		if (sql.charAt(index) == '(') {

			index++;

			skipWhitespace();

			while (true) {

				String col = readIdentify();
				columns.add(col);

				skipWhitespace();

				if (tryReadIgnore(",")) {
					skipWhitespace();
				} else {
					skipWhitespace();
					readIgnore(")");
					break;
				}
			}
		} else {
			throw new RuntimeException("expect (");
		}

	}

	private String readIdentify() {
		StringBuilder sb = new StringBuilder();

		char c = sql.charAt(index);
		if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_') {
			sb.append(c);
		} else {
			throw new RuntimeException("expect identity");
		}

		index++;

		while (index < sql.length()) {
			c = sql.charAt(index);

			if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.' || c == '_') {
				index++;
				sb.append(c);
			} else {
				break;
			}
		}

		return sb.toString().toLowerCase();
	}

	private void skipWhitespace() {
		while (index < sql.length() && Character.isWhitespace(sql.charAt(index))) {
			index++;
		}
	}

	private boolean tryReadWhitespace() {
		boolean isWhiteSpace = Character.isWhitespace(sql.charAt(index));
		skipWhitespace();
		return isWhiteSpace;
	}

	private void readWhite() {
		if (!tryReadWhitespace()) {
			throw new RuntimeException("expect white space");
		}
	}

	private boolean tryReadIgnore(String str) {
		int ci = index;
		for (int i = 0; i < str.length(); ++i, ci++) {
			char l = Character.toUpperCase((str.charAt(i)));
			char r = Character.toUpperCase(sql.charAt(ci));

			if (l != r) {
				return false;
			}
		}

		index += str.length();
		return true;
	}

	private void readIgnore(String str) {
		if (!tryReadIgnore(str)) {
			throw new RuntimeException("expect str");
		}
	}

	public enum Type {
		INSERT, DELETE, UPDATE, SELECT_COUNT, SELECT,
	}

	public enum OP {
		LIKE, EQUAL, NOT_EQUAL, GREAT_THAN, LESS_THAN, GREAT_EQUAL, LESS_EQUAL,
	}

	public static class Filter {
	}

	public static class BasicFilter extends Filter {
		String col;
		OP op;
		Object value;

		public String getCol() {
			return col;
		}

		public OP getOp() {
			return op;
		}

		public Object getValue() {
			return value;
		}

		public BasicFilter(String col, OP op, Object value) {
			this.col = col;
			this.op = op;
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			BasicFilter filter = (BasicFilter) o;

			if (col != null ? !col.equals(filter.col) : filter.col != null)
				return false;
			if (op != filter.op)
				return false;
			return !(value != null ? !value.equals(filter.value) : filter.value != null);

		}

		@Override
		public int hashCode() {
			int result = col != null ? col.hashCode() : 0;
			result = 31 * result + (op != null ? op.hashCode() : 0);
			result = 31 * result + (value != null ? value.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "Filter{" + "col='" + col + '\'' + ", op=" + op + ", value=" + value + '}';
		}
	}

	public static class OrFilter extends Filter {

		public OrFilter(List<Filter> filters) {
			this.subFilters = filters;
		}

		List<Filter> subFilters;

		public List<Filter> getSubFilters() {
			return subFilters;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			OrFilter orFilter = (OrFilter) o;

			return subFilters.equals(orFilter.subFilters);

		}

		@Override
		public int hashCode() {
			return subFilters.hashCode();
		}

		@Override
		public String toString() {
			return "OrFilter{" + "subFilters=" + subFilters + '}';
		}
	}

}
