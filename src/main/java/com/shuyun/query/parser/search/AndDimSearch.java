package com.shuyun.query.parser.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.shuyun.query.parser.filter.DimFilter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

public class AndDimSearch implements DimSearch {
    private static final Joiner AND_JOINER = Joiner.on(" && ");

    final private List<DimSearch> fields;

    @JsonCreator
    public AndDimSearch(@JsonProperty("fields") List<DimSearch> fields) {

        Preconditions.checkArgument(fields.size() > 0, "AND operator requires at least one field");
        this.fields = fields;
    }

    @JsonProperty
    public List<DimSearch> getFields() {
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

        AndDimSearch rhs = (AndDimSearch) obj;
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
