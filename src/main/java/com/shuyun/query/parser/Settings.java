package com.shuyun.query.parser;

public class Settings {
    private String query_id;
    private String data_source;
    private Pagination pagination;
    private String return_format = "json";

    public static class Pagination{
        private int limit = 20;
        private int offset;

        public Pagination(){};

        public int getOffset() {
            return offset;
        }
        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        @Override
        public String toString() {
            return "Pagination{" +
                    "limit=" + limit +
                    ", offset=" + offset +
                    '}';
        }
    }

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }


    public String getReturn_format() {
        return return_format;
    }

    public void setReturn_format(String return_format) {
        this.return_format = return_format;
    }

    public String getQuery_id() {
        return query_id;
    }

    public void setQuery_id(String query_id) {
        this.query_id = query_id;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "query_id='" + query_id + '\'' +
                ", data_source='" + data_source + '\'' +
                ", pagination=" + pagination +
                ", return_format='" + return_format + '\'' +
                '}';
    }
}
