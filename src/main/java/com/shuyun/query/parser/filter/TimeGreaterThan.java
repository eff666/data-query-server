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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 */
public class TimeGreaterThan implements DimFilter {
	final private String dimension;
	final private String value;

	@JsonCreator
	public TimeGreaterThan(@JsonProperty("dimension") String dimension, @JsonProperty("value") String value) {
		Preconditions.checkArgument(dimension != null, "dimension must not be null");

		this.dimension = dimension;
		this.value = value;
	}

	@JsonProperty
	public String getDimension() {
		return dimension;
	}

	@JsonProperty
	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		TimeGreaterThan rhs = (TimeGreaterThan) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(dimension, rhs.dimension)
				.append(value, rhs.value).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(dimension).append(value).toHashCode();
	}

	@Override
	public String toString() {
		return String.format("%s > %s", dimension, value);
	}
}
