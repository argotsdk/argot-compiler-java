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
package com.argot.compiler.dictionary;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.auto.TypeReaderAuto;
import com.argot.common.UInt16;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaAbstractMap;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaMarshaller;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class LibraryLoader 
implements TypeLibraryLoader
{
	public static final String DICTIONARY = "library_source.dictionary";
	
	public String getName()
	{
		return DICTIONARY;
	}

	public void load( TypeLibrary library ) throws TypeException
	{
		/*
		 * (library.entry
		 * 	  (library.definition u8ascii:"library.definition" u8ascii:"1.3")
		 *    (meta.sequence [
		 *    		(meta.tag u8ascii:"name" (meta.reference #meta.name))
		 *    		(meta.tag u8ascii:"version" (meta.reference #meta.version))
		 *    ]))
		 */
	
		int ldId = library.register(new DictionaryName("library.definition"), new MetaIdentity());
		MetaDefinition dlibDef =
			new MetaSequence( 
				new MetaExpression[] {
					new MetaTag( "name", new MetaReference(library.getTypeId("meta.name"))),
					new MetaTag( "version", new MetaReference(library.getTypeId("meta.version")))
				}
			);
    	library.register( new DictionaryDefinition( ldId,"library.definition", "1.3"), dlibDef, new TypeReaderAuto(LibraryDefinition.class), new MetaMarshaller(), LibraryDefinition.class );

		int lrId = library.register(new DictionaryName("library.relation"), new MetaIdentity());
		MetaDefinition dlibRel =
			new MetaSequence( 
				new MetaExpression[] {
					new MetaTag( "id", new MetaReference(library.getTypeId("meta.id"))),
					new MetaTag( "version", new MetaReference(library.getTypeId("meta.version"))),
					new MetaTag( "tag", new MetaReference(library.getTypeId("u8utf8")))
				}
			);
    	library.register( new DictionaryDefinition( lrId,"library.relation", "1.3"), dlibRel, new TypeReaderAuto(LibraryRelation.class), new MetaMarshaller(), LibraryRelation.class );
		
		/*
		 * (library.entry
		 * 	  (library.definition u8ascii:"library.entry" u8ascii:"1.3")
		 *    (meta.sequence [
		 *    		(meta.tag u8ascii:"location" (meta.reference #library.location))
		 *    		(meta.tag u8ascii:"definition" (meta.reference #meta.definition))
		 *    ]))
		 */
		int msId = library.register(new DictionaryName("library.entry"), new MetaIdentity());
		MetaDefinition dSourceStructure =
			new MetaSequence( 
				new MetaExpression[] {
					new MetaTag( "location", new MetaReference(library.getTypeId("dictionary.location"))),
					new MetaTag( "definition", new MetaReference(library.getTypeId("meta.definition")))
				}
			);
    	library.register( new DictionaryDefinition( msId,"library.entry", "1.3"), dSourceStructure, new TypeReaderAuto(LibraryEntry.class), new MetaMarshaller(), LibraryEntry.class );
		
		
		/*
		 * (library.entry
		 * 		(library.definition u8ascii:"library.list")
		 * 		(meta.sequence [
		 * 			(meta.array
		 * 				(meta.reference #uint16)
		 * 				(meta.reference #library.list.item))))
		 */
		int dsId = library.register(new DictionaryName("library.list"), new MetaIdentity() );	
		MetaDefinition dSource = new MetaSequence( new MetaExpression[] {
				new MetaArray(
					new MetaReference(library.getTypeId(UInt16.TYPENAME)),
					new MetaReference(library.getTypeId("library.entry"))
				)});
		library.register( new DictionaryDefinition(dsId,"library.list", "1.3"), dSource, new TypeReaderAuto(LibraryList.class), new MetaMarshaller(), LibraryList.class );
		
    }



}
