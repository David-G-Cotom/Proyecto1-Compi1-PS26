// IMPORTS
package com.example.proyecto1_compi1_ps26.domain.analyzers.form_creation;

import java_cup.runtime.*;
import com.example.proyecto1_compi1_ps26.domain.analyzers.form_creation.symForm;

import java.util.ArrayList;

import com.example.proyecto1_compi1_ps26.domain.entities.ErrorReport;

%% // ---------------------------------------- SECTION SEPARATOR ----------------------------------------

// USER CODE
%{
    // Utils
    private StringBuilder string;

    // Error Handling
    private ArrayList<String> symbols;
    private ArrayList<ErrorReport> lexicalErrors;

    private void error(String token) {
        this.lexicalErrors.add(new ErrorReport(token, yyline, yycolumn, "Lexico", "Cadena no existente en el lenguaje"));
    }

    public ArrayList<ErrorReport> getLexicalErrors(){
        return this.lexicalErrors;
    }

    public ArrayList<String> getSymbols(){
        return this.symbols;
    }

    //Parser Code
    private Symbol symbol(int type) {
        this.symbols.add(yytext());
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        this.symbols.add(value.toString());
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

// OPTIONS AND DECLARATIONS
%public
%class LexerForm
%line
%column
%char
%unicode
%cup
%init{
    this.string = new StringBuilder();
    this.symbols = new ArrayList<>();
    this.lexicalErrors = new ArrayList<>();
    yyline = 1;
    yycolumn = 1;
%init}

// REGULAR EXPRESSIONS
LineTerminator = \r|\n|\r\n
WhiteSpace = [ \t\f]
InputCharacter = [^\r\n]
Dot = \.
WholeNumber = 0|[1-9][0-9]*
DecimalNumber = {WholeNumber}{Dot}[0-9]+
Letter = [a-zA-Z]
ID = _?{Letter}({Letter}|_|{WholeNumber})*

// STATES
%state TEXT, SIMPLE_COMMENT, MULTIPLE_COMMENT

%% // ---------------------------------------- SECTION SEPARATOR ----------------------------------------

// LEXICAL RULES
// Default State
<YYINITIAL> {
    "+" { return symbol(symForm.SUMA); }
    "-" { return symbol(symForm.RESTA); }
    "*" { return symbol(symForm.MULTIPLICACION); }
    "/" { return symbol(symForm.DIVISION); }
    "^" { return symbol(symForm.POTENCIA); }
    "%" { return symbol(symForm.MODULO); }
    "(" { return symbol(symForm.PARENTESIS_ABIERTO); }
    ")" { return symbol(symForm.PARENTESIS_CERRADO); }

    "==" { return symbol(symForm.IGUALDAD); }
    "!!" { return symbol(symForm.DIFERENTE); }
    ">"  { return symbol(symForm.MAYOR); }
    "<"  { return symbol(symForm.MENOR); }
    ">=" { return symbol(symForm.MAYOR_IGUAL); }
    "<=" { return symbol(symForm.MENOR_IGUAL); }

    "&&" { return symbol(symForm.AND); }
    "||" { return symbol(symForm.OR); }
    "~"  { return symbol(symForm.NOT); }

    "=" { return symbol(symForm.IGUAL); }
    "," { return symbol(symForm.COMA); }
    "[" { return symbol(symForm.CORCHETE_ABIERTO); }
    "]" { return symbol(symForm.CORCHETE_CERRADO); }
    ":" { return symbol(symForm.DOS_PUNTOS); }
    "?" { return symbol(symForm.COMODIN); }
    "{" { return symbol(symForm.LLAVE_ABIERTO); }
    "}" { return symbol(symForm.LLAVE_CERRADO); }
    ";" { return symbol(symForm.PUNTO_COMA); }

    "number" { return symbol(symForm.NUMBER); }
    "string" { return symbol(symForm.STRING); }
    "special" { return symbol(symForm.SPECIAL); }

    "OPEN_QUESTION" { return symbol(symForm.OPEN_QUESTION); }
    "SECTION" { return symbol(symForm.SECTION); }
    "TABLE" { return symbol(symForm.TABLE); }
    "TEXT" { return symbol(symForm.TEXT); }
    "DROP_QUESTION" { return symbol(symForm.DROP_QUESTION); }
    "SELECT_QUESTION" { return symbol(symForm.SELECT_QUESTION); }
    "MULTIPLE_QUESTION" { return symbol(symForm.MULTIPLE_QUESTION); }

    "RED" { return symbol(symForm.RED); }
    "BLUE" { return symbol(symForm.BLUE); }
    "GREEN" { return symbol(symForm.GREEN); }
    "PURPLE" { return symbol(symForm.PURPLE); }
    "SKY" { return symbol(symForm.SKY); }
    "YELLOW" { return symbol(symForm.YELLOW); }
    "BLACK" { return symbol(symForm.BLACK); }
    "WHITE" { return symbol(symForm.WHITE); }

    "VERTICAL" { return symbol(symForm.VERTICAL); }
    "HORIZONTAL" { return symbol(symForm.HORIZONTAL); }

    "MONO" { return symbol(symForm.MONO); }
    "SANS_SERIF" { return symbol(symForm.SANS_SERIF); }
    "CURSIVE" { return symbol(symForm.CURSIVE); }

    "LINE" { return symbol(symForm.LINE); }
    "DOTTED" { return symbol(symForm.DOTTED); }
    "DOUBLE" { return symbol(symForm.DOUBLE); }

    "width" { return symbol(symForm.WIDTH); }
    "height" { return symbol(symForm.HEIGHT); }
    "label" { return symbol(symForm.LABEL); }
    "draw" { return symbol(symForm.DRAW); }
    "pointX" { return symbol(symForm.POINT_X); }
    "pointY" { return symbol(symForm.POINT_Y); }
    "orientation" { return symbol(symForm.ORIENTATION); }
    "elements" { return symbol(symForm.ELEMENTS); }
    "styles" { return symbol(symForm.STYLES); }
    "content" { return symbol(symForm.CONTENT); }
    "options" { return symbol(symForm.OPTIONS); }
    "correct" { return symbol(symForm.CORRECT); }
    "who_is_that_pokemon" { return symbol(symForm.WHO_IS_THAT_POKEMON); }

    "NUMBER" { return symbol(symForm.NUMBER_POKEMON); }

    \""color"\" { return symbol(symForm.COLOR); }
    \""background color"\" { return symbol(symForm.BACKGROUND_COLOR); }
    \""font family"\" { return symbol(symForm.FONT_FAMILY); }
    \""text size"\" { return symbol(symForm.TEXT_SIZE); }
    \""border"\" { return symbol(symForm.BORDER); }

    "IF" { return symbol(symForm.IF); }
    "ELSE" { return symbol(symForm.ELSE); }
    "WHILE" { return symbol(symForm.WHILE); }
    "DO" { return symbol(symForm.DO); }
    "FOR" { return symbol(symForm.FOR); }
    "in" { return symbol(symForm.IN); }

    "$" { this.string.setLength(0); yybegin(SIMPLE_COMMENT); }
    "/*" { this.string.setLength(0); yybegin(MULTIPLE_COMMENT); }

    \" { this.string.setLength(0); yybegin(TEXT); }

    "#"[a-fA-F0-9]{6} {
        this.string.setLength(0);
        this.string.append(yytext());
        this.string.deleteCharAt(0);
        return symbol(symForm.HEXADECIMAL, this.string.toString().trim());
    }
    "#"[a-fA-F0-9]{3} {
        this.string.setLength(0);
        this.string.append(yytext());
        this.string.deleteCharAt(0);
        return symbol(symForm.HEXADECIMAL, this.string.toString().trim());
    }

    {WholeNumber}   { return symbol(symForm.NUMERO_ENTERO, Integer.parseInt(yytext())); }
    {DecimalNumber} { return symbol(symForm.NUMERO_DECIMAL, Double.parseDouble(yytext())); }
    {ID}            { return symbol(symForm.ID, yytext()); }
    {Dot}           { return symbol(symForm.PUNTO); }
}

// Comments states
<SIMPLE_COMMENT> {
    {InputCharacter}+ { this.string.append(yytext()); }
    {LineTerminator} { yybegin(YYINITIAL); }
    <<EOF>> { yybegin(YYINITIAL); }
}
<MULTIPLE_COMMENT> {
    "*/" {
        System.out.println("COMENTARIO MULTILINEA: " + this.string.toString().trim());
        yybegin(YYINITIAL);
    }
    {InputCharacter} { this.string.append(yytext()); }
    {LineTerminator} { /* Ignore */ }
}

// Text state
<TEXT> {
    \" {
        System.out.println("TEXTO: " + this.string.toString().trim());
        yybegin(YYINITIAL);
        return symbol(symForm.TEXTO, this.string.toString().trim());
    }
    {InputCharacter} { this.string.append(yytext()); }
    {LineTerminator} { this.string.append("\n"); }
}

/* Ignored whitespace */
{WhiteSpace} | {LineTerminator} { /* Ignore */ }

/* Error handling */
. { error(yytext()); }
<<EOF>> { return symbol(symForm.EOF); }