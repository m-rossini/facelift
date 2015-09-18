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
 * Created on Sep 05, 2005
 */
package br.com.auster.facelift.queries.model;

/**
 * @author framos
 * @version $Id: VisibleDefinitionObject.java 158 2005-11-04 13:03:09Z framos $
 */
public abstract class VisibleDefinitionObject extends DefinitionObject {

	
	
    // ----------------------
    // Instance variables
    // ----------------------
	
    private String display;

	
	
    // ----------------------
    // Constructors
    // ----------------------
	
    public VisibleDefinitionObject(String _name) {
        super(_name);
    }
	
	
	
    // ----------------------
    // Public methods
    // ----------------------
	
    public final String getDisplayCaption() {
        return display;
    }
    public final void setDisplayCaption(String _caption) { 
        display = _caption;
    }
	
	public String toString() {
		return super.toString() + 
         " display= '" + getDisplayCaption() + "'";
	}
}
