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

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

/**
 */
public class AndDimFilter implements DimFilter {
	private static final Joiner AND_JOINER = Joiner.on(" && ");

	final private List<DimFilter> fields;

	@JsonCreator
	public AndDimFilter(@JsonProperty("fields") List<DimFilter> fields) {

		Preconditions.checkArgument(fields.size() > 0, "AND operator requires at least one field");
		this.fields = fields;
	}

	@JsonProperty
	public List<DimFilter> getFields() {
		return fields;
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

		AndDimFilter rhs = (AndDimFilter) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(fields, rhs.fields).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(fields).toHashCode();
	}

	@Override
	public String toString() {
		return String.format("(%s)", AND_JOINER.join(fields));
	}
}
