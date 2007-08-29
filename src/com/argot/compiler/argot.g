/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

header 
{
package com.argot.compiler;
}

options 
{
}

{
 
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
import com.argot.meta.MetaBasic;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaArray;

import com.argot.dictionary.Dictionary;

}

class ArgotParser extends Parser;
options 
{
	k=6;
	defaultErrorHandler=false;
}

{
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
    	return autoConstructor.construct(objects);
    }


}


file: statementlist
	{
	}
	;

statementlist: statement (statementlist)?
	{
	}
	;

statement: importl | load | entry | reserve | COMMENT
	{
	}
	;

reserve: RESERVE typename:IDENTIFIER SEMI
	{
		try
		{
			_library.reserve( typename.getText() );
			_map.map( _lastType++, _library.getId( typename.getText() ));
		}
		catch( TypeException ex )
		{
			try
			{
				_map.map( _lastType++, _library.getId( typename.getText() ));
			}
			catch( TypeException ex2 )
			{
				throw new RecognitionException( "failed to reserve " + typename.getText() );
			}
		}
	}
	;

load: LOAD file:QSTRING SEMI
	{
		// This will load the file specified.
		try
        {
        	_compiler.loadDictionary( file.getText() );
        }
        catch (FileNotFoundException e)
        {
			throw new RecognitionException( "failed to load " + file.getText() + ".\n" + e.getMessage() );
        }
        catch (TypeException e)
        {
			throw new RecognitionException( "failed to load " + file.getText() + ".\n" + e.getMessage() );
        }
        catch (IOException e)
        {
			throw new RecognitionException( "failed to load " + file.getText() + ".\n" + e.getMessage() );
        }
		
	}
	;

importl: IMPORT typename:IDENTIFIER SEMI
	{
		// This will import into the map the type specified.
		try
		{
			_map.map( _lastType++, _library.getId( typename.getText() ) );
		}
		catch( TypeException ex )
		{
			throw new RecognitionException( "failed to import " + typename.getText() );
		}
	}
	;

entry { MetaDefinition tds; }
	:	typename:IDENTIFIER COLON tds=specification SEMI
	{
		// An Entry adds the newly defined entry to the system.
		try
		{
			//TypeStructure struct = new TypeStructure( _library, tds );
			//if ( !_library.isValid( typename.getText() ) || _library.isReserved( typename.getText() ) )
			int state = _library.getTypeState( typename.getText() );
			if ( state == TypeLibrary.TYPE_NOT_DEFINED || state == TypeLibrary.TYPE_RESERVED )
			{
				//if  ( _library.isReserved( typename.getText() ) )
				if ( state == TypeLibrary.TYPE_RESERVED )
				{
					_library.register( typename.getText(), tds );	
				}
				else
				{
					_library.register( typename.getText(), tds );
					_map.map( _lastType++, _library.getId( typename.getText() ) );
				}
			}
			else
			{
				System.out.println( "warning: can't redefine '" + typename.getText() +"'.  Definition ignored." );
			
				try
				{
					_map.map( _lastType++, _library.getId( typename.getText() ) );
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
			throw new RecognitionException( "failed to map " + typename.getText() );			
		}
	}
	;

specification returns [MetaDefinition td=null;] { Object e; }
	: e=expression
	{
		td=(MetaDefinition) e;
	}
	;
	
	
	
expression returns [Object e=null;] { Object[] p; }
	: id:IDENTIFIER LBRACE p=primarylist RBRACE 
	{
		/* get the class from the id */
		try
		{
			/* too make sure it's mapped, lookup id through map */
			/*_map.getId( id.getText() );*/
			/* if anything these need to be looked up in a reference map. */
			
			Class c = _library.getClass( _library.getId( id.getText() ) );
			if ( c == null )
			{
				throw new RecognitionException( "type has no class: " + id.getText() );
			}
			e = construct( p, c );
		}
		catch( TypeException ex )
		{
			ex.printStackTrace();
		}
	}
	| LCBRACE p=primarylist RCBRACE
	{
		e = new MetaSequence( p );
	}
	;

primarylist returns [Object[] array=null;] { Object p; List l=new ArrayList(); }
	: ( p=primary { l.add(p); } ( COMA p=primary { l.add(p); } )* )?
	{
		array=l.toArray();
	}
	;

primary returns [Object p=null; Object e; ]
	: e=expression
	{
		p=e;
	}
	| i:INT
	{
		p = Integer.decode( i.getText() );
	}
	| qs:QSTRING
	{
		try
		{
			p = new String( qs.getText().getBytes(), "US-ASCII" );
		}
		catch( UnsupportedEncodingException ex )
		{
			ex.printStackTrace();
		}
	}
	| SLBRACK { List l=new ArrayList(); } ( e=primary { l.add(e); } ( COMA e=primary { l.add(e); } )* )? SRBRACK
	{
		p = l.toArray();
	}
	| HASH expr:IDENTIFIER
	{
		try
		{
			p = new Integer( _library.getId( expr.getText() )); 
		}
		catch( TypeException ex )
		{
			ex.printStackTrace();
		}
	}
	| ATSYM ref:IDENTIFIER SLBRACK nm:QSTRING SRBRACK
	{
		try
		{
			p = new MetaReference( _library.getId( ref.getText() ), nm.getText() );
		}
		catch( TypeException ex )
		{
			ex.printStackTrace();
		}
	}
	;
	

	

	

/*
 * Make sure to run antlr.Tool on the lexer.g file first!
 */

class ArgotLexer extends Lexer;
options {
        charVocabulary = '\3'..'\377';
	k=2;
}


tokens {

	IMPORT="import";
	RESERVE="reserve";
	LOAD="load";
}


WS:	(' ' | '\t' | '\n' { newline(); } | '\r' )+
		{ $setType( Token.SKIP ); }
	;

DQUOTE:	'"'
	;

PIPE:	'|'
	;
	
COMA:	','
	;

LBRACK:	'<'
	;

RBRACK:	'>'
	;

LBRACE:	'('
	;

RBRACE:	')'
	;

LCBRACE: '{'
	;
	
RCBRACE: '}'
	;
	
ATSYM:	'@'
	;

DOLLAR:	'$'
	;

ASTERIX:	'*'
	;

UNDER:	'_'
	;

HYPHEN:	'-'
	;
	
PLUS: '+'
	;

PERC:	'%'
	;

HASH:	'#'
	;


protected
DIGIT:	'0'..'9'
	;


QSTRING:	'"'! ( options {greedy=false;} : . )* '"'!
	;

/*
COMMENT:	'/' '*' ( options {greedy=false;} : . )* '*' '/'
	;
	*/
	
COMMENT
        :       "/*"
                ( { LA(2) != '/' }? '*'
                | "\r\n"                { newline(); }
                | ( '\r' | '\n' )       { newline();    }
                | ~( '*'| '\r' | '\n' )
                )*
                '*' '/' { _ttype = Token.SKIP; }               
        ;
	

INT	:	(DIGIT)+
	;

protected
IP	:	(DIGIT)+ PERIOD (DIGIT)+ PERIOD (DIGIT)+ PERIOD (DIGIT)+
	;

HEX	:	'0' 'x' (DIGIT)+
	;

IDENTIFIER:	('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_'|'#')*
	;

COLON	:	':'
	;

SEMI: ';'
	;
	
SLBRACK	:	'['
	;
 
SRBRACK :	']'
	;

FSLASH	:	'/'
	;

PERIOD	:	'.'
	;
