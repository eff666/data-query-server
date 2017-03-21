/*
 * Druid - a distributed column store.
 * Copyright (C) 2012, 2013  Metamarkets Group Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.shuyun.query.parser.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = { 
		@JsonSubTypes.Type(name = "and", value = AndDimFilter.class),
		@JsonSubTypes.Type(name = "or", value = OrDimFilter.class),
		@JsonSubTypes.Type(name = "not", value = NotDimFilter.class),
		@JsonSubTypes.Type(name = "str_not_selector", value = StrNotSelector.class),
		@JsonSubTypes.Type(name = "str_selector", value = StrSelector.class),
		@JsonSubTypes.Type(name = "gt", value = GreaterThan.class),
		@JsonSubTypes.Type(name = "gte", value = GreaterOrEqual.class),
		@JsonSubTypes.Type(name = "tgt", value = TimeGreaterThan.class),
		@JsonSubTypes.Type(name = "tgte", value = TimeGreaterOrEqual.class),
		@JsonSubTypes.Type(name = "lt", value = LittleThan.class),
		@JsonSubTypes.Type(name = "lte", value = LittleOrEqual.class),
		@JsonSubTypes.Type(name = "tlt", value = TimeLittleThan.class),
		@JsonSubTypes.Type(name = "tlte", value = TimeLittleOrEqual.class),
		@JsonSubTypes.Type(name = "eq", value = Equal.class), 
		@JsonSubTypes.Type(name = "neq", value = NotEqual.class),
		@JsonSubTypes.Type(name = "num_in", value = NumIn.class),
		@JsonSubTypes.Type(name = "num_not_in", value = NumNotIn.class),
		@JsonSubTypes.Type(name = "str_in", value = StrIn.class),
		@JsonSubTypes.Type(name = "str_not_in", value = StrNotIn.class),
        @JsonSubTypes.Type(name = "str_match", value = StrMatchForFilter.class)
		})
public interface DimFilter {
}
