package com.shuyun.query.process;


import com.google.common.collect.Iterables;
import com.google.common.collect.ObjectArrays;
import com.shuyun.query.meta.*;
import com.shuyun.query.parser.JsonParser;
import com.shuyun.query.result.EsResultSet;
import com.shuyun.query.result.EsResultSetForSource;
import com.shuyun.query.util.ShuyunCryptUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.common.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class SearchByPostProcessor extends PostProcessor {
    private static Logger logger = Logger.getLogger(SearchByPostProcessor.class);

	public SearchByPostProcessor(QueryContext queryContext) {
		super(queryContext);
	}

	@Override
	public ReportResultForEs process(List<?> input) {

		ReportResultForEs reportResult = new ReportResultForEs();

		reportResult.setFlag("success");
		reportResult.setMsg("ok");

		List<EsResultSetForSource> rows = (List<EsResultSetForSource>) input;
		
		reportResult.setPage(new ReportPage(0, 0));

//		HashMap<String, String> map = new HashMap<String, String>();
		/*if(parser.getSort() != null && parser.getSort().size()>0){
			for(Map<String, String> maps : parser.getSort()){
				map.put(maps.get("orderBy"), maps.get("order"));
			}
		}*/

		/*LinkedHashSet<String> sortFields = new LinkedHashSet(map.keySet());*/
		/*DataSourceConf dataSourceConf = EsQueryConf.getDataSourceInstance(queryContext.getJsonParser().getSettings().getData_source());
		reportResult.append(Iterables.toArray(Iterables.concat(dataSourceConf.getStringDimensions(),
				dataSourceConf.getNumericalDimensions(), dataSourceConf.getDecimalDimensions(), dataSourceConf.getDate())
				, String.class));
		Iterable<String> fromIterable = Iterables.concat(dataSourceConf.getStringDimensions(),
				dataSourceConf.getNumericalDimensions(), dataSourceConf.getDecimalDimensions(), dataSourceConf.getDate());
		for (final EsResultSetForSource r : rows) {
			reportResult.append(Iterables.toArray(
					Iterables.concat(
							Iterables.transform(fromIterable, new SearchDimensionTransformer(r, queryContext.getJsonParser().getSettings().getData_source()))), Object.class));
		}*/
        JsonParser jsonParser = queryContext.getJsonParser();
        String dataSource = jsonParser.getSettings().getData_source();
        List<String> needCrypt = EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt();
        boolean flagCrypt = false;
        if(EsQueryConf.getInstance().getNeedCryptIndex().contains(dataSource) && needCrypt.size() > 0 && needCrypt != null){
            flagCrypt = true;
        }

        for (final EsResultSetForSource r : rows) {
            //返回特定的column，针对特定index的column解密
            if(flagCrypt && jsonParser.getFields() != null && jsonParser.getFields().size() >  0) {
                for (String field : jsonParser.getFields()) {
                    if (needCrypt.contains(field)) {
                        getDecryptData(r, field);
                    }
                }
            } else {
                //返回所有的column
                if(flagCrypt) {
                    for (String crypt : needCrypt) {
                        getDecryptData(r, crypt);
                    }
                }
            }
            reportResult.append(r.get_source());
        }

        return reportResult;
	}

    private void getDecryptData(EsResultSetForSource r, String dataType){
        //判断是否是密文，如果不是，不作处理
        try {
            String cryptData = r.get_source().get(dataType).toString();
            if(ShuyunCryptUtil.isEncrypt(dataType, cryptData)){
                r.get_source().put(dataType, ShuyunCryptUtil.decrypt(dataType, cryptData));
            }
        } catch (Exception e) {
            logger.debug("data decrypt exception, data:[ " + r.get_source().toString() + "]");
        }
    }
}
