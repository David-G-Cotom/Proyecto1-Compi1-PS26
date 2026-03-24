package com.example.proyecto1_compi1_ps26.domain.translation.tags

abstract class PkmTag {

    abstract fun render(indent: Int = 0): String

    fun ind(n: Int) = "    ".repeat(n)

}