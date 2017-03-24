package com.shuyun.query.parser.sql;

/*import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import sql.Main.BasicFilter;
import sql.Main.Filter;
import sql.Main.OP;
import sql.Main.OrFilter;
import sql.Main.Type;*/

public class MainTest {



	/*@Test
	public void testInsert() {
		Main main = new Main("insert into tab1( a ,b,c, d, e) values (1, 2, null, 'ab\\'c', 'e''fg')");
		main.parse();

		Assert.assertTrue(main.type == Type.INSERT);
		Assert.assertTrue(main.tableName.equals("tab1"));
		Assert.assertTrue(main.columns.equals(Arrays.asList("a", "b", "c", "d", "e")));
		Assert.assertTrue(main.values.equals(Arrays.asList(1L, 2L, null, "ab'c", "e'fg")));
	}

	@Test
	public void testDelete() {
		Main main = new Main("delete from tab1    where id = 'xyz'");
		main.parse();

		Assert.assertTrue(main.type == Type.DELETE);
		Assert.assertTrue(main.tableName.equals("tab1"));
		Assert.assertTrue(main.idColumnName.equals("id"));
		Assert.assertTrue(main.idColumnValue.equals("xyz"));
	}

	@Test
	public void testUpdate() {
		Main main = new Main("update tab1 set a  = '''abc' , b = 123 where id = 'xy''z'");
		main.parse();

		Assert.assertTrue(main.type == Type.UPDATE);
		Assert.assertTrue(main.tableName.equals("tab1"));
		Assert.assertTrue(main.idColumnName.equals("id"));
		Assert.assertTrue(main.idColumnValue.equals("xy'z"));
		Assert.assertTrue(main.sets
				.equals(Arrays.asList(new Pair<String, Object>("a", "'abc"), new Pair<String, Object>("b", 123L))));
	}

	@Test
	public void testSelectCount() {
		Main main = new Main("select count( *) from a");
		main.parse();

		Assert.assertTrue(main.type == Type.SELECT_COUNT);
		Assert.assertTrue(main.tableName.equals("a"));
	}

	@Test
	public void testSelect() {
		Main main = new Main(
				"select a, b,c from tabb where d like 'abc' and colf=null and x!= null and y<>null and col3 != 3 and col4 <> 5 and colx <= '5' and "
						+ "coly >= 5 and colx < '5' and colz > '5' and coly is null and colxx is not null and col66 in ('5', 6)"
						+ " order by a,b,c  limit 2 offset 1");
		main.parse();

		Assert.assertTrue(main.type == Type.SELECT);
		Assert.assertTrue(main.columns.equals(Arrays.asList("a", "b", "c")));
		Assert.assertTrue(main.tableName.equals("tabb"));

		Assert.assertTrue(main.filters
				.equals(Arrays.asList(new BasicFilter("d", OP.LIKE, "abc"), new BasicFilter("colf", OP.EQUAL, null),
						new BasicFilter("x", OP.NOT_EQUAL, null), new BasicFilter("y", OP.NOT_EQUAL, null),
						new BasicFilter("col3", OP.NOT_EQUAL, 3L), new BasicFilter("col4", OP.NOT_EQUAL, 5L),
						new BasicFilter("colx", OP.LESS_EQUAL, "5"), new BasicFilter("coly", OP.GREAT_EQUAL, 5L),
						new BasicFilter("colx", OP.LESS_THAN, "5"), new BasicFilter("colz", OP.GREAT_THAN, "5"),
						new BasicFilter("coly", OP.EQUAL, null), new BasicFilter("colxx", OP.NOT_EQUAL, null),
						new OrFilter(Arrays.<Filter> asList(new BasicFilter("col66", OP.EQUAL, "5"),
								new BasicFilter("col66", OP.EQUAL, 6L))))));
		Assert.assertTrue(2 == main.limit);
		Assert.assertTrue(1 == main.offset);

	}*/
}
