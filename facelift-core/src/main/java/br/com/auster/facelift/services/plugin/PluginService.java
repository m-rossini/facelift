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

import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.services.Service;


/**
 * Default behaviour of a service which implements a plugin enabler service.
 * <P>
 * 
 * @author framos
 * @version $Id$
 */
public interface PluginService extends Service {
    
	/**
	 * Goes through this service's configuration looking up for plugin implementations defined in the plugin point and 
	 * 	condition value specified in the current <code>_context</code>. For each plugin implementation found, it is 
	 *  initialized and executed receiving as context the parameter of this method.
	 *  <p>
	 *     
	 * @param _context the current execution context 
	 */
    public void checkPlugins(PluginContext _context);
    
	/**
	 * Creates a new plugin executor, to control the lifecycle of a single plugin. The plugin executor is defined in
	 * 	configuration time for this service, and their creation can be controlled by an outside component, like some 
	 * 	proxy or cache component.
	 * <P>
	 * 
	 * @return a new instance of a plugin executor
	 * 
	 * @throws ConfigurationException if the plugin executor could not be initialized
	 */
    public PluginExecutor newExecutor() throws ConfigurationException;
    
}
