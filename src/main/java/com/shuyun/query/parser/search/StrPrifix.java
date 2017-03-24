package com.shuyun.query.parser.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class StrPrifix implements DimSearch {
    final private String dimension;
    final private String value;

    @JsonCreator
    public StrPrifix(@JsonProperty("dimension") String dimension, @JsonProperty("value") String value) {
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

        StrPrifix rhs = (StrPrifix) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(dimension, rhs.dimension)
                .append(value, rhs.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(dimension).append(value).toHashCode();
    }

    @Override
    public String toString() {
        return String.format("%s = '%s'", dimension, value);
    }
}