/*
 *  Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
package br.com.auster.facelift.services.plugin.impl;

import java.util.Map;

import br.com.auster.facelift.services.plugin.FaceliftPlugin;
import br.com.auster.facelift.services.plugin.PluginContext;

/**
 * A simple plugin implementation, to save the coding effort to define the context and configuration
 * 	attributes. Also, it has a default behaviour of the <code>execute(PluginContext)</code> method, which
 *  is to set the context attribute, and call the <code>execute()</code>. And, the <code>cleanup()</code>
 *  method has a default behaviour to set the context attribute to <code>null</code>.
 *  <P>
 *  
 * @author framos
 * @version $Id: DefaultPluginImpl.java 50 2005-05-17 14:37:44Z framos $
 */
public abstract class DefaultPluginImpl implements FaceliftPlugin {

	

    // ----------------------
    // Instance variables
    // ----------------------
	
	private PluginContext context;
	private Map configuration; 
	
	
	
    // ----------------------
    // Public methods
    // ----------------------
	
	/**
	 * Inherited from <code>FaceliftPlugin</code>
	 */
	public void setContext(PluginContext _context) {
		context = _context;
	}

	/**
	 * Inherited from <code>FaceliftPlugin</code>
	 */
	public PluginContext getContext() {
		return context;
	}

	/**
	 * Inherited from <code>FaceliftPlugin</code>
	 */
	public void setConfigurationParameters(Map _parameters) {
		configuration = _parameters;
	}

	/**
	 * Inherited from <code>FaceliftPlugin</code>
	 */
	public Map getConfigurationParameters() {
		return configuration;
	}

	/**
	 * Inherited from <code>FaceliftPlugin</code>
	 */
	public void execute(PluginContext _context) {
		setContext(_context);
		execute();
	}
	
	/**
	 * Inherited from <code>FaceliftPlugin</code>
	 */
	public void cleanup() {
		context = null;
	}
}
