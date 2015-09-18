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
import java.util.Map;

/**
 * Interface to standarize the methods needed to be implemented by any plugin.
 * <P>
 * The plugin context holds runtime information sensitive to the plugin point where this plugin was configured on. The 
 * 	configuration parameters are defined in configuration time, in the plugin definition resource (for example, a XML
 * 	definition file).
 * <P>
 * The behaviour of a plugin is : 
 * <LI>The plugin is only instantiated when ths plugin point where it was configured is triggered by the application 
 * 	business layer
 * <LI>When triggered, a plugin has its <code>execute()</code> method called by the current plugin executor
 * <LI>After this method is finished, the <code>cleanup()</code> is called.
 * <P>
 * 
 * @author framos
 * @version $id$
 */
public interface FaceliftPlugin extends Serializable {

    public void setContext(PluginContext _context);
    public PluginContext getContext();

    public void setConfigurationParameters(Map _parameters);
    public Map getConfigurationParameters();
    
	/**
	 * Executes the plugin logic, with the previously defined plugin context. If the context was not
	 * 	defined, and exception will be raised.
	 * <P>
	 * If any exception is caught during this logic, a <code>PluginRuntimeException</code> is thrown.
	 */
    public void execute();

	/**
	 * Executes the plugin logic, with the plugin context passed as parameter. If this context is null,
	 * and exception will be raised.
	 * <P>
	 * If any exception is caught during this logic, a <code>PluginRuntimeException</code> is thrown.
	 */
	public void execute(PluginContext _context);

	/**
	 * Resets all information holded by this plugin, which is, at least, by default, the context previously
	 * 	set to this plugin implementation.
	 */
    public void cleanup();
}
