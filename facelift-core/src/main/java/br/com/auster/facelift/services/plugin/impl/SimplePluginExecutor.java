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
 * Created on Feb 28, 2005
 */
package br.com.auster.facelift.services.plugin.impl;

import br.com.auster.facelift.services.plugin.FaceliftPlugin;
import br.com.auster.facelift.services.plugin.PluginExecutor;

/**
 * A local, non-threaded implementation of a plugin executor. This is meant as example, or for 
 * 	test only.
 * <P>
 * The implementation of this plugin executor makes all plugins, configured for the same plugin point, to run sequencially.
 * <P>
 * 
 * @author framos
 * @version $Id$
 */
public class SimplePluginExecutor implements PluginExecutor {

	

    // ----------------------
    // Instance variables
    // ----------------------
	
	private FaceliftPlugin plugin;
    
    
    
    // ----------------------
    // Constructors
    // ----------------------
	
	public SimplePluginExecutor() {
        super();
    }


	
    // ----------------------
    // Public methods
    // ----------------------
	
	/**
	 * Inherited from <code>PluginExecutor</code>
	 */
    public void setPlugin(FaceliftPlugin _plugin) {
        plugin = _plugin;
    }

	/**
	 * Inherited from <code>PluginExecutor</code>
	 */
    public boolean execute() {
        if (plugin == null) {
            throw new IllegalStateException("must set plugin before posting it to execution");
        }
        return execute(plugin);
    }

	/**
	 * Inherited from <code>PluginExecutor</code>
	 */
    public boolean execute(FaceliftPlugin _plugin) {
        _plugin.execute();
        return true;
    }

}
