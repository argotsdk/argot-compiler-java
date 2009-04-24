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
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaAbstractMap;

public class LibraryList 
{
	Object[] _items;
	TypeMap _map;
	int _lastType;
	
	public LibraryList( Object[] items)
	{
		_items = items;
		_map = null;
		_lastType = 0;
	}
	
	public TypeMap buildTypeMap( TypeLibrary library) 
	throws TypeException
	{
		if (_map != null) return _map;
		
		_map = new TypeMap(library, null);
		_lastType = 0;
		
		for (int x=0; x<_items.length;x++)
		{
			// TODO fix this.  
/*
			if (_items[x] instanceof MetaStructure)
			{
				MetaStructure struct = (MetaStructure) _items[x];
				System.out.println("def for:" + struct.getName() );
				addType(library, struct.getName(), struct.getVersion(), struct.getDefinition());
			}
			else if (_items[x] instanceof MetaAbstractMap)
			{
				MetaAbstractMap metaMap = (MetaAbstractMap) _items[x];
				System.out.println("def for:" + metaMap.getMapTypeName(library));
				// TODO null version is incorrect.
				addType(library, metaMap.getMapTypeName(library), null, metaMap );
			}
			else
			{
				throw new TypeException("Invalid structure found in source file:" + _items[x].getClass().getName() );
			}
*/
		}
		return _map;
	}

	public void addType( TypeLibrary library, TypeLocation location, MetaDefinition definition ) 
	throws TypeException
	{
		// An Entry adds the newly defined entry to the system.
		try
		{
			//TypeStructure struct = new TypeStructure( _library, tds );
			//if ( !_library.isValid( typename.getText() ) || _library.isReserved( typename.getText() ) )
			int state = library.getTypeState( library.getTypeId( location ));
			if ( state == TypeLibrary.TYPE_NOT_DEFINED || state == TypeLibrary.TYPE_RESERVED )
			{
				//if  ( _library.isReserved( typename.getText() ) )
				if ( state == TypeLibrary.TYPE_RESERVED )
				{
					library.register( location, definition );	
				}
				else
				{
					library.register( location, definition );
					_map.map( _lastType++, library.getTypeId( location ) );
				}
			}
			else
			{
				System.out.println( "warning: can't redefine '" + library.getName(library.getTypeId(location)) +"'.  Definition ignored." );
			
				try
				{
					_map.map( _lastType++, library.getTypeId( location ) );
				}
				catch( TypeException ex )
				{
					System.out.println( "import into map failed" );
				}
			
			}
		}
		catch( TypeException ex )
		{
			ex.printStackTrace();
			throw new TypeException( "failed to map " + location.toString() );			
		}
		
	}
				
}
