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
 * Created on Apr 29, 2005
 */
package br.com.auster.facelift.queries.model;

import java.io.Serializable;

/**
 * Basic information class for the interactive query module. It holds the real name and the user-friendly string
 * 	of components like tables and columns.
 *  
 * @author framos
 * @version $Id$
 */
public abstract class DefinitionObject implements Serializable {

    
    
    // ----------------------
    // Instance variables
    // ----------------------
	
	private String name;
    

	
    // ----------------------
    // Constructors
    // ----------------------
	
    public DefinitionObject(String _name) {
        setName(_name);
    }

	
    
    // ----------------------
    // Public methods
    // ----------------------
	
    public final String getName() {
        return name;
    }
    public final void setName(String _name) {
        if (_name == null) {
            throw new IllegalArgumentException("cannot set name to null");
        }
        name = _name;
    }
    
    public boolean equals(Object _other) {
        try {
            DefinitionObject other = (DefinitionObject) _other;
            return getName().equals(other.getName());
        } catch (ClassCastException cce) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }
 
    public String toString() {
        return "name= '" + getName() + "' ";
    }
}
