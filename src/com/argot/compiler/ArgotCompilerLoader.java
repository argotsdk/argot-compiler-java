package com.argot.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.dictionary.Dictionary;

public class ArgotCompilerLoader 
implements TypeLibraryLoader
{
	private String _resource;
	private byte[] _dictionaryData;
	
	public ArgotCompilerLoader(String resource )
	{
		_resource = resource;
		_dictionaryData = null;
	}

	private InputStream getDictionaryStream( String location )
	{
		File dictionaryFile = new File( location );
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

		InputStream is = getClass().getResourceAsStream( location );
		if (is != null)
		{
			return is;
		}
		
		ClassLoader cl = this.getClass().getClassLoader();
		is = cl.getResourceAsStream( location );
		if ( is == null )
		{
			return null;
		}				
		return is;
	}
	
	public void load(TypeLibrary library) 
	throws TypeException 
	{
		URL[] paths = null;
		
		InputStream is = getDictionaryStream( _resource );
		if ( is == null )
		{
			throw new TypeException("Failed to load:" + _resource );
		}
		
		try 
		{
			ArgotCompiler compiler = new ArgotCompiler( is, paths );
			compiler.setLoadCommon(true);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			compiler.compileDictionary( out );
			
			_dictionaryData = out.toByteArray();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw new TypeException("Failed to load argot source file: " + _resource, e);
		} 
		catch (ArgotCompilerException e) 
		{
			e.printErrors(System.err);
			throw new TypeException("Failed to load argot source file: " + _resource, e);
		}
		
		try
		{
			Dictionary.readDictionary( library, new ByteArrayInputStream(_dictionaryData) );
		}
		catch (TypeException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}
		catch (IOException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}
		
		bind(library);
	}
	
	public byte[] getDictionaryData()
	throws TypeException
	{
		if (_dictionaryData == null)
		{
			throw new TypeException("ArgotCompilerLoader - Source not compiled");
		}
		return _dictionaryData;
	}
	
	public void bind( TypeLibrary library ) 
	throws TypeException
	{
				
	}

	public String getName() 
	{
		return _resource;
	}

}
