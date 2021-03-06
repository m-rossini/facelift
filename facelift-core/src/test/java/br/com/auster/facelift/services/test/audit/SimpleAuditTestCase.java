/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
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
 * Created on 18/09/2006
 */
package br.com.auster.facelift.services.test.audit;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.audit.log4j.SimpleLog4jAuditLog;
import junit.framework.TestCase;

/**
 * @author framos
 * @version $Id$
 */
public class SimpleAuditTestCase extends TestCase {

	
	public void testOnConsole() {
		try {
			Element conf = DOMUtils.openDocument("audit/auditlog.xml", false);
			SimpleLog4jAuditLog audit = new SimpleLog4jAuditLog();
			audit.init(conf);
			audit.audit("testing message");
			Logger thislog = Logger.getLogger(SimpleAuditTestCase.class);
			thislog.info("should not display this message");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
