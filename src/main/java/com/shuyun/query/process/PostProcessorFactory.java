package com.shuyun.query.process;


import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

public class PostProcessorFactory {

	private static Logger logger = Logger.getLogger(PostProcessorFactory.class);

	public static PostProcessor create(QueryContext queryContext) {
		QueryType type = queryContext.getQueryType();
		switch (type) {
			case FILTER:
				return new FilterPostProcessor(queryContext);
			case SEARCH:
				return new SearchByPostProcessor(queryContext);
            case TRADE:
                return new TradeByPostProcessor(queryContext);
			default:
				String msg = "unknown query type:" + type + ", support[" + Joiner.on(",").join(QueryType.values());
				logger.error(msg);
				return null;
		}
	}

}
