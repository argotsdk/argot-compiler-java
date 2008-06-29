/*
 * Copyright 2003-2008 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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
grammar Argot;


options 
{
  output=AST;
  ASTLabelType=CommonTree;
}


@header 
{
package com.argot.compiler;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeConstructorAuto;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaFixedWidth;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaArray;

import com.argot.dictionary.Dictionary;

import com.argot.compiler.primitive.ArgotPrimitiveParser;
import com.argot.compiler.dictionary.MetaStructure;

}

@members{
/*
	private TypeLibrary _library;
	private TypeMap _map;
	private int _lastType;
	private ArgotCompiler _compiler;
	
	private boolean _validateReferences = true;
	
	public void setLibrary( TypeLibrary lib )
	{
		_library = lib;
	}
	
	public void setTypeMap( TypeMap map )
	{
		_map = map;
		_lastType = 0;
	}
	
	public void setArgotCompiler( ArgotCompiler compiler )
	{
		_compiler = compiler;
	}
	
	public void setValidateReference( boolean validate )
	{
		_validateReferences = validate;
	}

    public Object construct(Object[] objects, Class clss ) throws TypeException
    {
    	TypeConstructorAuto autoConstructor = new TypeConstructorAuto(clss);
    	return autoConstructor.construct(null,objects);
    }

	public void registerType( MetaStructure structure ) throws TypeException, RecognitionException
	{
		String typename = structure.getName();
		MetaDefinition tds = structure.getDefinition();
		// An Entry adds the newly defined entry to the system.
		try
		{
			//TypeStructure struct = new TypeStructure( _library, tds );
			//if ( !_library.isValid( typename.getText() ) || _library.isReserved( typename.getText() ) )
			int state = _library.getTypeState( typename );
			if ( state == TypeLibrary.TYPE_NOT_DEFINED || state == TypeLibrary.TYPE_RESERVED )
			{
				//if  ( _library.isReserved( typename.getText() ) )
				if ( state == TypeLibrary.TYPE_RESERVED )
				{
					_library.register( typename, tds );	
				}
				else
				{
					_library.register( typename, tds );
					_map.map( _lastType++, _library.getId( typename ) );
				}
			}
			else
			{
				System.out.println( "warning: can't redefine '" + typename +"'.  Definition ignored." );
			
				try
				{
					_map.map( _lastType++, _library.getId( typename ) );
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
			throw new RecognitionException();			
		}
		
	}
*/
}


file : (headers)? expression
	;


headers: headerline (headers)?
	{
	}
	;
	
headerline: importl | load | reserve | COMMENT
	{
	}
	;

reserve: '!'! 'reserve'^ IDENTIFIER ';'!
	;

load: '!'! 'load'^ QSTRING ';'!
	;

importl: '!'! 'import'^ IDENTIFIER ( 'as'! IDENTIFIER)? ';'!
	;
	
expression: '('^ IDENTIFIER (primary)* ')'!
	/*{ #expression = #([EXPR], id, p); }*/

	/*| LCBRACE (primary) RCBRACE*/
	;

primary: expression
	| INT^
	| QSTRING^
	| '['^ ( primary)*  ']'!
	| '#'^ IDENTIFIER
	| IDENTIFIER^ ':'! value
	;
	
value: INT^
	| QSTRING^
	;


WS: (' ' | '\t' | '\r'? '\n' )+ {$channel=HIDDEN;};

DQUOTE:	'"';
PIPE:	'|';
COMA:	',';
LBRACK:	'<';
RBRACK:	'>';
LBRACE:	'(';
RBRACE:	')';
LCBRACE: '{';
RCBRACE: '}';
ATSYM:	'@';
DOLLAR:	'$';
ASTERIX: '*';
UNDER:	'_';
HYPHEN:	'-';
PLUS: '+';
PERC:	'%';
HASH:	'#';
BANG:	'!';
COLON	:	':';
SEMI: ';';
SLBRACK	:	'[';
SRBRACK :	']';
FSLASH	:	'/';
PERIOD	:	'.';

/*DIGIT:	'0'..'9';*/

QSTRING:	'"'! ( options {greedy=false;} : . )* '"'!;

COMMENT
  : '/*' {if (input.LA(1)=='*') $type=COMMENT; else $channel=HIDDEN;} .* '*/';
  
INT	:	('0'..'9')+;

IDENTIFIER:	('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_'|'#')*;


