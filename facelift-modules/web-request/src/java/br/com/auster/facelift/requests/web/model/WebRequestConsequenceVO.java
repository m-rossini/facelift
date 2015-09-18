/**
 * 
 */
package br.com.auster.facelift.requests.web.model;

/**
 * VO para trasnport de valor do WebRequestConsequence.
 * 
 * @author gbrandao
 *
 */
public class WebRequestConsequenceVO {

    /**
     * default contructor
     */
    public WebRequestConsequenceVO() {
	// TODO Auto-generated constructor stub
    }
    
    private String ruleCode;
    private String ruleName;
    private long count;
    
    
    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }
    /**
     * @param count the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }
    /**
     * @return the ruleCode
     */
    public String getRuleCode() {
        return ruleCode;
    }
    /**
     * @param ruleCode the ruleCode to set
     */
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }
    /**
     * @return the ruleName
     */
    public String getRuleName() {
        return ruleName;
    }
    /**
     * @param ruleName the ruleName to set
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
