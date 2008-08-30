package com.argot.compiler.dictionary;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeReaderAuto;
import com.argot.common.UInt16;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaAbstractMap;
import com.argot.meta.MetaMarshaller;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class DictionarySourceLoader 
implements TypeLibraryLoader
{
	public static final String DICTIONARY = "dictionary_source.dictionary";
	
	public String getName()
	{
		return DICTIONARY;
	}

	public void load( TypeLibrary library ) throws TypeException
	{
		MetaDefinition dSourceItem = new MetaAbstract();
		library.register( "dictionary.source.item", dSourceItem, new MetaMarshaller(), new MetaMarshaller(), null );
		
		MetaDefinition dSource = new MetaSequence( new MetaExpression[] {
				new MetaArray(
					new MetaReference(library.getId(UInt16.TYPENAME)),
					new MetaReference(library.getId("dictionary.source.item"))
				)});
		library.register( "dictionary.source", dSource, new TypeReaderAuto(DictionarySource.class), new MetaMarshaller(), DictionarySource.class );
		
		MetaDefinition dSourceStructure =
				new MetaSequence( 
					new MetaExpression[] {
						new MetaTag( "name", new MetaReference(library.getId("meta.name"))),
						new MetaTag( "definition", new MetaReference(library.getId("meta.definition")))
					}
				);
		library.register( "meta.structure", dSourceStructure, new TypeReaderAuto(MetaStructure.class), new MetaMarshaller(), MetaStructure.class );

		MetaAbstractMap dSourceToStructure = new MetaAbstractMap( library.getId("dictionary.source.item"), library.getId("meta.structure"));
		library.register( dSourceToStructure.getMapTypeName(library), dSourceStructure, new MetaMarshaller(), new MetaMarshaller(), null );
		MetaAbstractMap dSourceToMap = new MetaAbstractMap( library.getId("dictionary.source.item"), library.getId("meta.abstract.map"));
		library.register( dSourceToMap.getMapTypeName(library), dSourceStructure, new MetaMarshaller(), new MetaMarshaller(), null );
    }



}
