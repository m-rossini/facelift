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
package br.com.auster.facelift.services.test;

import java.util.Map;

import br.com.auster.facelift.services.plugin.FaceliftPlugin;
import br.com.auster.facelift.services.plugin.PluginContext;

/**
 * @author framos
 * @version $Id$
 */
public class TestPlugin implements FaceliftPlugin {

    
    private PluginContext ctx; 

    public TestPlugin() {
    }

    public void setContext(PluginContext _context) {
        ctx = _context;
    }

	public PluginContext getContext() {
		return this.ctx;
	}
	
    public void setConfigurationParameters(Map _parameters) {
    }

    public Map getConfigurationParameters() {
        return null;
    }

    public void execute() {
        if (ctx == null) {
            throw new IllegalStateException("cannot execute plugin without a context");
        }
        execute(ctx);
    }

    public void execute(PluginContext _context) {
        System.out.println("plugin executed : 1");
    }

    public void cleanup() {
        ctx = null;
    }



}
