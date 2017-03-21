package com.shuyun.query.parser.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.shuyun.query.parser.filter.DimFilter;
import com.shuyun.query.parser.filter.NotDimFilter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created by shuyun on 2016/10/19.
 */
public class NotDimSearch implements DimSearch{
    final private DimSearch field;

    @JsonCreator
    public NotDimSearch(@JsonProperty("field") DimSearch field) {
        Preconditions.checkArgument(field != null,
                "NOT operator requires at least one field");
        this.field = field;
    }

    @JsonProperty("field")
    public DimSearch getField() {
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

        NotDimSearch rhs = (NotDimSearch) obj;

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
