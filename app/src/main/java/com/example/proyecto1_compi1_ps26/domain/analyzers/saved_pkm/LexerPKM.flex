// IMPORTS
package com.example.proyecto1_compi1_ps26.domain.analyzers.saved_pkm;

import java_cup.runtime.*;
import com.example.proyecto1_compi1_ps26.domain.analyzers.saved_pkm.symPKM;

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
%class LexerPKM
%line
%column
%char
%unicode
%ignorecase
%cup
%init{
    this.string = new StringBuilder();
    this.symbols = new ArrayList<>();
    this.lexicalErrors = new ArrayList<>();
    yyline = 1;
    yycolumn = 1;
%init}

// REGULAR EXPRESSIONS
LineTerminator = \r|\n|\n\r|\r\n
WhiteSpace = [ \t\f]
InputCharacter = [^\r\n]
Dot = \.
WholeNumber = 0|[1-9][0-9]*
DecimalNumber = {WholeNumber}{Dot}[0-9]+
Letter = [a-zA-Z]
ID = _?{Letter}({Letter}|_|{WholeNumber})*

// STATES
%state TEXT

%% // ---------------------------------------- SECTION SEPARATOR ----------------------------------------

// LEXICAL RULES
// Default State
<YYINITIAL> {
    "###"                    { return symbol(symPKM.SEPARADOR); }
    ":"                  { return symbol(symPKM.DOS_PUNTOS); }

    "Author"                  { return symbol(symPKM.AUTHOR); }
    "Fecha"                     { return symbol(symPKM.FECHA); }
    "Hora"                      { return symbol(symPKM.HORA); }
    "Description"                { return symbol(symPKM.DESCRIPTION); }
    "Total de Secciones"                { return symbol(symPKM.TOTAL_SECCIONES); }
    "Total de Preguntas"                   { return symbol(symPKM.TOTAL_PREGUNTAS); }
    "Abiertas"                     { return symbol(symPKM.ABIERTAS); }
    "Desplegables"                 { return symbol(symPKM.DESPLEGABLES); }
    "Seleccion"                    { return symbol(symPKM.SELECCION); }
    "Multiples"                  { return symbol(symPKM.MULTIPLES); }

    {WholeNumber}"/"{WholeNumber}"/"{WholeNumber}                 { return symbol(symPKM.INPUT_FECHA); }
    {WholeNumber}":"{WholeNumber}           { return symbol(symPKM.INPUT_HORA); }

    "style"              { return symbol(symPKM.STYLE_ETIQUETA); }
    "color"   { return symbol(symPKM.COLOR_ETIQUETA); }
    "background color"   { return symbol(symPKM.BACKGROUND_COLOR_ETIQUETA); }
    "font family"   { return symbol(symPKM.FONT_FAMILY_ETIQUETA); }
    "text size"   { return symbol(symPKM.TEXT_SIZE_ETIQUETA); }
    "border"   { return symbol(symPKM.BORDER_ETIQUETA); }
    "section"   { return symbol(symPKM.SECTION_ETIQUETA); }
    "content"   { return symbol(symPKM.CONTENT_ETIQUETA); }
    "table"   { return symbol(symPKM.TABLE_ETIQUETA); }
    "line"   { return symbol(symPKM.LINE_ETIQUETA); }
    "element"   { return symbol(symPKM.ELEMENT_ETIQUETA); }
    "open"   { return symbol(symPKM.OPEN_ETIQUETA); }
    "drop"   { return symbol(symPKM.DROP_ETIQUETA); }
    "select"   { return symbol(symPKM.SELECT_ETIQUETA); }
    "multiple"   { return symbol(symPKM.MULTIPLE_ETIQUETA); }

    "RED" { return symbol(symPKM.RED); }
    "BLUE" { return symbol(symPKM.BLUE); }
    "GREEN" { return symbol(symPKM.GREEN); }
    "PURPLE" { return symbol(symPKM.PURPLE); }
    "SKY" { return symbol(symPKM.SKY); }
    "YELLOW" { return symbol(symPKM.YELLOW); }
    "BLACK" { return symbol(symPKM.BLACK); }
    "WHITE" { return symbol(symPKM.WHITE); }

    "MONO" { return symbol(symPKM.MONO); }
    "SANS_SERIF" { return symbol(symPKM.SANS_SERIF); }
    "CURSIVE" { return symbol(symPKM.CURSIVE); }

    "LINE" { return symbol(symPKM.LINE); }
    "DOTTED" { return symbol(symPKM.DOTTED); }
    "DOUBLE" { return symbol(symPKM.DOUBLE); }

    "VERTICAL" { return symbol(symPKM.VERTICAL); }
    "HORIZONTAL" { return symbol(symPKM.HORIZONTAL); }

    "<"  { return symbol(symPKM.MENOR); }
    ">"  { return symbol(symPKM.MAYOR); }
    "=" { return symbol(symPKM.IGUAL); }
    "," { return symbol(symPKM.COMA); }
    "/" { return symbol(symPKM.DIAGONAL); }
    "(" { return symbol(symPKM.PARENTESIS_ABIERTO); }
    ")" { return symbol(symPKM.PARENTESIS_CERRADO); }
    "{" { return symbol(symPKM.LLAVE_ABIERTO); }
    "}" { return symbol(symPKM.LLAVE_CERRADO); }

    "+" { return symbol(symPKM.SUMA); }
    "-" { return symbol(symPKM.RESTA); }
    "*" { return symbol(symPKM.MULTIPLICACION); }
    "/" { return symbol(symPKM.DIVISION); }
    "^" { return symbol(symPKM.POTENCIA); }
    "%" { return symbol(symPKM.MODULO); }

    \" { this.string.setLength(0); yybegin(TEXT); }

    "#"[a-fA-F0-9]{6} {
        this.string.setLength(0);
        this.string.append(yytext());
        this.string.deleteCharAt(0);
        return symbol(symPKM.HEXADECIMAL, this.string.toString().trim());
    }
    "#"[a-fA-F0-9]{3} {
        this.string.setLength(0);
        this.string.append(yytext());
        this.string.deleteCharAt(0);
        return symbol(symPKM.HEXADECIMAL, this.string.toString().trim());
    }

    {WholeNumber}   { return symbol(symPKM.NUMERO_ENTERO, Integer.parseInt(yytext())); }
    {DecimalNumber} { return symbol(symPKM.NUMERO_DECIMAL, Double.parseDouble(yytext())); }
    "-"{WholeNumber}   { return symbol(symPKM.NUMERO_ENTERO_NEGATIVO, Integer.parseInt(yytext())); }
    "-"{DecimalNumber} { return symbol(symPKM.NUMERO_DECIMAL_NEGATIVO, Double.parseDouble(yytext())); }
    {ID}            { return symbol(symPKM.ID, yytext()); }
}

// Text state
<TEXT> {
    \" {
        System.out.println("TEXTO:" + this.string.toString().trim());
        yybegin(YYINITIAL);
        return symbol(symPKM.TEXTO, this.string.toString().trim());
    }
    {InputCharacter} { this.string.append(yytext()); }
    {LineTerminator} { this.string.append("\n"); }
}

/* Ignored whitespace */
{WhiteSpace} | {LineTerminator} { /* Ignore */ }

/* Error handling */
. { error(yytext()); }
<<EOF>> { return symbol(symPKM.EOF); }