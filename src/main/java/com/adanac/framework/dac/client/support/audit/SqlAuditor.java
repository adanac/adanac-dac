package com.adanac.framework.dac.client.support.audit;
/**
 * 描述：sql跟踪接口
 */
public interface SqlAuditor {
    
    /** 
     * 描述：跟踪处理方法<br>
     */
    void audit(String sql, Object param, long interval);
}
