package com.shuyun.query.process;

import java.util.List;
import com.shuyun.query.meta.ReportResultForEs;
import com.shuyun.query.parser.JsonParser;

public abstract class PostProcessor {

	final QueryContext queryContext;

	public PostProcessor(QueryContext queryContext) {
		this.queryContext = queryContext;
	}

	abstract public ReportResultForEs process(List<?> input);
}
