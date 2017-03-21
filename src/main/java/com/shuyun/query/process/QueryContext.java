package com.shuyun.query.process;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.parser.JsonParser;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class QueryContext {

    SearchSourceBuilder query;
    QueryType queryType;
    TypeReference typeRef;
    JsonParser jsonParser;

    public QueryContext() {}

    public SearchSourceBuilder getQuery() {
        return query;
    }

    public void setQuery(SearchSourceBuilder query) {
        this.query = query;
    }

    public TypeReference getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(TypeReference typeRef) {
        this.typeRef = typeRef;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public JsonParser getJsonParser() {
        return jsonParser;
    }

    public void setJsonParser(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }
}
