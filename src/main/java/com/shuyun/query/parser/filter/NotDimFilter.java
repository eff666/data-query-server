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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 */
public class NotDimFilter implements DimFilter {
	final private DimFilter field;

	@JsonCreator
	public NotDimFilter(@JsonProperty("field") DimFilter field) {
		Preconditions.checkArgument(field != null,
				"NOT operator requires at least one field");
		this.field = field;
	}

	@JsonProperty("field")
	public DimFilter getField() {
		return field;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		NotDimFilter rhs = (NotDimFilter) obj;

		return new EqualsBuilder().appendSuper(super.equals(obj)).append(field, rhs.field).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(field).toHashCode();
	}

	@Override
	public String toString() {
		return String.format("!(%s)", field);
	}
}
