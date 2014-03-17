/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.argot.compiler.dictionary;

import com.argot.TypeException;
import com.argot.TypeLocation;
import com.argot.meta.MetaVersion;

public class LibraryRelation 
implements TypeLocation
{

	private int id;
	private MetaVersion version;
	private String tag;

	public LibraryRelation(int id, MetaVersion version, String tag)
	throws TypeException
	{
		this.setId(id);
		this.version = version;
		this.setTag(tag);
	}	
	
	public LibraryRelation(int id, String version, String tag)
	throws TypeException
	{
		this.setId(id);
		this.version = MetaVersion.parseVersion(version);
		this.setTag(tag);
	}
	
	
	public void setVersion(MetaVersion version) 
	{
		this.version = version;
	}
	
	public MetaVersion getVersion() 
	{
		return version;
	}

	public int getType() 
	{
		return -1;
	}


	public void setTag(String tag) 
	{
		this.tag = tag;
	}


	public String getTag() 
	{
		return tag;
	}


	public void setId(int id) 
	{
		this.id = id;
	}


	public int getId() 
	{
		return id;
	}
	
}
