/*
 * Copyright (c) 2003-2019, Live Media Pty. Ltd.
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
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS IntERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
grammar Argot;

@header 
{ 
package com.argot.compiler;
}
 
 
file : (headers)? (expression)* EOF
  	;

headers: headerline (headers)?
  	;
	
headerline: importl | load | reserve | cluster | definition | relation 
  	;

cluster: 'cluster' identifier ';'
  	;

definition: 'definition' identifier Int '.' Int ':' ( expression | sequence ) ';'
  	;
  
relation: 'relation' identifier Int '.' Int QSTRING ':' ( expression ) ';'
  	;

sequence: '{' (tag)* '}'
  	;

tag: '@' identifier  '#' identifier ';'
  	;

reserve: '!' 'reserve' identifier  ';'
  	;
 
load: '!' 'load' QSTRING (',' identifier )? ';'
  	;

importl: '!' 'import' identifier ( 'as' identifier)? ';'
  	;

expression: '(' expressionIdentifier (primary)* ')'
	;
	
expressionIdentifier: identifier ('/' Int '.' Int )?
    ;

	
primary: expression
	| array
	| value
	| typeIdentifier
	| mappedIdentifier
	;
	
array: '[' ( primary)*  ']'
   	;
   
typeIdentifier: '#' identifier
   	;

	
value: identifier ':' (Int | QSTRING)
	;

mappedIdentifier: '$' identifier
	;

identifier: IDENTIFIER 
   	;


IDENTIFIER: VALID_ID_START VALID_ID_CHAR*
   	;


fragment 
VALID_ID_START: ('a' .. 'z') | ('A' .. 'Z') | '_';


fragment 
VALID_ID_CHAR: VALID_ID_START | ('0' .. '9') | '.' | '_';
   

QSTRING: '"' (ESCAPE_SEQUENCE | ~ ('\\' | '"'))* '"';

fragment 
ESCAPE_SEQUENCE: '\\' ('b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\') 
   	;

Int: [0-9]+;

WS: [ \t\r\n]+ -> channel(HIDDEN) ;

BlockComment : '/*' .*? '*/' -> channel(HIDDEN);
    

