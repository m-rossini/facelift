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
package br.com.auster.facelift.requests.web.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.auster.facelift.persistence.PersistenceResourceAccessException;
import br.com.auster.facelift.requests.interfaces.RequestCriteria;
import br.com.auster.facelift.requests.interfaces.RequestManagerException;
import br.com.auster.facelift.requests.model.InputFile;
import br.com.auster.facelift.requests.model.OutputFile;
import br.com.auster.facelift.requests.model.Request;
import br.com.auster.facelift.requests.model.Trail;
import br.com.auster.facelift.requests.web.interfaces.WebRequestManager;
import br.com.auster.facelift.requests.web.model.WebRequest;
import br.com.auster.facelift.services.ServiceLocator;

/**
 * @author framos
 * @version $Id$
 */
public class SimpleTest {

    public static void main(String[] args) {
        
        
        System.setProperty(ServiceLocator.FACTORY_CONFIGURATION, "conf/local-test.xml");
        
        ServiceLocator.init(true);
        ServiceLocator locator = ServiceLocator.getInstance();
        
        createRequest(locator);
        //findRequest(locator);
    }
    
    
    public static void createRequest(ServiceLocator locator) {
        try {
            WebRequestManager manager = (WebRequestManager) locator.getService("service.request");

            
            WebRequest req = new WebRequest();
            req.setStartDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            req.setStatus(1);
            req.setOwnerId(10);

            // creating first proc.request
            Request newRequest = new Request();
            newRequest.setLabel("my new request");

			Map apt = new HashMap();
			apt.put("cycle", "55");
			req.setAdditionalInformation(apt);
            
            // default to all proc. requests  :: START
            Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());

            Set list = new HashSet();
            InputFile inFile = new InputFile();
            inFile.setFilename("/input/file/1");
            inFile.setRequest(newRequest);
            list.add(inFile);
            inFile = new InputFile();
            inFile.setFilename("/input/file/2");
            inFile.setRequest(newRequest);
            list.add(inFile);

            // default to all proc. requests  :: END
            
            newRequest.setInputFiles(list);
            
            ArrayList listI = new ArrayList();
            Trail trail = new Trail();
            trail.setMessage("creating request");
            trail.setStatus(1);
            trail.setTrailDate(now);
            trail.setRequest(newRequest);
            listI.add(trail);
            newRequest.setTrails(listI);           
            
            Map add = new HashMap();
            add.put("account-number", "10011001");
            add.put("asxml", "true");
            add.put("astxt", "true");
            newRequest.setAdditionalInformation(add);
            
            HashSet procs = new HashSet();
            procs.add(newRequest);

            newRequest = new Request();
            newRequest.setLabel("my other request");
            
            list = new HashSet();
            inFile = new InputFile();
            inFile.setFilename("/input/file/1");
            inFile.setRequest(newRequest);
            list.add(inFile);
            inFile = new InputFile();
            inFile.setFilename("/input/file/2");
            inFile.setRequest(newRequest);
            list.add(inFile);            
            newRequest.setInputFiles(list);
            
            listI = new ArrayList();
            trail = new Trail();
            trail.setMessage("creating request");
            trail.setStatus(1);
            trail.setTrailDate(now);
            trail.setRequest(newRequest);
            listI.add(trail);
            newRequest.setTrails(listI);           
            
            add = new HashMap();
            add.put("account-number", "20022002");
            add.put("asxml", "true");
            add.put("astxt", "true");
            newRequest.setAdditionalInformation(add);
            
            
            procs.add(newRequest);
            req.setProcessingRequests(procs);
            
            manager.createWebRequest(req);
            
			trail = new Trail();
            trail.setMessage("starting to process");
            now = new Timestamp(Calendar.getInstance().getTimeInMillis());
            trail.setTrailDate(now);
            trail.setStatus(2);
            
            OutputFile outFile = new OutputFile(); 
            outFile.setFilename("/out/file/2");
			Map attrs = new HashMap();
			attrs.put("attrib1", "value1");
			attrs.put("attrib2", "value2");
			outFile.setAttributes(attrs);
            outFile.setTrail(trail);		
			
			manager.updateRequestStatus(newRequest.getRequestId(), trail, new OutputFile[] { outFile } );
			
			manager.updateWebRequestStatus(req.getRequestId(), 3);
			
        } catch (PersistenceResourceAccessException prae) {
			prae.printStackTrace();
        } catch (RequestManagerException rme) {
            rme.printStackTrace();
        }
    }
    
    public static void findRequest(ServiceLocator locator) {
        try {
            WebRequestManager manager = (WebRequestManager) locator.getService("service.request");
            RequestCriteria criteria = new RequestCriteria();
            criteria.setLabel("%other%");
            List resultSet = manager.findWebRequestProcesses(1, criteria);
			int counter = manager.countWebRequestProcesses(1, criteria);
            System.out.println("total=" + counter);
            System.out.println("got=" + resultSet.size());
            
        } catch (PersistenceResourceAccessException prae) {
			prae.printStackTrace();
        } catch (RequestManagerException rme) {
            rme.printStackTrace();
        }
    }
    
}
