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

import com.argot.TypeLocation;
import com.argot.meta.MetaDefinition;

public class LibraryEntry 
{
	private TypeLocation _location;
	private MetaDefinition _definition;
	
	public LibraryEntry( TypeLocation location, MetaDefinition definition )
	{
		_location = location;
		_definition = definition;
	}
	
	public TypeLocation getLocation()
	{
		return _location;
	}
	
	public MetaDefinition getDefinition()
	{
		return _definition;
	}
}
