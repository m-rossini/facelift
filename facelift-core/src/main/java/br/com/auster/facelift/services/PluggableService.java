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
 * Created on 22/05/2006
 */
package br.com.auster.facelift.services;

import java.util.List;

import br.com.auster.facelift.services.plugin.PluginContext;
import br.com.auster.facelift.services.plugin.PluginService;


/**
 * @author framos
 * @version $Id$
 */
public abstract class PluggableService implements Service {

    
    
    
    /**
     * Triggers the execution of all configured plugins, according to the specified plugin point and condition value.
     * This method is be called whenever the service class requires that plugins are executed. 
     * <P>   
     * The decision on wether or not validate the condition value is up to the plugin service, which will be based on
     * its configuration file.
     * <P>  
     * 
     * @param _pluginToken the plugin point identifier
     * @param _operationValue the value used as condition for this plugin point 
     * @param _parameters the list of parameters to be passed to each plugin triggered
     */
    public void callPlugins(String _pluginToken, String _conditionValue, List _parameters) {
        ServiceLocator locator = ServiceLocator.getInstance();
        PluginService service = locator.getPluginService();
        PluginContext context = new PluginContext();
        context.setPluginPoint(_pluginToken);
        context.setConditionValue(_conditionValue);
        context.setExecutionParameters(_parameters);
        service.checkPlugins(context);
    }    
}
