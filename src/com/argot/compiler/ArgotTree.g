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

tree grammar ArgotTree;

options
{
  tokenVocab=Argot; 
  ASTLabelType=CommonTree;
}

@header 
{
package com.argot.compiler;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
//import java.util.Vector;

//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;

import com.argot.TypeLibrary;
import com.argot.TypeMap;
//import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeConstructorAuto;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaMap;
//import com.argot.meta.MetaFixedWidth;
//import com.argot.meta.MetaExpression;
//import com.argot.meta.MetaReference;
//import com.argot.meta.MetaSequence;
//import com.argot.meta.MetaArray;

//import com.argot.dictionary.Dictionary;

import com.argot.compiler.primitive.ArgotPrimitiveParser;
import com.argot.compiler.dictionary.MetaStructure;
}




@members
{
	private TypeLibrary _library;
	private TypeMap _map;
	private int _lastType;
	private ArgotCompiler _compiler;
	
	//private boolean _validateReferences = true;
	
	public void setLibrary( TypeLibrary lib )
	{
		_library = lib;
	}
	
	public void setTypeMap( TypeMap map )
	{
		_map = map;
		_lastType = 1;
	}
	
	public void setArgotCompiler( ArgotCompiler compiler )
	{
		_compiler = compiler;
	}
	
	/*public void setValidateReference( boolean validate )
	{
		_validateReferences = validate;
	}*/

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
			throw new RecognitionException( /*"failed to map " + typename*/ );			
		}
		
	}
	
	private void registerType( String typename, MetaDefinition tds )
	throws RecognitionException
	{
		try
		{
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
			throw new ArgotParserException( "failed to map " + typename, input);
		}
	
	}
}


file returns [Object f] 
  : { List list=new ArrayList(); } (l=line {if(l!=null) list.add(l); }  )+
  {
     f=list.toArray();
  }
	;
	
line returns [Object l]
  : importl | load | reserve | e=expression
	{
	   if (e==null) throw new RecognitionException();
	   l=e;
	}
	;

reserve: ^('reserve' typename=IDENTIFIER )
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
        throw new ArgotParserException( "failed to reserve " + typename.getText(), input);
      }
    }
  }
  ;

load: ^('load' filenameText=QSTRING )
  {
      // This will load the file specified.
      String filename = filenameText.getText();
	  if ( "\"".equals( filename.substring(0, 1) ) )
      {
        filename = filename.substring(1, filename.length()-1 );
      }      
      try
      {
      	  System.out.println("LOADING: " + filename );
          _compiler.loadDictionary( filename );
      }
      catch (FileNotFoundException e)
      {
          e.printStackTrace();
          throw new ArgotParserException( "failed to load " + filename + ".\n" + e.getMessage(), input);
      }
      catch (TypeException e)
      {
          e.printStackTrace();
          throw new ArgotParserException( "failed to load " + filename + ".\n" + e.getMessage(), input);
      }
      catch (IOException e)
      {
          e.printStackTrace();
          throw new ArgotParserException( "failed to load " + filename + ".\n" + e.getMessage(), input);
      } 
  }
  ;

importl: ^('import' typename=IDENTIFIER ( alias=IDENTIFIER)? )
  {
    // This will import into the map the type specified.
    try
    {
      _map.map( _lastType++, _library.getId( typename.getText() ) );
    }
    catch( TypeException ex )
    {
      ex.printStackTrace();
      throw new ArgotParserException("failed to import " + typename.getText(), input);
    }
  }
  ;
	
expression returns [Object e]
  : ^(LBRACE { List l = new ArrayList(); } id=IDENTIFIER (p=primary { l.add(p); } )* )
  {
    //System.out.println( id.getText() );
    try
    {
      
      Class c = _library.getClass( _library.getId( id.getText() ) );
      if ( c == null )
      {
        throw new ArgotParserException( "type has no class: " + id.getText(), input );
      }
      e = construct( l.toArray(), c );
    }
    catch( TypeException ex )
    {
      ex.printStackTrace();
    }
    if ( "meta.structure".equals( id.getText() ) )
    {
    		MetaStructure structure = (MetaStructure) e;
        registerType( structure.getName(), structure.getDefinition() );
    }
    else if ( "meta.map".equals( id.getText() ) )
    {
    	try
    	{
    		MetaMap map = (MetaMap) e;
    		registerType( map.getMapTypeName( _library ), map );
    	}
    	catch (TypeException ex)
    	{
    		throw new ArgotParserException( ex.getMessage(), input );
    	}
    }

  }
  /* | LCBRACE (primary) RCBRACE */
  ;

primary returns [Object p]
  :e=expression
  {
    $p = e;
  }
  | ^(SLBRACK { List l=new ArrayList(); } (e=primary { l.add(e); } )* )
  {
     $p = l.toArray();
  }
  | ^(HASH argotType=IDENTIFIER)
  {
    try
    {
      $p = new Integer( _library.getId( argotType.getText() )); 
    }
    catch( TypeException ex )
    {
      ex.printStackTrace();
      throw new ArgotParserException("No type found for #" + argotType.getText(), input );
    }
   }
   | ^(name=IDENTIFIER s=value)
   {
    try
    {
      ArgotPrimitiveParser parser = _compiler.getPrimitiveParser( name.getText() );
      if ( parser == null )
        throw new ArgotParserException("No parser for :" + name.getText(), input);
        
      $p= parser.parse( s );
    }
    catch( TypeException ex )
    {
      throw new ArgotParserException("Unable to parse primitive:" + name.getText(), input );
    }
   }
/*	| INT
	{
	}
	| QSTRING
	{
	
	}*/
  ;
	
value returns [String v]
  : INT
  {
    $v = $INT.text;
  }
  | QSTRING
  {
     if ( "\"".equals( QSTRING2.getText().substring(0, 1) ) )
     {
        v = QSTRING2.getText().substring(1, QSTRING2.getText().length()-1 );
     }
     else
     {
        $v = $QSTRING.text;
     }
  }
  ;


