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
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.meta.MetaDefinition;

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
