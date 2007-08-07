package com.argot.compiler.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.argot.TypeException;
import com.argot.compiler.ArgotCompiler;

public class AntArgotCompiler
extends Task
{
	private File _input;
	private File _output;
	
	public void setInput( File file )
	{
		_input = file;
	}
	
	public void setOutput( File output )
	{
		_output = output;
	}
	
	public void execute() 
	throws BuildException
	{
		try
		{
			if ( _input == null || !_input.exists() )
			{
				throw new BuildException("input file not supplied or doesn't exist");
			}
			
			if ( _output == null )
			{
				int lastIndex = _input.getName().lastIndexOf(".");
				if ( lastIndex != -1 )
				{
					_output = new File( _input.getParent(), _input.getName().substring(0,lastIndex) + ".dictionary" );
				}
				else
				{
					_output = new File( _input.getParent(), _input.getName() + ".dictionary" );
				}
			}

			if ( _output.lastModified() > _input.lastModified() )
			{
				// We don't need to recompile. exit task.
				return;
			}
			
			ArgotCompiler compiler = new ArgotCompiler();
			compiler.doCompile( _input, _output );
		}
		catch (FileNotFoundException e)
		{
			throw new BuildException(e);
		}
		catch (TypeException e)
		{
			throw new BuildException(e);
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
		
	}
}
