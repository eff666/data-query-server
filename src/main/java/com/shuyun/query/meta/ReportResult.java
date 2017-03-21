package com.shuyun.query.meta;



import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;



public class ReportResult {

    private static Logger logger = Logger.getLogger(ReportResult.class);
    private String flag;
    private String msg;
    private Entity data = new Entity();

    public ReportResult() {
    }

    public static class Entity {
        private ReportPage page;
        private List<Object[]> data = new ArrayList<Object[]>();

        public ReportPage getPage() {
            return page;
        }

        public void setPage(ReportPage page) {
            this.page = page;
        }

        public List<Object[]> getData() {
            return data;
        }

        public void setData(List<Object[]> data) {
            this.data = data;
        }

        String prettyPrint() {
            StringBuilder builder = new StringBuilder();
            builder.append(new Gson().toJson(page)).append("<br />");
            Joiner joiner = Joiner.on('\t').useForNull("");
            joiner.join(Lists.newArrayList(page));
            for (Object[] objects : data) {
                builder.append(joiner.join(objects)).append("<br />");
            }

            return builder.toString();
        }

        String asHtmlTable() {
            StringBuilder builder = new StringBuilder();
            builder.append("<table border=1 width=100%><tr>");
            builder.append(new Gson().toJson(page)).append("</tr>");
            Joiner colJoiner = Joiner.on("</td><td>").useForNull("");
            builder.append("<tr><td>");
            for (Object[] objects : data) {
                builder.append(colJoiner.join(objects));
                builder.append("</td></tr><tr><td>");
            }
            builder.append("</tr></table>");

            return builder.toString();
        }
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Entity getData() {
        return data;
    }

    public void setData(Entity data) {
        this.data = data;
    }

    public void append(Object[] row) {
        data.data.add(row);
    }

    public void setPage(ReportPage page) {
        data.setPage(page);
    }

    public String prettyPrint() {
        return data.prettyPrint();
    }

    public String asHtmlTable() {
        return data.asHtmlTable();
    }

}
