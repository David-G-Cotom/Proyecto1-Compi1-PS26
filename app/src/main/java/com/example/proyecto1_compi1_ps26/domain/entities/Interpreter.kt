package com.example.proyecto1_compi1_ps26.domain.entities

import androidx.core.graphics.ColorUtils
import com.example.proyecto1_compi1_ps26.domain.ast.ASTNode
import com.example.proyecto1_compi1_ps26.domain.ast.Program
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.BinaryExpression
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Call
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Hex
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Hsl
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Identifier
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Literal
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.LiteralColor
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.PokemonFunction
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Rgb
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.UnaryExpression
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Wildcard
import com.example.proyecto1_compi1_ps26.domain.ast.statements.DoWhile
import com.example.proyecto1_compi1_ps26.domain.ast.statements.DropQuestion
import com.example.proyecto1_compi1_ps26.domain.ast.statements.For
import com.example.proyecto1_compi1_ps26.domain.ast.statements.ForRange
import com.example.proyecto1_compi1_ps26.domain.ast.statements.If
import com.example.proyecto1_compi1_ps26.domain.ast.statements.MultipleQuestion
import com.example.proyecto1_compi1_ps26.domain.ast.statements.OpenQuestion
import com.example.proyecto1_compi1_ps26.domain.ast.statements.Section
import com.example.proyecto1_compi1_ps26.domain.ast.statements.SelectQuestion
import com.example.proyecto1_compi1_ps26.domain.ast.statements.Table
import com.example.proyecto1_compi1_ps26.domain.ast.statements.Text
import com.example.proyecto1_compi1_ps26.domain.ast.statements.VarDecl
import com.example.proyecto1_compi1_ps26.domain.ast.statements.While
import com.example.proyecto1_compi1_ps26.domain.entities.enums.ColorType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.OperatorType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.ValueType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.VariableType
import java.net.URL
import kotlin.math.pow

class Interpreter {

    private val globalEnvironment = Environment()

    fun run(ast: Program) {
        this.executeBlock(ast.statements, this.globalEnvironment)
    }

    private fun executeBlock(statements: ArrayList<ASTNode>, env: Environment) {
        for (statement in statements) {
            this.execute(statement, env)
        }
    }

    private fun execute(statement: ASTNode, env: Environment) {
        when (statement) {
            is VarDecl -> this.executeVarDecl(statement, env)
            is Call -> this.executeCall(statement, env)
            is If -> this.executeIf(statement, env)
            is While -> this.executeWhile(statement, env)
            is DoWhile -> this.executeDoWhile(statement, env)
            is For -> this.executeFor(statement, env)
            is ForRange -> this.executeForRange(statement, env)
            is Section -> this.executeSection(statement, env)
            is Table -> this.executeTable(statement, env)
            is Text -> this.executeTextElement(statement, env)
            is OpenQuestion -> this.executeOpenQuestion(statement, env)
            is DropQuestion -> this.executeDropQuestion(statement, env)
            is SelectQuestion -> this.executeSelectQuestion(statement, env)
            is MultipleQuestion -> this.executeMultipleQuestion(statement, env)
            is Program -> executeBlock(statement.statements, env)
            else -> throw Exception(
                "Instruccion no ejecutable: ${statement::class.simpleName} en linea ${statement.line}"
            )
        }
    }

    private fun executeVarDecl(node: VarDecl, env: Environment) {
        if (node.type != null) {
            val value: Any = if (node.value != null) { // tipo id valor
                val v = evaluate(node.value, env)
                checkTypeCompatibility(node.type, v, node.line)
                v
            } else {    // tipo id valorDefault
                when (node.type) {
                    VariableType.NUMBER -> 0.0
                    VariableType.STRING -> ""
                    VariableType.SPECIAL -> throw Exception(
                        "Variable 'special' en la linea ${node.line} deben inicializarse con una pregunta."
                    )
                }
            }
            env.define(node.id, node.type, value, node.line, node.column)
        } else {    // id valor
            val value = evaluate(node.value!!, env)
            env.assign(node.id, value)
        }
    }

    fun evaluate(node: ASTNode, env: Environment): Any = when (node) {
        is Literal -> this.extracLiteral(node)
        is Identifier -> env.getSymbol(node.name)
        is Wildcard -> throw Exception(
            "El comodín '?' sólo puede usarse dentro de la definición de una variable special."
        )

        is BinaryExpression -> evalBinary(node, env)
        is UnaryExpression -> evalUnary(node, env)
        is Hex -> node.value
        is Rgb -> this.rgbToHex(
            toInt(evaluate(node.r, env), node.line),
            toInt(evaluate(node.g, env), node.line),
            toInt(evaluate(node.b, env), node.line)
        )

        is Hsl -> this.hslToHex(
            toNumber(evaluate(node.h, env), node.line),
            toNumber(evaluate(node.s, env), node.line),
            toNumber(evaluate(node.l, env), node.line)
        )

        is LiteralColor -> namedColor(node.value)
        is PokemonFunction -> evalPokemon(node, env)
        else -> {}
    }

    private fun extracLiteral(literal: Literal): Any {
        return when (literal.type) {
            ValueType.DECIMAL -> return literal.value as Double
            ValueType.WHOLE -> return literal.value as Int
            ValueType.STRING -> return literal.value as String
            else -> {}
        }
    }

    private fun checkTypeCompatibility(expected: VariableType, actual: Any, line: Int) {
        val ok = when (expected) {
            VariableType.NUMBER -> actual is Int || actual is Double
            VariableType.STRING -> actual is String
            VariableType.SPECIAL -> actual is SpecialValue
        }
        if (!ok) throw Exception(
            "Tipo incompatible: Se esperaba ${expected.name.lowercase()} " +
                    "pero se obtuvo ${actual::class.simpleName} en la linea ${line}"
        )
    }

    private fun executeCall(node: Call, env: Environment) {
        val special = env.getSymbol(node.varName)
        if (special.type != VariableType.SPECIAL) {
            throw Exception(
                "Variable ${node.varName} no es de tipo special. Error en linea ${node.line}"
            )
        }
        val specialValue = special.value as SpecialValue
        if (node.arguments.size != specialValue.wildcardCount) {
            throw Exception(
                "Error en linea ${node.line}: ${node.varName}.draw() requiere ${specialValue.wildcardCount} argumento(s) " +
                        "pero recibió ${node.arguments.size}."
            )
        }
    }

    private fun executeIf(node: If, env: Environment) {
        if (this.isTrue(evaluate(node.condition, env), node.line)) {
            executeBlock(node.thenStatement, env.createChild())
            return
        }
        if (node.elseIfStatement != null) {
            for (statement in node.elseIfStatement) {
                if (this.isTrue(evaluate(statement.condition, env), node.line)) {
                    if (statement.body != null) {
                        executeBlock(statement.body, env.createChild())
                    }
                    return
                }
            }
        }
        node.elseStatement?.let { executeBlock(it, env.createChild()) }
    }

    private fun isTrue(value: Any, line: Int): Boolean = when (value) {
        is Int -> value >= 1
        is Double -> value >= 1.0
        else -> throw Exception(
            "Error en linea ${line}: Se esperaba un valor numérico en la condición, se obtuvo ${value::class.simpleName}."
        )
    }

    private fun executeWhile(node: While, env: Environment) {
        while (this.isTrue(evaluate(node.condition, env), node.line)) {
            executeBlock(node.body, env.createChild())
        }
    }

    private fun executeDoWhile(node: DoWhile, env: Environment) {
        do {
            executeBlock(node.body, env.createChild())
        } while (this.isTrue(evaluate(node.condition, env), node.line))
    }

    private fun executeFor(node: For, env: Environment) {
        val loopEnv = env.createChild()
        execute(node.init, loopEnv)
        val controlVar: String = when (val init = node.init) {
            is VarDecl -> {
                if (init.type != VariableType.NUMBER) {
                    throw Exception(
                        "Error en la linea ${node.line}. La variable '${init.id}' del for debe ser de tipo number."
                    )
                }
                if (loopEnv.getSymbol(init.id).type != VariableType.NUMBER) {
                    throw Exception(
                        "Error en la linea ${node.line}. La variable '${init.id}' del for debe ser de tipo number."
                    )
                }
                init.id
            }

            else -> throw Exception(
                "Error en la linea ${node.line}. La inicialización del for debe ser una declaración o asignación."
            )
        }
        while (this.isTrue(evaluate(node.condition, loopEnv), node.line)) {
            executeBlock(node.body, loopEnv.createChild())
            execute(node.update, loopEnv)
        }
    }

    private fun executeForRange(node: ForRange, env: Environment) {
        val loopEnv = env.createChild()
        val from = toNumber(evaluate(node.start, env), node.line).toLong()
        val to = toNumber(evaluate(node.end, env), node.line).toLong()

        if (!loopEnv.isDeclared(node.id)) {
            loopEnv.define(
                node.id,
                VariableType.NUMBER,
                from.toDouble(),
                node.line,
                node.column
            )
        } else {
            if (loopEnv.getSymbol(node.id).type != VariableType.NUMBER) {
                throw Exception(
                    "Error en la linea ${node.line}. La variable '${node.id}' del for debe ser de tipo number."
                )
            }
        }

        for (i in from..to) {
            loopEnv.assign(node.id, i.toDouble())
            executeBlock(node.body, loopEnv.createChild())
        }
    }

    private fun toNumber(value: Any, line: Int): Double {
        return when (value) {
            is Number -> value.toDouble()
            else -> throw Exception(
                "Error en linea ${line}: Se esperaba number, se obtuvo ${value::class.simpleName}."
            )
        }
    }

    private fun executeSection(node: Section, env: Environment) {
        val sectionEnv = env.createChild()
        node.elements?.forEach { execute(it, sectionEnv) }
    }

    private fun executeTable(node: Table, env: Environment) {
        val tableEnv = env.createChild()
        node.elements.forEach { execute(it, tableEnv) }
    }

    private fun executeTextElement(node: Text, env: Environment) {
        val content = evaluate(node.content, env).toString()
        //formOutput.add(mapOf("type" to "TEXT", "content" to content))
        println("[FORM] Texto agregado: \"$content\"")
    }

    private fun executeOpenQuestion(node: OpenQuestion, env: Environment) {
        val label = evaluate(node.label, env).toString()
        val width = node.width?.let { toNumber(evaluate(it, env), node.line) }
        val height = node.height?.let { toNumber(evaluate(it, env), node.line) }
        val styles = evalStyles(node.styles, env)

        /*formOutput.add(mapOf(
            "type"   to "OPEN_QUESTION",
            "label"  to label,
            "width"  to width,
            "height" to height,
            "styles" to styles
        ))*/
        println("[FORM] OPEN_QUESTION: \"$label\"")
    }

    private fun evalStyles(node: Styles?, env: Environment): Map<String, Any> {
        if (node == null) return emptyMap()
        return node.entries.mapValues { (_, sv) ->
            when (sv) {
                is ColorStyleValue -> evaluate(sv.color, env)
                is FontStyleValue -> sv.font
                is TextSizeStyleValue -> evaluate(sv.size, env)
                is BorderStyleValue -> mapOf(
                    "width" to evaluate(sv.width, env),
                    "type" to sv.type,
                    "color" to evaluate(sv.color, env)
                )
            }
        }
    }

    private fun executeDropQuestion(node: DropQuestion, env: Environment) {
        val label = evaluate(node.label, env).toString()
        val width = node.width?.let { toNumber(evaluate(it, env), node.line) }
        val height = node.height?.let { toNumber(evaluate(it, env), node.line) }
        val options = resolveOptions(node.options, env)
        val correct = node.correct?.let { toNumber(evaluate(it, env), node.line).toInt() }

        correct?.let {
            if (it < 1 || it > options.size) {
                throw Exception(
                    "Error en la linea: ${node.line}. DROP_QUESTION: 'correct' ($it) fuera del rango de opciones (1..${options.size})."
                )
            }
        }
        val styles = evalStyles(node.styles, env)
        //formOutput.add(mapOf("type" to "DROP_QUESTION", "label" to label, "options" to options, "correct" to correct, "styles" to styles))
        println("[FORM] DROP_QUESTION: \"$label\" (${options.size} opciones)")
    }

    private fun resolveOptions(src: OptionsSource, env: Environment): ArrayList<String> =
        when (src) {
            is LiteralOptions -> src.options.map { evaluate(it, env).toString() }
            is PokemonOptions -> {
                val csv = evaluate(src.pokemonQuery, env) as String
                csv.split(",").map { it.trim() }
            }
        } as ArrayList<String>

    private fun executeSelectQuestion(node: SelectQuestion, env: Environment) {
        val options = resolveOptions(node.options, env)
        val label = node.label?.let { evaluate(it, env).toString() }
        val correct = node.correct?.let { toNumber(evaluate(it, env), node.line).toInt() }

        if (options.size > 5) {
            println(
                "[ADVERTENCIA] SELECT_QUESTION tiene más de 5 opciones (${options.size}). " +
                        "El usuario deberá confirmar antes de agregar al formulario."
            )
        }
        correct?.let {
            if (it < 1 || it > options.size) {
                throw Exception(
                    "Error en la linea ${node.line}. SELECT_QUESTION: 'correct' ($it) fuera del rango (1..${options.size})."
                )
            }
        }
        val styles = evalStyles(node.styles, env)
        //formOutput.add(mapOf("type" to "SELECT_QUESTION", "label" to label, "options" to options, "correct" to correct, "styles" to styles))
    }

    private fun executeMultipleQuestion(node: MultipleQuestion, env: Environment) {
        val options = resolveOptions(node.options, env)
        val label = node.label?.let { evaluate(it, env).toString() }
        val correct = node.correct?.map { toNumber(evaluate(it, env), node.line).toInt() }

        if (options.size > 5) {
            println("[ADVERTENCIA] MULTIPLE_QUESTION tiene más de 5 opciones (${options.size}).")
        }
        correct?.forEach {
            if (it < 1 || it > options.size) {
                throw Exception(
                    "Error en la linea ${node.line}. MULTIPLE_QUESTION: índice correcto ($it) fuera del rango (1..${options.size})."
                )
            }
        }
        val styles = evalStyles(node.styles, env)
        //formOutput.add(mapOf("type" to "MULTIPLE_QUESTION", "label" to label, "options" to options, "correct" to correct, "styles" to styles))
    }

    private fun evalBinary(node: BinaryExpression, env: Environment): Any {
        if (node.operator == OperatorType.OR) {
            val l = evaluate(node.left, env)
            if (this.isTrue(l, node.line)) return 1.0
            val r = evaluate(node.right, env)
            return if (this.isTrue(r, node.line)) 1.0 else 0.0
        }
        if (node.operator == OperatorType.AND) {
            val l = evaluate(node.left, env)
            if (!this.isTrue(l, node.line)) return 0.0
            val r = evaluate(node.right, env)
            return if (this.isTrue(r, node.line)) 1.0 else 0.0
        }

        val left = evaluate(node.left, env)
        val right = evaluate(node.right, env)

        return when (node.operator) {
            OperatorType.SUMA -> when {
                left is Number && right is Number -> left.toDouble() + right.toDouble()
                left is String || right is String -> "$left$right"
                else -> throw Exception(
                    "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                )
            }

            OperatorType.RESTA -> toNumber(left, node.line) - toNumber(right, node.line)
            OperatorType.MULTIPLICACION -> toNumber(left, node.line) * toNumber(right, node.line)
            OperatorType.DIVISION -> {
                val d = toNumber(right, node.line)
                if (d == 0.0) throw Exception("Error en la linea ${node.line}: División por cero.")
                toNumber(left, node.line) / d
            }

            OperatorType.POTENCIA -> toNumber(left, node.line).pow(
                toNumber(right, node.line)
            )

            OperatorType.MODULO -> toNumber(left, node.line) % toNumber(right, node.line)

            OperatorType.MAYOR -> bool(toNumber(left, node.line) > toNumber(right, node.line))
            OperatorType.MAYOR_IGUAL -> bool(
                toNumber(left, node.line) >= toNumber(
                    right,
                    node.line
                )
            )

            OperatorType.MENOR -> bool(toNumber(left, node.line) < toNumber(right, node.line))
            OperatorType.MENOR_IGUAL -> bool(
                toNumber(left, node.line) <= toNumber(
                    right,
                    node.line
                )
            )

            OperatorType.IGUALDAD -> bool(left == right)
            OperatorType.DIFERENTE -> bool(left != right)
            else -> {}
        }
    }

    private fun bool(b: Boolean) = if (b) 1.0 else 0.0

    private fun evalUnary(node: UnaryExpression, env: Environment): Any {
        val v = evaluate(node.right, env)
        return when (node.operator) {
            OperatorType.NEGATIVO -> -toNumber(v, node.line)
            OperatorType.NOT -> bool(!this.isTrue(v, node.line))
            else -> {}
        }
    }

    private fun rgbToHex(r: Int, g: Int, b: Int): String {
        val redHex = Integer.toHexString(r)
        val greenHex = Integer.toHexString(g)
        val blueHex = Integer.toHexString(b)

        return redHex + greenHex + blueHex
    }

    private fun hslToHex(h: Double, s: Double, l: Double): String {
        val hsl = floatArrayOf(h.toFloat(), s.toFloat(), l.toFloat())
        val colorInt = ColorUtils.HSLToColor(hsl)
        val hexColor = String.format("#%06X", 0xFFFFFF and colorInt)
        return hexColor.removePrefix("#")
    }

    private fun namedColor(name: ColorType): String = when (name) {
        ColorType.RED -> "FF0000"
        ColorType.BLUE -> "0000FF"
        ColorType.GREEN -> "008000"
        ColorType.PURPLE -> "800080"
        ColorType.SKY -> "87CEEB"
        ColorType.YELLOW -> "FFFF00"
        ColorType.BLACK -> "000000"
        ColorType.WHITE -> "FFFFFF"
    }

    private fun toInt(value: Any, line: Int): Int =
        toNumber(value, line).toInt()

    private fun evalPokemon(node: PokemonFunction, env: Environment): Any {
        val from = toNumber(evaluate(node.from, env), node.line).toInt()
        val to = toNumber(evaluate(node.to, env), node.line).toInt()

        val names = mutableListOf<String>()
        for (i in from..to) {
            try {
                val json = URL("https://pokeapi.co/api/v2/pokemon/$i").readText()
                val match = Regex(""""name"\s*:\s*"([^"]+)"""").find(json)
                names.add(match?.groupValues?.getOrNull(1)?.replaceFirstChar { it.uppercase() }
                    ?: "Pokémon #$i")
            } catch (e: Exception) {
                println("[ADVERTENCIA] No se pudo obtener el Pokémon #$i: ${e.message}")
                names.add("Pokémon #$i")
            }
        }
        return names.joinToString(",")
    }

}