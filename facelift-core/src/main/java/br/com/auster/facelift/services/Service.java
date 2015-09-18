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
 * Created on Feb 24, 2005
 */
package br.com.auster.facelift.services;

import org.w3c.dom.Element;

/**
 * This interface defines the basic operations a service must implement. Since the concept of a service
 * 	is abastract enough to allow them expose virtually any kind of operation, the only method that needs
 *  to be standarized throughout all services, is the <code>init()</code>.
 *  <P> 
 * @author framos
 * @version $Id: Service.java 46 2005-05-17 14:36:02Z framos $
 */
public interface Service {

	/**
	 * Initializes the service, prior to its use. This method is called from within the service locator implementation
	 * 	before returing the service instance to the client application. 
	 * <P>
	 * In most services this method simply saves configuration information into instance variables, but its not limited
	 * 	to it.
	 * 
	 * @param _configuration the configuration of the service 
	 * @throws ConfigurationException if any error while configuration the service was detected
	 */
    public void init(Element _configuration) throws ConfigurationException;
    
}