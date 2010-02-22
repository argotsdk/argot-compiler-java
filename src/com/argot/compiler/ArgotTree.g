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
import com.argot.TypeLocationBase;
import com.argot.TypeLocationName;
import com.argot.TypeLocationDefinition;
import com.argot.TypeLocationRelation;
import com.argot.auto.TypeConstructorAuto;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaAbstractMap;
import com.argot.meta.DictionaryBase;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaName;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaVersion;

import com.argot.compiler.dictionary.LibraryBase;
import com.argot.compiler.dictionary.LibraryName;
import com.argot.compiler.dictionary.LibraryEntry;
import com.argot.compiler.dictionary.LibraryDefinition;
import com.argot.compiler.dictionary.LibraryRelation;
import com.argot.compiler.primitive.ArgotPrimitiveParser;
}




@members
{
  public String getErrorMessage(RecognitionException e, String[] tokenNames) 
  {
    if ( e instanceof ArgotParserException )
    {
      ArgotParserException ex = (ArgotParserException) e;
      return ex.getReason();
    }
    else
    {
      return super.getErrorMessage( e, tokenNames );
    }
  }
  
  public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        //System.err.println("ArgotTree: " + getErrorMessage( e, tokenNames ));
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        _errors.add(hdr + " " + msg);
        // Now do something with hdr and msg...
    }
    
    public String getErrorHeader(RecognitionException e) {
       return "ERROR(" + (e.approximateLineInfo?"after ":"")+e.line+":"+e.charPositionInLine + ")";
    }
      
    public void emitErrorMessage(String msg) {
        System.err.println("ArgotTree error: " + msg);
    }
    
    private List _errors = new ArrayList();
    
    public List getErrors() {
        return _errors;
    }
    

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
	    else if (location instanceof LibraryName)
	    {
	      LibraryName libName = (LibraryName) location;
	      
	      location = new DictionaryName( libName.getName() );
	    }
	    else if (location instanceof LibraryBase)
	    {
	      LibraryBase libBase = (LibraryBase) location;
	      location = new DictionaryBase();
	    }

      int id = _library.getTypeId(location);
			int state = _library.getTypeState( id );
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
			   if (location instanceof TypeLocationDefinition)
			   {
			      TypeLocationDefinition libDef = (TypeLocationDefinition) location;
			      String name = libDef.getName().getFullName() + ":" + libDef.getVersion().toString();
          System.out.println( "WARNING: can't redefine '"+name +"'.  Definition ignored." );
			   }
			   else if (location instanceof TypeLocationRelation)
			   {
			      TypeLocationRelation libRel = (TypeLocationRelation) location;
			      String name = _library.getName( libRel.getId() ).getFullName() + ":" + libRel.getTag();
            System.out.println( "WARNING: can't redefine '"+ name +"'.  Definition ignored." );
			   }
			   else if (location instanceof TypeLocationName)
			   {
			      TypeLocationName libName = (TypeLocationName) location;
			      String name = libName.getName().getFullName();
            System.out.println( "WARNING: can't redefine '"+ name +"'.  Definition ignored." );
			   }
			   else if (location instanceof TypeLocationBase)
			   {
           System.out.println( "WARNING: can't redefine library base." );
			   }
			   else
			   {
			     System.out.println( "WARNING: can't redefine library type of unknown location type: "+location.getClass().getName() );
			   }
			
			
				try
				{
					_map.getStreamId( _library.getTypeId( location ));
				}
				catch( TypeException ex )
				{
					System.out.println( "import into map failed - " + ex.getMessage() );
				}
			
			}
		}
		catch( TypeException ex )
		{
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
  /*
    #name references the identity type identifier.
  */
  | ^(HASH argotType=IDENTIFIER)
  {
    try
    {
      int id =  _library.getTypeId( argotType.getText() ); 
     /* if (!_map.isMapped(id))
      {
        _map.map( _lastType++, id );
      }*/
      //int id = _map.getStreamId( argotType.getText() );
      //id = _map.getDefinitionId(id);
      $p = new Integer(id);
    }
    catch( TypeException ex )
    {
      throw new ArgotParserException("No type found for #" + argotType.getText(), input );
    }
   }
   /*
      $name refereces the specific version of a type.
   */
  | ^(DOLLAR argotType=IDENTIFIER)
  {
    try
    {
     /* int id =  _library.getTypeId( argotType.getText() ); */
     /* if (!_map.isMapped(id))
      {
        _map.map( _lastType++, id );
      }*/
      int id = _map.getStreamId( argotType.getText() );
      id = _map.getDefinitionId(id);
      $p = new Integer(id);
    }
    catch( TypeException ex )
    {
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


