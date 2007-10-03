package com.argot.compiler.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import com.argot.TypeException;
import com.argot.compiler.ArgotCompiler;

public class AntArgotCompiler
extends Task
{
	private File _input;
	private File _output;
	private Path _path;
	private boolean _loadCommon = true;
	private boolean _loadRemote = true;
	
	public void setInput( File file )
	{
		_input = file;
	}
	
	public void setOutput( File output )
	{
		_output = output;
	}
	
	public void setPath( Path path )
	{
		_path = path;
	}
	
	public void setLoadCommon( boolean load)
	{
		_loadCommon = load;
	}
	
	public void setLoadRemote( boolean load )
	{
		_loadRemote = load;
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
			
			URL[] paths = null; 
			if ( _path != null )
			{
				String[] pathStrings = _path.list();
				paths = new URL[ pathStrings.length ];
				for (int x=0;x<pathStrings.length;x++)
				{
					String pathx = pathStrings[x];
					File file = new File(pathx);
					paths[x] = file.toURI().toURL();
					System.out.println("path:" + paths[x].toExternalForm() );
				}
			}
			
			ArgotCompiler compiler = new ArgotCompiler( _input, _output, paths );
			compiler.setLoadCommon(_loadCommon);
			compiler.setLoadRemote(_loadRemote);
			compiler.doCompile();
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
