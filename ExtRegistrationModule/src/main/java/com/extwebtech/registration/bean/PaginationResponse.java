package com.extwebtech.registration.bean;

import java.util.List;

import lombok.Data;

@Data
public class PaginationResponse<T> {
	private List<T> content;
	private long totalElements;
	private int totalPages;
	private int currentPage;
	private int pageSize;

	public PaginationResponse(List<T> content, long totalElements, int totalPages, int currentPage, int pageSize) {
		this.content = content;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}

}