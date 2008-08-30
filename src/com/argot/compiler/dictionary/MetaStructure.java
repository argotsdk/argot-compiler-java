package com.argot.compiler.dictionary;

import com.argot.meta.MetaDefinition;

public class MetaStructure 
{
	private String _name;
	private MetaDefinition _definition;
	
	public MetaStructure( String name, MetaDefinition definition )
	{
		_name = name;
		_definition = definition;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public MetaDefinition getDefinition()
	{
		return _definition;
	}
}
