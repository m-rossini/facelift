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

/**
 * Class responsable to control the execution of a plugin. It is initialized everytime a plugin point is triggered.
 * <P>
 * 
 * @author framos
 * @version $id$
 */
public interface PluginExecutor {

	/**
	 * Sets a plugin implementation as the plugin this executor will take care of
	 * 
	 * @param _plugin a plugin implementation
	 */
    public void setPlugin(FaceliftPlugin _plugin);
    
	/**
	 * Executes of the current defined plugin. 
	 * 
	 * @return if the execution was successful 
	 */
    public boolean execute();
	
	/**
	 * Defines the plugin implementation to be controlled, and executes it.
	 * 
	 * @param _plugin the plugin to be handled
	 * @return if the execution was successful 
	 */
    public boolean execute(FaceliftPlugin _plugin);
}
