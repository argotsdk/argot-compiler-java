package com.argot.compiler.primitive;

import com.argot.TypeException;

public interface ArgotPrimitiveParser 
{
	Object parse( String data ) throws TypeException;
}
