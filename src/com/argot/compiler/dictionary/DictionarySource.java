package com.argot.compiler.dictionary;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaAbstractMap;

public class DictionarySource 
{
	Object[] _items;
	TypeMap _map;
	int _lastType;
	
	public DictionarySource( Object[] items)
	{
		_items = items;
		_map = null;
		_lastType = 0;
	}
	
	public TypeMap buildTypeMap( TypeLibrary library) 
	throws TypeException
	{
		if (_map != null) return _map;
		
		_map = new TypeMap(library);
		_lastType = 0;
		
		for (int x=0; x<_items.length;x++)
		{
			if (_items[x] instanceof MetaStructure)
			{
				MetaStructure struct = (MetaStructure) _items[x];
				System.out.println("def for:" + struct.getName() );
				addType(library, struct.getName(), struct.getDefinition());
			}
			else if (_items[x] instanceof MetaAbstractMap)
			{
				MetaAbstractMap metaMap = (MetaAbstractMap) _items[x];
				System.out.println("def for:" + metaMap.getMapTypeName(library));
				addType(library, metaMap.getMapTypeName(library), metaMap );
			}
			else
			{
				throw new TypeException("Invalid structure found in source file:" + _items[x].getClass().getName() );
			}
		}
		return _map;
	}

	public void addType( TypeLibrary library, String typename, MetaDefinition definition ) 
	throws TypeException
	{
		// An Entry adds the newly defined entry to the system.
		try
		{
			//TypeStructure struct = new TypeStructure( _library, tds );
			//if ( !_library.isValid( typename.getText() ) || _library.isReserved( typename.getText() ) )
			int state = library.getTypeState( typename );
			if ( state == TypeLibrary.TYPE_NOT_DEFINED || state == TypeLibrary.TYPE_RESERVED )
			{
				//if  ( _library.isReserved( typename.getText() ) )
				if ( state == TypeLibrary.TYPE_RESERVED )
				{
					library.register( typename, definition );	
				}
				else
				{
					library.register( typename, definition );
					_map.map( _lastType++, library.getId( typename ) );
				}
			}
			else
			{
				System.out.println( "warning: can't redefine '" + typename +"'.  Definition ignored." );
			
				try
				{
					_map.map( _lastType++, library.getId( typename ) );
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
			throw new TypeException( "failed to map " + typename );			
		}
		
	}
				
}
