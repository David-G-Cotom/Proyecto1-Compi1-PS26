package com.example.proyecto1_compi1_ps26.domain.ast

class Program(line: Int, column: Int, val statements: ArrayList<ASTNode>) : ASTNode(line, column) {
}