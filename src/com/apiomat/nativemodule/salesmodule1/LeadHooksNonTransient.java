/*
 * Copyright (c) 2011 - 2018, Apinauten GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.apiomat.nativemodule.salesmodule1;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.apiomat.nativemodule.*;
import com.apiomat.nativemodule.basics.User;
import com.apiomat.nativemodule.mysqlaomdbusershopapp1.Employees;
import com.apiomat.nativemodule.mysqlaomdbusershopapp1.MySQLAomdbuserShopApp1;
import com.apiomat.nativemodule.salesmodule1.*;

/**
* Generated class for hooks on your Lead data model
*/

public class LeadHooksNonTransient<T extends com.apiomat.nativemodule.salesmodule1.Lead> implements com.apiomat.nativemodule.IModelHooksNonTransient<com.apiomat.nativemodule.salesmodule1.Lead>
{
    private static final Long String = null;
	protected com.apiomat.nativemodule.salesmodule1.Lead model;

    @Override
    public void setCallingModel( com.apiomat.nativemodule.salesmodule1.Lead model )
    {
        this.model = model;
    }


    /*
     * Following Methods can be used if your data model is NOT set to TRANSIENT
     */

    @Override
    public void beforePost( com.apiomat.nativemodule.salesmodule1.Lead obj, com.apiomat.nativemodule.Request r )
    {
    	obj.setLastVisit(new Date());
    	String score = (String) SalesModule1.APP_CONFIG_PROXY.getConfigValue( SalesModule1.SCORE, r.getApplicationName(), r.getSystem() ); 
    	obj.setScore(Long.valueOf(score));
    	
    	final IModel[] result = SalesModule1.AOM.findByNames( r.getApplicationName( ), Employees.MODULE_NAME, Employees.MODEL_NAME, "", r ); 
    	if(result != null && result.length >0) {
	    	Employees emp = (Employees) result[0];
	    	
	    	ContactProtocol cp = this.model.createObject(ContactProtocol.class,r);
	    	cp.setNotes(emp.getName());
	    	cp.save();
	    	
	    	obj.postContactAttempts(cp);
    	}
    	
    	this.model.log("New Lead added.");
    	
    }


    @Override
    public void afterPost( com.apiomat.nativemodule.salesmodule1.Lead obj, com.apiomat.nativemodule.Request r )
    {
    	String userEmail = r.getUserEmail();
    	List<Salesman> salesman = this.model.findByNames(Salesman.class, "userName == \""+ userEmail +"\"", r);
    	if(null!=salesman && salesman.size()>0) {
    		for (Salesman salesman2 : salesman) {
    			salesman2.postListOfLeads(obj);
    		}
    	}
    }

    @Override
    public void beforeGet( String id, com.apiomat.nativemodule.Request r )
    {
    }

    
    @Override
    public void afterGet( com.apiomat.nativemodule.salesmodule1.Lead obj, com.apiomat.nativemodule.Request r )
    {
    }

    @Override
    public String beforeGetAll( String query, com.apiomat.nativemodule.Request r )
    {
        /* NOTE that returning null or "" would ignore any query and always return any object of this class and backend */
        return query;
    }

    @Override
    public java.util.List<com.apiomat.nativemodule.salesmodule1.Lead> afterGetAll( java.util.List<com.apiomat.nativemodule.salesmodule1.Lead> objects, String query, com.apiomat.nativemodule.Request r )
    {
        /*
         * If you want to change the returned list of elements, you have to create a new list
         * and add the elements to return to it. Because getting elements from the "objects"
         * list will return a copy, changing values in this list does not have any effect.
         *
         * If NULL is returned, unnecessary conversions are omitted and result is taken from database.
         */
        return null;
    }
    @Override
    public boolean beforePut( com.apiomat.nativemodule.salesmodule1.Lead objFromDB, com.apiomat.nativemodule.salesmodule1.Lead obj, com.apiomat.nativemodule.Request r )
    {
    	Double lat = obj.getRegPlaceLatitude();
    	Double lon = obj.getRegPlaceLongitude();
    	String latstr = lat.toString();
    	String lonstr = lon.toString();
    	
    	InputStream input;
		try {
			input = new URL("https://maps.googleapis.com/maps/api/staticmap?center="+latstr+","+lonstr+"&zoom=14&size=400x400&key="+SalesModule1.APP_CONFIG_PROXY.getConfigValue( SalesModule1.GAPI, r.getApplicationName(), r.getSystem() )).openStream();
			obj.postAreaPicture(input,"staticmap","png");	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	if(!r.getIsAccountRequest()) {
        	if(objFromDB.getScore() != obj.getScore()) {
        		this.model.throwException("score modification not allowed");
        		return true;
        	} else {
        		return false;
        	}
    	}else {
    		return false;
    	}
        
    }

    @Override
    public void afterPut( com.apiomat.nativemodule.salesmodule1.Lead obj, com.apiomat.nativemodule.Request r )
    {
    }

    @Override
    public boolean beforeDelete( com.apiomat.nativemodule.salesmodule1.Lead obj, com.apiomat.nativemodule.Request r )
    {
        return false;
    }


    @Override
    public boolean beforePostData( final com.apiomat.nativemodule.salesmodule1.Lead obj, final String attributeName, final com.apiomat.nativemodule.DataWrapper dataWrapper, final com.apiomat.nativemodule.Request r )
    {
        return false;
    }

    @Override
    public void afterPostData( final com.apiomat.nativemodule.salesmodule1.Lead obj, final String attributeName, final com.apiomat.nativemodule.DataWrapper dataWrapper, final com.apiomat.nativemodule.Request r )
    {
    }

    @Override
    public String beforeGetData( final String dataId, final String attributeName, final com.apiomat.nativemodule.TranscodingConfiguration transcodingConfig, final com.apiomat.nativemodule.Request r )
    {
        return null;
    }

    @Override
    public void afterGetData( final String dataId, final String attributeName, final com.apiomat.nativemodule.DataWrapper dataWrapper, final com.apiomat.nativemodule.TranscodingConfiguration transcodingConfig, final com.apiomat.nativemodule.Request r )
    {
    }

    @Override
    public boolean beforeDeleteData( final com.apiomat.nativemodule.salesmodule1.Lead obj, final String attributeName, final com.apiomat.nativemodule.DataWrapper dataWrapper, final com.apiomat.nativemodule.Request r )
    {
        return false;
    }

    @Override
    public void afterDeleteData( final com.apiomat.nativemodule.salesmodule1.Lead obj, final String attributeName, final com.apiomat.nativemodule.DataWrapper dataWrapper, final com.apiomat.nativemodule.Request r )
    {
    }

    @Override
    public boolean beforePostRef( com.apiomat.nativemodule.salesmodule1.Lead obj, Object referencedObject, String referenceName, com.apiomat.nativemodule.Request r )
    {
        return false;
    }

    @Override
    public void afterPostRef( com.apiomat.nativemodule.salesmodule1.Lead obj, Object referencedObject, String referenceName, com.apiomat.nativemodule.Request r )
    {
    }

    @Override
    public String beforeGetAllReferences( String query, String referenceName, com.apiomat.nativemodule.Request r )
    {
        /* NOTE that returning null or "" would ignore any query and always return any referenced object */
        return query;
    }

    @Override
    public <Z extends com.apiomat.nativemodule.AbstractClientDataModel> java.util.List<Z> afterGetAllReferences( java.util.List<Z> objects, String query,
        String referenceName, com.apiomat.nativemodule.Request r )
    {
            return null; // return objects here if you changed sth; returning null prevents unnecessary conversions
    }

    @Override
    public boolean beforeDeleteRef( com.apiomat.nativemodule.salesmodule1.Lead obj, Object referencedObject, String referenceName, com.apiomat.nativemodule.Request r )
    {
        return false;
    }

    @Override
    public void afterDeleteRef( com.apiomat.nativemodule.salesmodule1.Lead obj, Object referencedObject, String referenceName, com.apiomat.nativemodule.Request r )
    {
    }

}
