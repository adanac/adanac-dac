package com.adanac.framework.rws.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionUtils {
	/**
	 * 降序排列Integer List
	 * @return
	 */
	public static List<Integer> sortDesc(List<Integer> list) {

		Collections.sort(list, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 <= o2) {
					return 1;
				}
				return 0;
			}
		});

		return list;
	}

}