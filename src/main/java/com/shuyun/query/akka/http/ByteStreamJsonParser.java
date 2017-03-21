package com.shuyun.query.akka.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.shuyun.query.akka.http.parser.ParserStatus;
import com.shuyun.query.akka.http.parser.RowParserException;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Bytes;

public class ByteStreamJsonParser {

	private TypeReference<?> type;
	private ObjectMapper mapper;
	private JsonFactory jsonFactory;
	private List<Byte> left = new LinkedList<>();
	private boolean isFirstParser = true;
	private Integer total;


	public ByteStreamJsonParser(JsonFactory jsonFactory, ObjectMapper mapper, TypeReference<?> type) {
		this.jsonFactory = jsonFactory;
		this.mapper = mapper;
		this.type = type;
	}

	public List<Object> tryParse(List<Byte> stream) {
		List<Object> rows = new LinkedList<>();

		// 目前默认接受到hits后面，之后再做修改
		if(isFirstParser){
			String firstStr = new String(Bytes.toArray(stream));
			int position = firstStr.indexOf("hits");
			/*if(position > 0){
				int position1 = firstStr.indexOf("total", position);
				if (position1 > 0 ){
		//在这边得到总数
				}
			}*/
			int totalPosition = firstStr.indexOf("total", position);
			int totalPosition2 = firstStr.indexOf(",", totalPosition);
			total = Integer.parseInt(firstStr.substring(totalPosition + 7, totalPosition2));
			int position2 = firstStr.indexOf("hits", position + 5);
			left.addAll(stream.subList(position2 + 6 , stream.size()));
			isFirstParser = false;
		}else{
			left.addAll(stream);
			// skip 逗号
			if (left.size() > 0 && left.get(0) == 44) {
				left.remove(0);
			}

			// 添加[
			if (left.size() == 0 || left.get(0) != (byte)91) {
				left.add(0, (byte)91);
			}
		}

		ParserStatus status = ParserStatus.START;

		int parsedTo = 0;

		JsonParser jsonParser;
		try {
			jsonParser = jsonFactory.createParser(Bytes.toArray(left));
		} catch (IOException e) {
			String msg = "IOException when create parser";
			logger.error(msg, e);
			throw new RowParserException(msg, e);
		}

		while (true) {
			JsonToken token = null;
			try {
				token = jsonParser.nextToken();
			} catch (JsonParseException e) {
				break;
			} catch (IOException e) {
				throw new RowParserException("IOException when get next token");
			}

			if (ParserStatus.START == status) {
				if (token == JsonToken.START_ARRAY) {
					status = ParserStatus.ARRAY_START;
					parsedTo = jsonParser.getCurrentLocation().getColumnNr();
				} else {
					throw new RowParserException("expect '[' for the first char");
				}
			} else if (ParserStatus.ARRAY_START == status) {
				if (token == JsonToken.START_OBJECT) {
					status = ParserStatus.OBJECT_START;
				} else if(token == JsonToken.END_ARRAY) {
					break;
				} else{
					throw new RowParserException("expect '{' or ']' for the first row");
				}
			} else if (ParserStatus.OBJECT_START == status) {

				boolean isError = false;
				try {
					Object row = mapper.readValue(jsonParser, type);
					rows.add(row);
				} catch (JsonParseException | JsonMappingException e) {
					isError = true;
				} catch (IOException e) {
					throw new RowParserException("IOException when read row object", e);
				}

				if (isError) {
					break;
				} else {
					parsedTo = jsonParser.getCurrentLocation().getColumnNr();
				}

				status = ParserStatus.OBJECT_END;
			} else if (ParserStatus.OBJECT_END == status) {
				if (token == JsonToken.START_OBJECT) {
					status = ParserStatus.OBJECT_START;
				} else if (token == null) {
					break;
				} else if(token == JsonToken.END_ARRAY) {
					break;
				} else{
					throw new RowParserException("expect '{' or ']'");
				}
			} else {
				throw new RowParserException("logic error!");
			}
		}

		for (int i = 0; i < parsedTo - 1; ++i) {
			left.remove(0);
		}

		return rows;
	}

	public Integer getTotal() {
		return total;
	}

	private static Logger logger = Logger.getLogger(ByteStreamJsonParser.class);
}
