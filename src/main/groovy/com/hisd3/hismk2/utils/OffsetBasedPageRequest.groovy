package com.hisd3.hismk2.utils

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class OffsetBasedPageRequest implements Pageable, Serializable {
	
	private int limit
	private int offset
	private final Sort sort
	
	/**
	 * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
	 *
	 * @param offset zero-based offset.
	 * @param limit the size of the elements to be returned.
	 * @param sort can be {@literal null}.
	 */
	OffsetBasedPageRequest(int offset, int limit, Sort sort) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset index must not be less than zero!")
		}
		
		if (limit < 1) {
			throw new IllegalArgumentException("Limit must not be less than one!")
		}
		this.limit = limit
		this.offset = offset
		this.sort = sort
	}
	
	/**
	 * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
	 *
	 * @param offset zero-based offset.
	 * @param limit the size of the elements to be returned.
	 * @param direction the direction of the {@link Sort} to be specified, can be {@literal null}.
	 * @param properties the properties to sort by, must not be {@literal null} or empty.
	 */
	OffsetBasedPageRequest(int offset, int limit, Sort.Direction direction, String... properties) {
		this(offset, limit, new Sort(direction, properties))
	}
	
	/**
	 * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
	 *
	 * @param offset zero-based offset.
	 * @param limit the size of the elements to be returned.
	 */
	OffsetBasedPageRequest(int offset, int limit) {
		this(offset, limit, new Sort(Sort.Direction.ASC, "id"))
	}
	
	@Override
	int getPageNumber() {
		return offset / limit
	}
	
	@Override
	int getPageSize() {
		return limit
	}
	
	@Override
	long getOffset() {
		return offset
	}
	
	@Override
	Sort getSort() {
		return sort
	}
	
	@Override
	Pageable next() {
		return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort())
	}
	
	OffsetBasedPageRequest previous() {
		return hasPrevious() ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this
	}
	
	@Override
	Pageable previousOrFirst() {
		return hasPrevious() ? previous() : first()
	}
	
	@Override
	Pageable first() {
		return new OffsetBasedPageRequest(0, getPageSize(), getSort())
	}
	
	@Override
	boolean hasPrevious() {
		return offset > limit
	}
	
	@Override
	boolean equals(Object o) {
		if (this == o) return true
		
		if (!(o instanceof OffsetBasedPageRequest)) return false
		
		OffsetBasedPageRequest that = (OffsetBasedPageRequest) o
		
		return new EqualsBuilder()
				.append(limit, that.limit)
				.append(offset, that.offset)
				.append(sort, that.sort)
				.isEquals()
	}
	
	@Override
	int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(limit)
				.append(offset)
				.append(sort)
				.toHashCode()
	}
	
	@Override
	String toString() {
		return new ToStringBuilder(this)
				.append("limit", limit)
				.append("offset", offset)
				.append("sort", sort)
				.toString()
	}
}
