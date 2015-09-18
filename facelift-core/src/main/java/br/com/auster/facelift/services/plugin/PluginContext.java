/*
/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on Feb 26, 2005
 */
package br.com.auster.facelift.services.plugin;

import java.io.Serializable;
import java.util.List;

/**
 * Default implementation of a plugin context. It holds all runtime information to 
 * 	be used by the plugin when it is executed. 
 *  <P>
 * These informations are : the plugin point, the condition value of the plugin point and
 * 	all sensitive infomration related to this specific plugin point.
 * <P>
 * 
 * @author framos
 * @version $id$
 */
public class PluginContext implements Serializable {

	
	
    // ----------------------
    // Instance variables
    // ----------------------

	private String pluginPoint;
    private String value;
    private List params;
	
	
	
    // ----------------------
    // Constructors
    // ----------------------

	public PluginContext() {
    }
    
    public PluginContext(String _pluginPoint, String _value, List _params) {
        setPluginPoint(_pluginPoint);
        setConditionValue(_value);
        setExecutionParameters(_params);
    }

	
    
    // ----------------------
    // Public methods
    // ----------------------
	
    public final void setPluginPoint(String _pluginPoint) {
        pluginPoint = _pluginPoint;
    }

    public final String getPluginPoint() {
        return pluginPoint;
    }
    
    public final void setConditionValue(String _value) {
        value = _value;
    }

    public final String getConditionValue() {
        return value;
    }
    
    public final void setExecutionParameters(List _params) {
        params = _params;
    }
    
    public final List getExecutionParameters() {
        return params;
    }
}
