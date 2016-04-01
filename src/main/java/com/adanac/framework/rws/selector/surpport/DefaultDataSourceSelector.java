package com.adanac.framework.rws.selector.surpport;

import java.util.List;
import java.util.Random;

import com.adanac.framework.rws.schema.config.DsConfig;
import com.adanac.framework.rws.selector.IDataSourceSelector;

/**
 * Random 基于权重随机算法的选择器
 * @author adanac
 * @version 1.0
 */
public class DefaultDataSourceSelector implements IDataSourceSelector {

	private final Random random = new Random();

	@Override
	public DsConfig select(List<DsConfig> dsConfigs) {

		int length = dsConfigs.size(); // 总个数
		int totalWeight = 0; // 总权重
		boolean sameWeight = true; // 权重是否都一样

		for (int i = 0; i < length; i++) {
			int weight = dsConfigs.get(i).getWeight();
			totalWeight += weight; // 累计总权重
			if (sameWeight && i > 0 && weight != dsConfigs.get(i - 1).getWeight()) {
				sameWeight = false; // 计算所有权重是否一样
			}
		}
		if (totalWeight > 0 && !sameWeight) {
			// 如果权重不相同且权重大于0则按总权重数随机
			int offset = random.nextInt(totalWeight);
			// 并确定随机值落在哪个片断上
			for (int i = 0; i < length; i++) {
				offset -= dsConfigs.get(i).getWeight();
				if (offset < 0) {
					return dsConfigs.get(i);
				}
			}
		}
		// 如果权重相同或权重为0则均等随机
		return dsConfigs.get(random.nextInt(length));
	}

}