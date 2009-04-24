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


import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeLocation;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.auto.TypeConstructorAuto;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaAbstractMap;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaName;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaVersion;

import com.argot.compiler.dictionary.LibraryEntry;
import com.argot.compiler.dictionary.LibraryDefinition;
import com.argot.compiler.dictionary.LibraryRelation;
import com.argot.compiler.primitive.ArgotPrimitiveParser;
}




@members
{
	private TypeLibrary _library;
	private TypeMap _map;
	private ArgotCompiler _compiler;
	private TypeMap _readMap;
	
	public void setLibrary( TypeLibrary lib )
	{
		_library = lib;
	}
	
	public void setTypeMap( TypeMap map )
	{
		_map = map;
	}
	
	public void setReadTypeMap( TypeMap readMap )
	{
	 _readMap = readMap;
	}
	
	public void setArgotCompiler( ArgotCompiler compiler )
	{
		_compiler = compiler;
	}

    public Object construct(int definitionId, Object[] objects, Class clss ) throws TypeException
    {
      TypeElement structure = _library.getStructure( definitionId );
      if ( structure instanceof MetaSequence )
      {
        MetaSequence sequence = (MetaSequence) structure;
        for (int x=0; x<sequence.size();x++)
        {
          int nid = _readMap.getStreamId(sequence.getElement(x).getTypeId());
        }
        
      }
   
    	TypeConstructorAuto autoConstructor = new TypeConstructorAuto(clss);
    	return autoConstructor.construct(null,objects);
    }

	private void registerType( TypeLocation location, MetaDefinition tds )
	throws RecognitionException
	{
	
		try
		{
	    if (location instanceof LibraryDefinition)
	    {
	      LibraryDefinition libDef = (LibraryDefinition) location;
	      MetaName name = libDef.getName();
	      MetaVersion version = libDef.getVersion();
	      
	      DictionaryName libName = new DictionaryName(name);
	      int nameState = _library.getTypeState(_library.getTypeId(libName));
	      int nameId = -1;
	      if ( nameState == TypeLibrary.TYPE_NOT_DEFINED )
	      {
	         nameId = _library.register(libName, new MetaIdentity());
	      }
	      else
	      {
	         nameId = _library.getTypeId(libName);
	      }
	      
	      location = new DictionaryDefinition(nameId,name,version);
	      
	      if (!_map.isMapped(nameId))
	      {
	         //_map.map( _lastType++, nameId );
	         //_map.getStreamId( nameId );
	      }
	    }
	    else if (location instanceof LibraryRelation)
	    {
	     LibraryRelation libRel = (LibraryRelation) location;
	     MetaName name = _library.getName( libRel.getId() );
	     DictionaryDefinition dictDef = new DictionaryDefinition( libRel.getId(), name, libRel.getVersion() );
	     
	     int libRelId = _library.getTypeId( dictDef );
	     if ( !_map.isMapped(libRelId))
	     {
	       //_map.map( _lastType++, libRelId );
	       _map.getStreamId( libRelId );
	     }
	     
	     location = new DictionaryRelation( libRelId, libRel.getTag() );
	    }


			int state = _library.getTypeState( _library.getTypeId(location) );
			if ( state == TypeLibrary.TYPE_NOT_DEFINED || state == TypeLibrary.TYPE_RESERVED )
			{
				//if  ( _library.isReserved( typename.getText() ) )
				if ( state == TypeLibrary.TYPE_RESERVED )
				{
					_library.register( location, tds );	
				}
				else
				{
					int nId = _library.register( location, tds );
					_map.getStreamId( nId );
				}
			}
			else
			{
				System.out.println( "warning: can't redefine '" +"'.  Definition ignored." );
			
				try
				{
					_map.getStreamId( _library.getTypeId( location ));
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
			throw new ArgotParserException( "failed to map ", input);
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
    System.out.println("reserve not implemented" );
  /*
    try
    {
      _library.reserve( typename.getText() );
      _map.map( _lastType++, _library.getTypeId( typename.getText() ));
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
    */
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
      //_map.map( _lastType++, _library.getTypeId( typename.getText() ) );
      _readMap.getStreamId( _library.getTypeId( typename.getText() ) );
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
    try
    {
      int nid = _readMap.getStreamId( _library.getTypeId( id.getText() ));
      int defId = _readMap.getDefinitionId( nid );
      Class c = _library.getClass( defId );
      if ( c == null )
      {
        throw new ArgotParserException( "type has no class: " + id.getText(), input );
      }
      e = construct( defId, l.toArray(), c );
    }
    catch( TypeException ex )
    {
      ex.printStackTrace();
      throw new ArgotParserException("type has no class: " + id.getText(), input );
    }
    if ( "library.entry".equals( id.getText() ) )
    {
    		LibraryEntry structure = (LibraryEntry) e;
        registerType( structure.getLocation(), structure.getDefinition() );
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
      int id =  _library.getTypeId( argotType.getText() ); 
     /* if (!_map.isMapped(id))
      {
        _map.map( _lastType++, id );
      }*/
      $p = new Integer(id);
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


