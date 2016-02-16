package com.adanac.framework.dac.pagination;

import java.util.List;

/**
 * 封装返回信息
 * 
 * @author adanac作者13011806@cnsuning.com
 * @param <T>
 */
public class PaginationResult<T> {
	private final List<T> result;

	private final int count;

	public PaginationResult(List<T> result, int count) {
		super();
		this.result = result;
		this.count = count;
	}

	public List<T> getResult() {
		return result;
	}

	public int getCount() {
		return count;
	}
}
