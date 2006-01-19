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

package com.argot.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.argot.TypeBindCommon;
import com.argot.TypeException;
import com.argot.TypeLibrarySingleton;
import com.argot.TypeMap;

import com.argot.TypeLibrary;
import com.argot.dictionary.Dictionary;
import com.argot.remote.RemoteTypes;


/**
 *  This compiles a file full of types into a typemap that can be
 *  written to a typemap file.  The programmer then reads in the
 *  typemap and binds objects to the definitions.
 */
public class ArgotCompiler 
{

	public ArgotCompiler() 
	{

	}


	private void parse( TypeLibrary library, TypeMap map, InputStream in )
	throws TypeException
	{

		ArgotLexer lexer = new ArgotLexer( in );
		ArgotParser parser = new ArgotParser(lexer);
		
		parser.setLibrary( library );
		parser.setTypeMap( map );
		parser.setValidateReference( false );
		
		try
		{
			parser.file();
		}
		catch (RecognitionException e)
		{
			e.printStackTrace();
			throw new TypeException( "Specification not recognised" + e.toString() );
		}
		catch (TokenStreamException e)
		{
			throw new TypeException( "Invalid specification" + e.toString() );
		}

		
	}
	
	public InputStream getDictionaryStream( String argotHomeString, String location )
	{
		File dictionaryFile = new File( argotHomeString, location );
		if ( dictionaryFile.exists())
		{
			try
			{
				return new FileInputStream( dictionaryFile );
			}
			catch (FileNotFoundException e)
			{
				// ignore and drop through.
			}
		}

		ClassLoader cl = this.getClass().getClassLoader();
		InputStream is = cl.getResourceAsStream( location );
		if ( is == null )
		{
			System.out.println("Argot " + location + " file not found");
			System.out.println("File must be located at ARGOT_HOME/" + location );
			System.exit(-1);
		}		
		return is;
	}
	
	public void argotCompile( String[] args ) 
	throws FileNotFoundException, TypeException, IOException
	{
		System.out.println("Argot Compiler Version 1.2.0");
		System.out.println("Copyright 2004-2005 (C) Live Media Pty Ltd.");
		System.out.println("www.einet.com.au\n");
		
		if ( args.length == 0 )
		{
			System.out.println("Usage: ac <input.argot>");
			System.exit(-1);
		}
		
		ArgotCompiler compiler = new ArgotCompiler();
		TypeLibrary library = TypeLibrarySingleton.getDefault();
		
		String argotHomeString = System.getProperty("ARGOT_HOME");
		if ( argotHomeString == null )
		{
			argotHomeString = ".";
		}

		Dictionary.readDictionary( library, getDictionaryStream( argotHomeString, "argot/common.dictionary"));
		TypeBindCommon.bindCommon(library);

		Dictionary.readDictionary( library, getDictionaryStream( argotHomeString, "argot/remote.dictionary"));
		RemoteTypes.bindTypes(library);
		
		
		String inputFileName = args[0];
		String outputFileName = null;

		File inputFile = new File( inputFileName );
		if (!inputFile.exists())
		{
			System.out.println("Input File not found");
			System.out.println("Usage: ac <input.argot>");
			System.exit(-1);
		}
		
		FileInputStream fin = new FileInputStream( inputFile );
		
		int lastIndex = inputFileName.lastIndexOf(".");
		if ( lastIndex != -1 )
		{
			outputFileName = inputFileName.substring(0,lastIndex) + ".dictionary";
		}
		else
		{
			outputFileName = inputFileName + ".dictionary";
		}
		
		FileOutputStream fout = new FileOutputStream( outputFileName );
		
		TypeMap map = new TypeMap( library );			
		compiler.parse( library, map, fin );					
		Dictionary.writeDictionary( fout, map );
		
	}

	public static void main(String[] args)
	{
		try 
		{
			ArgotCompiler compiler = new ArgotCompiler();
			compiler.argotCompile( args );
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (TypeException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e )
		{
			e.printStackTrace();
		}
		catch (Exception e)
        {
            e.printStackTrace();
        }
	}
}
