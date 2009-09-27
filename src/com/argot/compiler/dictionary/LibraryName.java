package com.argot.compiler.dictionary;

import com.argot.TypeLocation;
import com.argot.meta.MetaName;

public class LibraryName
implements TypeLocation
{
	private MetaName name;
	
	public LibraryName(MetaName name)
	{
		this.name = name;
	}

	public void setName(MetaName name) 
	{
		this.name = name;
	}
	
	public MetaName getName() 
	{
		return name;
	}
	
	public int getType() 
	{
		return -1;
	}
	
}
