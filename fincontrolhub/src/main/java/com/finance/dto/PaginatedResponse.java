package com.finance.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {
	private List<T> content;
	private int page;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean isFirst;
	private boolean isLast;
	private boolean hasNext;
	private boolean hasPrevious;
}
