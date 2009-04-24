/*
 * Copyright 2003-2009 (c) Live Media Pty Ltd. <argot@einet.com.au> 
 *
 * This software is licensed under the Argot Public License 
 * which may be found in the file LICENSE distributed 
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 * 
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject 
 * to the law of Victoria, Australia, and subject to exclusive 
 * jurisdiction of the Victorian courts.
 */
package com.argot.compiler.dictionary;

import com.argot.TypeException;
import com.argot.TypeLocation;
import com.argot.meta.MetaName;
import com.argot.meta.MetaVersion;

public class LibraryDefinition 
implements TypeLocation
{
	private MetaName name;
	private MetaVersion version;
	
	
	public LibraryDefinition(MetaName name, MetaVersion version)
	{
		this.name = name;
		this.version = version;
	}

	public LibraryDefinition(String name, String version)
	throws TypeException
	{
		this( MetaName.parseName(name), MetaVersion.parseVersion(version));
	}

	
	public void setName(MetaName name) 
	{
		this.name = name;
	}
	
	public MetaName getName() 
	{
		return name;
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
	
}
