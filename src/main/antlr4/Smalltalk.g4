/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
grammar Smalltalk;

script : sequence EOF;
sequence : temps? ws statements?;
ws : (SEPARATOR | COMMENT)*;
temps : PIPE (ws IDENTIFIER)+ ws PIPE;
statements : answer # StatementAnswer
           | expressions ws PERIOD ws answer # StatementExpressionsAnswer
           | expressions PERIOD? # StatementExpressions
           ;
answer : CARROT ws expression ws PERIOD?;
expression : assignment | cascade | keywordSend | binarySend | primitive;
expressions : expression expressionList*;
expressionList : PERIOD ws expression;
cascade : ws (keywordSend | binarySend) (ws SEMI_COLON ws message)+;
message : binaryMessage | unaryMessage | keywordMessage;
assignment : variable ws ASSIGNMENT ws expression;
variable : IDENTIFIER;
binarySend : unarySend binaryTail?;
unarySend : operand ws unaryTail?;
keywordSend : binarySend keywordMessage;
keywordMessage : ws (keywordPair ws)+;
keywordPair : KEYWORD ws binarySend ws;
operand : literal | reference | subexpression;
subexpression : OPEN_PAREN ws expression ws CLOSE_PAREN;
literal : runtimeLiteral | parsetimeLiteral;
runtimeLiteral : dynamicDictionary | dynamicArray | block;
block : BLOCK_START blockParamList? ws sequence? BLOCK_END;
blockParamList : (ws BLOCK_PARAM)+;
dynamicDictionary : DYNDICT_START ws expressions? ws DYNARR_END;
dynamicArray : DYNARR_START ws expressions? ws DYNARR_END;
parsetimeLiteral : pseudoVariable | number | charConstant | literalArray | string | symbol;
number : numberExp | hex | stFloat | stInteger;
numberExp : (stFloat | stInteger) EXP stInteger;
charConstant : CHARACTER_CONSTANT;
hex : MINUS? HEX HEXDIGIT+;
stInteger : MINUS? DIGIT+;
stFloat : MINUS? DIGIT+ PERIOD DIGIT+;
pseudoVariable : RESERVED_WORD;
string : STRING;
symbol : HASH bareSymbol;
primitive : LT ws KEYWORD ws DIGIT+ ws GT;
bareSymbol : (IDENTIFIER | BINARY_SELECTOR) | KEYWORD+ | string;
literalArray : LITARR_START literalArrayRest;
literalArrayRest : ws ((parsetimeLiteral | bareLiteralArray | bareSymbol) ws)* CLOSE_PAREN;
bareLiteralArray : OPEN_PAREN literalArrayRest;
unaryTail : unaryMessage ws unaryTail? ws;
unaryMessage : ws unarySelector;
unarySelector : IDENTIFIER;
keywords : KEYWORD+;
reference : variable;
binaryTail : binaryMessage binaryTail?;
binaryMessage : ws BINARY_SELECTOR ws (unarySend | operand);

SEPARATOR : [ \t\r\n];
STRING : '\'' (.)*? '\'';
COMMENT : '"' (.)*? '"';
BLOCK_START : '[';
BLOCK_END : ']';
CLOSE_PAREN : ')';
OPEN_PAREN : '(';
LT : '<';
GT : '>';
PIPE : '|';
MINUS : '-';
RESERVED_WORD : 'nil' | 'true' | 'false' | 'self' | 'super';
IDENTIFIER : [a-zA-Z]+[a-zA-Z0-9_]*;
PERIOD : '.';
CARROT : '^';
COLON : ':';
SEMI_COLON : ';';
ASSIGNMENT : ':=';
HASH : '#';
DOLLAR : '$';
EXP : 'e';
HEX : '16r';
LITARR_START : '#(';
DYNDICT_START : '#{';
DYNARR_END : '}';
DYNARR_START : '{';
DIGIT : [0-9];
HEXDIGIT : [0-9a-fA-F];
BINARY_SELECTOR : ('\\' | '+' | '*' | '/' | '=' | GT | LT | ',' | '@' | '%' | '~' | PIPE | '&' | MINUS | '?')+;
KEYWORD : IDENTIFIER COLON;
BLOCK_PARAM : COLON IDENTIFIER;
CHARACTER_CONSTANT : DOLLAR (HEXDIGIT | DOLLAR);
