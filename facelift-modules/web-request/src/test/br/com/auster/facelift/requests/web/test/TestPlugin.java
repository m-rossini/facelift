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
 * Created on Apr 8, 2005
 */
package br.com.auster.facelift.requests.web.test;

import java.util.Map;

import br.com.auster.facelift.services.plugin.FaceliftPlugin;
import br.com.auster.facelift.services.plugin.PluginContext;

public class TestPlugin extends Object implements FaceliftPlugin {


    PluginContext context;
    
    
    public void setContext(PluginContext arg0) {
        context = arg0;
    }

    public PluginContext getContext() {
        return context;
    }
	
    public void setConfigurationParameters(Map arg0) {
    }
    public Map getConfigurationParameters() {
        return null;
    }

    public void execute() {
        System.out.println("calling plugin :" + context.getPluginPoint());
    }

    public void execute(PluginContext arg0) {
        setContext(arg0);
        execute();
    }

    public void cleanup() {
        context = null;
    }

}
