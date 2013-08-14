/*
 * Copyright (c) 2003-2013, Live Media Pty. Ltd.
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
import com.argot.meta.DictionaryBase;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaName;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaVersion;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaCluster;

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
    
    private List<String> _errors = new ArrayList<String>();
    
    public List<String> getErrors() {
        return _errors;
    }
    

	private TypeLibrary _library;
	private TypeMap _map;
	private ArgotCompiler _compiler;
	private TypeMap _readMap;
	
	private Stack<TypeElement> _expressionStack = new Stack<TypeElement>();
	
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


}


file returns [Object f] 
  : { List list=new ArrayList(); } (l=line {if(l!=null) list.add(l); }  )+
  {
     f=list.toArray();
  }
	;
	
line returns [Object l]
  : importl | load | reserve | cluster | definition | relation | e=expression
	{
	   if (e==null) throw new RecognitionException();
	   l=e;
	}
	;

cluster: ^('cluster' clustername=IDENTIFIER )
  {
    try
    {
      MetaName name = MetaName.parseName( _library, clustername.getText() );
      _compiler.registerLibraryType( _map, new LibraryName( name ), new MetaCluster() );
    }
    catch (TypeException ex)
    {
      ex.printStackTrace();
      throw new ArgotParserException("Failed to create cluster:" + clustername.getText(), input );
    }
    
  }
  ;
  
definition: ^('definition' defname=IDENTIFIER major=INT minor=INT ( seq=sequence | exp=expression ))
  {
    short ma = Short.parseShort(major.getText());
    short mi = Short.parseShort(minor.getText());
  
    try
    {
      MetaDefinition expression = null;
      if (seq != null )
      {
        expression = (MetaDefinition) seq;
      }
      else if (exp != null) 
      {
        if (!(exp instanceof MetaDefinition))
        {
          throw new ArgotParserException("Expression not a MetaExpression type", input );
        }
        expression = (MetaDefinition) exp;
      }
      
      MetaName name = MetaName.parseName( _library, defname.getText() );
      MetaVersion version = new MetaVersion( ma, mi );
      _compiler.registerLibraryType( _map, new LibraryDefinition( name, version ), expression );
    }
    catch (TypeException ex)
    {
      ex.printStackTrace();
      throw new ArgotParserException("Failed to create name from:" + defname.getText(), input );
    }
  }
  ;

relation: ^('relation' defname=IDENTIFIER major=INT minor=INT tag=QSTRING ( exp=expression ))
  {
    short ma = Short.parseShort(major.getText());
    short mi = Short.parseShort(minor.getText());
  
    try
    {
      MetaName name = MetaName.parseName( _library, defname.getText() );
      MetaVersion version = new MetaVersion( ma, mi );
      int id = _library.getTypeId( name );
      MetaExpression metaExp = (MetaExpression) _library.getStructure( id ); 
    
      if (!(exp instanceof MetaDefinition))
      {
          throw new ArgotParserException("Expression not a MetaExpression type", input );
      }
      
      _compiler.registerLibraryType( _map, new LibraryRelation( id, version, tag.getText() ), (MetaDefinition) exp );
    }
    catch (TypeException ex)
    {
      ex.printStackTrace();
      throw new ArgotParserException("Failed to create name from:" + defname.getText(), input );
    }
  }
  ;

sequence returns [Object e]: ^(LCBRACE { List l = new ArrayList(); }  (tag=tagged  { l.add(tag); })* )
  {
    try
    {
    MetaSequence sequence = new MetaSequence( l.toArray() );
    e = sequence;
    }
    catch (IllegalArgumentException ex)
    {
      throw new ArgotParserException("Failed to create sequence", input );      
    }
  }
  ;

tagged returns [Object e]
  : ^( '@' name=IDENTIFIER argotType=IDENTIFIER )
  {
    try
    {
      int id =  _library.getTypeId( argotType.getText() ); 
      e = new MetaTag( name.getText(), new MetaReference( id ));
    }
    catch( TypeException ex )
    {
      throw new ArgotParserException("No type found for #" + argotType.getText(), input );
    }
  }
  ;

reserve: ^('reserve' typename=IDENTIFIER )
  {
  
    try
    {
      MetaName name = MetaName.parseName( _library, typename.getText() );
      _library.register( new DictionaryName( _library, typename.getText() ), new MetaIdentity() );
    }
    catch( TypeException ex )
    {
        ex.printStackTrace();
        throw new ArgotParserException( "failed to reserve " + typename.getText(), input);
    }

  }
  ;

load: ^('load' filenameText=QSTRING (classname=IDENTIFIER)? )
  {
      // This will load the file specified.
      String filename = filenameText.getText();
      
      String loadername = null;
      
      if (classname != null)
        loadername = classname.getText();
        
	  if ( "\"".equals( filename.substring(0, 1) ) )
      {
        filename = filename.substring(1, filename.length()-1 );
      }      
      try
      {
      	  System.out.println("LOADING: " + filename );
          _compiler.loadDictionary( filename, loadername );
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
      throw new ArgotParserException("Failed to import " + typename.getText(), input);
    }
  }
  ;
	
expression returns [Object e]
  : ^(LBRACE { List l = new ArrayList(); } id=IDENTIFIER ( major=INT minor=INT )?
  {
  
    try
    {
      int nid;
      if (major != null & minor != null)
      {
         short ma = Short.parseShort(major.getText());
         short mi = Short.parseShort(minor.getText());
         MetaName name = MetaName.parseName( _library, id.getText() );
         MetaVersion version = new MetaVersion( ma, mi );
         nid = _readMap.getStreamId( _library.getTypeId( id.getText(), version.toString() ));
         _expressionStack.push( _library.getStructure(_readMap.getDefinitionId(nid)));
      }
      else
      {
         nid = _readMap.getStreamId( _library.getTypeId( id.getText() ));
         _expressionStack.push( _library.getStructure(_readMap.getDefinitionId(nid)));
      }
 
    }
    catch( TypeException ex )
    {
      throw new ArgotParserException("Failed to create object for: " + id.getText() + "\n" + ex.getMessage(), input );
    }
  
  } (p=primary { l.add(p); } )* )
  {
    
  
    try
    {
      int nid;
      if (major != null & minor != null)
      {
         short ma = Short.parseShort(major.getText());
         short mi = Short.parseShort(minor.getText());
         MetaName name = MetaName.parseName( _library, id.getText() );
         MetaVersion version = new MetaVersion( ma, mi );
         nid = _readMap.getStreamId( _library.getTypeId( id.getText(), version.toString() ));
      }
      else
      {
          nid = _readMap.getStreamId( _library.getTypeId( id.getText() ));
      }
      int defId = _readMap.getDefinitionId( nid );
      Class c = _library.getClass( defId );
      if ( c == null )
      {
        throw new ArgotParserException( "type has no class bound: " + id.getText(), input );
      }
      e = construct( defId, l.toArray(), c );
      _expressionStack.pop();
    }
    catch( TypeException ex )
    {
      throw new ArgotParserException("Failed to create object for: " + id.getText() + "\n" + ex.getMessage(), input );
    }
    if ( "library.entry".equals( id.getText() ) )
    {
      try
      {
    		LibraryEntry structure = (LibraryEntry) e;
        _compiler.registerLibraryType( _map, structure.getLocation(), structure.getDefinition() );
      }
      catch(TypeException ex )
      {
        ex.printStackTrace();
        throw new ArgotParserException("Failed to register " + id.getText(), input);
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
      {
        // See if we can find the type that is defined.
        TypeElement element = _expressionStack.peek();
        if (element instanceof MetaSequence)
        {
           MetaSequence seq = (MetaSequence) element;
           for(int x=0; x<seq.size();x++)
           {
             TypeElement ele = seq.getElement(x);
             if (ele instanceof MetaTag)
             {
                MetaTag tag = (MetaTag) ele;
                if (tag.getDescription().equals(name.getText()) && tag.getExpression() instanceof MetaReference)
                {
                    MetaReference ref = (MetaReference) tag.getExpression();
                    parser = _compiler.getPrimitiveParser( _library.getName(ref.getType() ).getFullName() );
                    break;
                }
             }
           }
        }
        
        if (parser == null)
        {
          throw new ArgotParserException("No parser for :" + name.getText(), input);
        }
      }
      $p= parser.parse( s );
    }
    catch( TypeException ex )
    {
      ex.printStackTrace();
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


