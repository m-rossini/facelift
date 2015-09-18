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
package br.com.auster.facelift.services.test;

import junit.framework.TestCase;
import br.com.auster.facelift.services.ServiceLocator;
import br.com.auster.facelift.services.plugin.PluginContext;
import br.com.auster.facelift.services.plugin.PluginService;

/**
 * @author framos
 * @version $Id$
 */
public class FullTestSuite extends TestCase {

    
    public void init() {
        if (System.getProperty(ServiceLocator.FACTORY_CONFIGURATION) == null) {
            String filename = System.getProperty("webconsole.services.test");
            if (filename == null) {
                filename = "conf/webconsole.xml";
            }
            System.setProperty(ServiceLocator.FACTORY_CONFIGURATION, filename);
        }
    }
    
    public void testServiceLocator() {
        System.setProperty(ServiceLocator.FACTORY_CONFIGURATION, "conf/webconsole.xml");
        ServiceLocator.init(true);
        ServiceLocator locator = ServiceLocator.getInstance();
        try {
            locator.getService("service.test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void testInvalidConfigurationFile() {
        System.setProperty(ServiceLocator.FACTORY_CONFIGURATION, "conf/webconsole.xml");
        try {
            ServiceLocator.init(true);
            ServiceLocator locator = ServiceLocator.getInstance();
            locator.getService("service.test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void testConfigurationFileNotFound() {
        try {
            System.setProperty(ServiceLocator.FACTORY_CONFIGURATION, "not-found.xml");
            ServiceLocator.init(true);
            ServiceLocator locator = ServiceLocator.getInstance();
            locator.getService("service.test");
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public void testPlugin() {
        try {
            ServiceLocator.init(true);
            ServiceLocator locator = ServiceLocator.getInstance();
            PluginService svc = (PluginService) locator.getPluginService();
            PluginContext ctx = new PluginContext("test.pp1", "always", null);
            svc.checkPlugins(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }    
//    
//    public static void main(String[] args) {
//        
//        if (args.length <1) {
//            System.out.println("usage AllTestSuite <testId>");
//        }
//        
//        int id = Integer.parseInt(args[0]);
//        FullTestSuite suite = new FullTestSuite();
//        suite.init();
//        switch(id) {
//            case 1 : suite.testServiceLocator(); break;
//            case 2 : suite.testInvalidConfigurationFile(); break;
//            case 3 : suite.testConfigurationFileNotFound(); break;
//            case 4 : suite.testPlugin(); break;
//        }
//    }
}
