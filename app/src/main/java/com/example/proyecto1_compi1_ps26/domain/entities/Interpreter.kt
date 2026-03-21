package com.example.proyecto1_compi1_ps26.domain.entities

import androidx.core.graphics.ColorUtils
import com.example.proyecto1_compi1_ps26.domain.ast.ASTNode
import com.example.proyecto1_compi1_ps26.domain.ast.LiteralOptions
import com.example.proyecto1_compi1_ps26.domain.ast.OptionsSource
import com.example.proyecto1_compi1_ps26.domain.ast.PokemonOptions
import com.example.proyecto1_compi1_ps26.domain.ast.Program
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.BinaryExpression
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Call
import com.example.proyecto1_compi1_ps26.domain.ast.expressions.Expression
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
import com.example.proyecto1_compi1_ps26.domain.entities.elements.DropQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.FormElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.MultipleQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.OpenQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.SectionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.SelectQuestionElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.StyleAttributes
import com.example.proyecto1_compi1_ps26.domain.entities.elements.TableElement
import com.example.proyecto1_compi1_ps26.domain.entities.elements.TextElement
import com.example.proyecto1_compi1_ps26.domain.entities.enums.ColorType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.OperatorType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.OrientationType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.ValueType
import com.example.proyecto1_compi1_ps26.domain.entities.enums.VariableType
import com.example.proyecto1_compi1_ps26.domain.entities.question_values.DropQValue
import com.example.proyecto1_compi1_ps26.domain.entities.question_values.MultipleQValue
import com.example.proyecto1_compi1_ps26.domain.entities.question_values.OpenQValue
import com.example.proyecto1_compi1_ps26.domain.entities.question_values.SelectQValue
import com.example.proyecto1_compi1_ps26.domain.entities.question_values.SpecialValue
import java.net.URL
import kotlin.math.pow

class Interpreter {

    private val globalEnvironment = Environment()

    val formOutput = mutableListOf<FormElement>()

    fun run(ast: Program) {
        this.executeBlock(ast.statements, this.globalEnvironment)
    }

    private fun emit(element: FormElement, into: MutableList<FormElement>?) {
        (into ?: this.formOutput).add(element)
    }

    private fun executeBlock(
        statements: ArrayList<ASTNode>,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        for (statement in statements) {
            this.execute(statement, env, into)
        }
    }

    private fun execute(
        statement: ASTNode,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        when (statement) {
            is VarDecl -> this.executeVarDecl(statement, env)
            is Call -> this.executeCall(statement, env, into)
            is If -> this.executeIf(statement, env, into)
            is While -> this.executeWhile(statement, env, into)
            is DoWhile -> this.executeDoWhile(statement, env, into)
            is For -> this.executeFor(statement, env, into)
            is ForRange -> this.executeForRange(statement, env, into)
            is Section -> this.executeSection(statement, env, into)
            is Table -> this.executeTable(statement, env, into)
            is Text -> this.executeTextElement(statement, env, into)
            is OpenQuestion -> this.executeOpenQuestion(statement, env, into)
            is DropQuestion -> this.executeDropQuestion(statement, env, into)
            is SelectQuestion -> this.executeSelectQuestion(statement, env, into)
            is MultipleQuestion -> this.executeMultipleQuestion(statement, env, into)
            is Program -> executeBlock(statement.statements, env, into)
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
        is Identifier -> env.getSymbol(node.name).value!!
        is OpenQuestion -> evalOpenQuestion(node, env)
        is DropQuestion -> evalDropQuestion(node, env)
        is SelectQuestion -> evalSelectQuestion(node, env)
        is MultipleQuestion -> evalMultipleQuestion(node, env)
        is Wildcard -> throw Exception(
            "El comodín '?' sólo puede usarse dentro de la definición de una variable special."
        )

        is BinaryExpression -> evalBinary(node, env)
        is UnaryExpression -> evalUnary(node, env)
        is Hex -> "#" + node.value
        is Rgb -> "#" + this.rgbToHex(
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

    private fun evalOptionalNum(expr: Expression?, env: Environment): Double? {
        if (expr == null || expr is Wildcard) return null
        return toNumber(evaluate(expr, env), expr.line)
    }

    private fun countWildcards(vararg exprs: Expression?): Int =
        exprs.count { it is Wildcard }

    private fun evalOpenQuestion(node: OpenQuestion, env: Environment): OpenQValue {
        return OpenQValue(
            evalOptionalNum(node.width, env),
            evalOptionalNum(node.height, env),
            evaluate(node.label, env).toString(),
            evalStyles(node.styles, env),
            countWildcards(node.width, node.height)
        )
    }

    private fun evalDropQuestion(node: DropQuestion, env: Environment): DropQValue {
        val options = resolveOptions(node.options, env)
        val correct = node.correct?.let { toNumber(evaluate(it, env), node.line).toInt() }
        correct?.let {
            if (it < 1 || it > options.size) throw Exception(
                "Error en la linea ${node.line}: DROP_QUESTION: 'correct' ($it) fuera del rango (1..${options.size})."
            )
        }
        return DropQValue(
            evalOptionalNum(node.width, env),
            evalOptionalNum(node.height, env),
            evaluate(node.label, env).toString(),
            options,
            correct,
            evalStyles(node.styles, env),
            countWildcards(node.width, node.height)
        )
    }

    private fun evalSelectQuestion(node: SelectQuestion, env: Environment): SelectQValue {
        val options = resolveOptions(node.options, env)
        val correct = node.correct?.let { toNumber(evaluate(it, env), node.line).toInt() }
        correct?.let {
            if (it < 1 || it > options.size) throw Exception(
                "Error en la linea ${node.line}: SELECT_QUESTION: 'correct' ($it) fuera del rango (1..${options.size})."
            )
        }
        return SelectQValue(
            evalOptionalNum(node.width, env),
            evalOptionalNum(node.height, env),
            node.label?.let { evaluate(it, env).toString() } as String,
            options,
            correct,
            evalStyles(node.styles, env),
            countWildcards(node.width, node.height)
        )
    }

    private fun evalMultipleQuestion(node: MultipleQuestion, env: Environment): MultipleQValue {
        val options = resolveOptions(node.options, env)
        val correct = node.correct?.map { toNumber(evaluate(it, env), node.line).toInt() }
        correct?.forEach {
            if (it < 1 || it > options.size) throw Exception(
                "Error en la linea ${node.line}: MULTIPLE_QUESTION: índice correcto ($it) fuera del rango (1..${options.size})."
            )
        }
        return MultipleQValue(
            evalOptionalNum(node.width, env),
            evalOptionalNum(node.height, env),
            node.label?.let { evaluate(it, env).toString() } as String,
            options,
            correct as ArrayList<Int>?,
            evalStyles(node.styles, env),
            countWildcards(node.width, node.height)
        )
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

    private fun executeCall(node: Call, env: Environment, into: MutableList<FormElement>? = null) {
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

        val argVals = node.arguments.map { arg ->
            (evaluate(arg, env) as? Double)
                ?: throw Exception(
                    "Error en la linea ${node.line}: Los argumentos de draw() deben ser de tipo number."
                )
        }

        val resolved = resolveWildcards(specialValue, argVals)
        addToForm(resolved, into)
    }

    private fun resolveWildcards(sv: SpecialValue, args: List<Double>): SpecialValue {
        var idx = 0
        fun next(): Double = if (idx < args.size) args[idx++] else
            throw Exception("Faltan argumentos para draw().")

        return when (sv) {
            is OpenQValue -> sv.copy(
                width = sv.width ?: next(),
                height = sv.height ?: next()
            )

            is DropQValue -> sv.copy(
                width = sv.width ?: next(),
                height = sv.height ?: next()
            )

            is SelectQValue -> sv.copy(
                width = sv.width ?: next(),
                height = sv.height ?: next()
            )

            is MultipleQValue -> sv.copy(
                width = sv.width ?: next(),
                height = sv.height ?: next()
            )
        }
    }

    private fun addToForm(sv: SpecialValue, into: MutableList<FormElement>?) {
        val element: FormElement = when (sv) {
            is OpenQValue -> OpenQuestionElement(
                sv.width,
                sv.height,
                sv.label,
                StyleAttributes.from(sv.styles)
            )

            is DropQValue -> DropQuestionElement(
                sv.width,
                sv.height,
                sv.label,
                sv.options,
                sv.correct,
                StyleAttributes.from(sv.styles)
            )

            is SelectQValue -> SelectQuestionElement(
                sv.width,
                sv.height,
                sv.label,
                sv.options,
                sv.correct,
                StyleAttributes.from(sv.styles)
            )

            is MultipleQValue -> MultipleQuestionElement(
                sv.width,
                sv.height,
                sv.label,
                sv.options,
                sv.correct,
                StyleAttributes.from(sv.styles)
            )
        }
        emit(element, into)
    }

    private fun executeIf(node: If, env: Environment, into: MutableList<FormElement>? = null) {
        if (this.isTrue(evaluate(node.condition, env), node.line)) {
            executeBlock(node.thenStatement, env.createChild(), into)
            return
        }
        if (node.elseIfStatement != null) {
            for (statement in node.elseIfStatement) {
                if (this.isTrue(evaluate(statement.condition, env), node.line)) {
                    if (statement.body != null) {
                        executeBlock(statement.body, env.createChild(), into)
                    }
                    return
                }
            }
        }
        node.elseStatement?.let { executeBlock(it, env.createChild(), into) }
    }

    private fun isTrue(value: Any, line: Int): Boolean = when (value) {
        is Int -> value >= 1
        is Double -> value >= 1.0
        else -> throw Exception(
            "Error en linea ${line}: Se esperaba un valor numérico en la condición, se obtuvo ${value::class.simpleName}."
        )
    }

    private fun executeWhile(
        node: While,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        while (this.isTrue(evaluate(node.condition, env), node.line)) {
            executeBlock(node.body, env.createChild(), into)
        }
    }

    private fun executeDoWhile(
        node: DoWhile,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        do {
            executeBlock(node.body, env.createChild(), into)
        } while (this.isTrue(evaluate(node.condition, env), node.line))
    }

    private fun executeFor(node: For, env: Environment, into: MutableList<FormElement>? = null) {
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
            executeBlock(node.body, loopEnv.createChild(), into)
            execute(node.update, loopEnv)
        }
    }

    private fun executeForRange(
        node: ForRange,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
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
            executeBlock(node.body, loopEnv.createChild(), into)
        }
    }

    private fun toNumber(value: Any, line: Int): Double {
        return when (value) {
            is Number -> value.toDouble()
            is SymbolValue -> when (value.type) {
                VariableType.NUMBER -> value.value as Double
                else -> throw Exception(
                    "Error en linea ${line}: Se esperaba number, se obtuvo ${value::class.simpleName}."
                )
            }

            else -> throw Exception(
                "Error en linea ${line}: Se esperaba number, se obtuvo ${value::class.simpleName}."
            )
        }
    }

    private fun executeSection(
        node: Section,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        val children = mutableListOf<FormElement>()
        val sectionEnv = env.createChild()
        node.elements?.forEach { execute(it, sectionEnv, children) }
        val element = SectionElement(
            toNumber(evaluate(node.width, env), node.line),
            toNumber(evaluate(node.height, env), node.line),
            toNumber(evaluate(node.pointX, env), node.line),
            toNumber(evaluate(node.pointY, env), node.line),
            node.orientation ?: OrientationType.VERTICAL,
            children,
            StyleAttributes.from(evalStyles(node.styles, env))
        )
        emit(element, into)
    }

    private fun executeTable(
        node: Table,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        val tableEnv = env.createChild()
        val rows = node.elements.map { rowNode ->
            val rowChildren = mutableListOf<FormElement>()
            execute(rowNode, tableEnv, rowChildren)
            rowChildren.toList()
        }

        val element = TableElement(
            toNumber(evaluate(node.width, env), node.line),
            toNumber(evaluate(node.height, env), node.line),
            toNumber(evaluate(node.pointX, env), node.line),
            toNumber(evaluate(node.pointY, env), node.line),
            rows,
            StyleAttributes.from(evalStyles(node.styles, env))
        )
        emit(element, into)
    }

    private fun executeTextElement(
        node: Text,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        val element = TextElement(
            node.width?.let { toNumber(evaluate(it, env), node.line) },
            node.height?.let { toNumber(evaluate(it, env), node.line) },
            evaluate(node.content, env).toString(),
            StyleAttributes.from(evalStyles(node.styles, env))
        )
        emit(element, into)
    }

    private fun executeOpenQuestion(
        node: OpenQuestion,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        val element = OpenQuestionElement(
            node.width?.let { toNumber(evaluate(it, env), node.line) },
            node.height?.let { toNumber(evaluate(it, env), node.line) },
            evaluate(node.label, env).toString(),
            StyleAttributes.from(evalStyles(node.styles, env))
        )
        emit(element, into)
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

    private fun executeDropQuestion(
        node: DropQuestion,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
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
        val element = DropQuestionElement(
            node.width?.let { toNumber(evaluate(it, env), node.line) },
            node.height?.let { toNumber(evaluate(it, env), node.line) },
            label,
            options,
            correct,
            StyleAttributes.from(evalStyles(node.styles, env))
        )
        emit(element, into)
    }

    private fun resolveOptions(src: OptionsSource, env: Environment): ArrayList<String> =
        when (src) {
            is LiteralOptions -> src.options.map { evaluate(it, env).toString() }
            is PokemonOptions -> {
                val csv = evaluate(src.pokemonQuery, env) as String
                csv.split(",").map { it.trim() }
            }
        } as ArrayList<String>

    private fun executeSelectQuestion(
        node: SelectQuestion,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        val options = resolveOptions(node.options, env)
        val label = node.label?.let { evaluate(it, env).toString() }
        val correct = node.correct?.let { toNumber(evaluate(it, env), node.line).toInt() }

        correct?.let {
            if (it < 1 || it > options.size) {
                throw Exception(
                    "Error en la linea ${node.line}. SELECT_QUESTION: 'correct' ($it) fuera del rango (1..${options.size})."
                )
            }
        }
        val element = SelectQuestionElement(
            node.width?.let { toNumber(evaluate(it, env), node.line) },
            node.height?.let { toNumber(evaluate(it, env), node.line) },
            label,
            options,
            correct,
            StyleAttributes.from(evalStyles(node.styles, env))
        )
        emit(element, into)
    }

    private fun executeMultipleQuestion(
        node: MultipleQuestion,
        env: Environment,
        into: MutableList<FormElement>? = null
    ) {
        val options = resolveOptions(node.options, env)
        val label = node.label?.let { evaluate(it, env).toString() }
        val correct = node.correct?.map { toNumber(evaluate(it, env), node.line).toInt() }

        correct?.forEach {
            if (it < 1 || it > options.size) {
                throw Exception(
                    "Error en la linea ${node.line}. MULTIPLE_QUESTION: índice correcto ($it) fuera del rango (1..${options.size})."
                )
            }
        }
        val element = MultipleQuestionElement(
            node.width?.let { toNumber(evaluate(it, env), node.line) },
            node.height?.let { toNumber(evaluate(it, env), node.line) },
            label,
            options,
            correct,
            StyleAttributes.from(evalStyles(node.styles, env))
        )
        emit(element, into)
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
            OperatorType.SUMA -> when (left) {
                is Number -> when (right) {
                    is Number -> left.toDouble() + right.toDouble()
                    is String -> "$left$right"
                    is SymbolValue -> when {
                        right.type == VariableType.NUMBER -> left.toDouble() + right.value as Double
                        right.type == VariableType.STRING -> left.toString() + right.value as String
                        else -> throw Exception(
                            "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                        )
                    }

                    else -> throw Exception(
                        "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                    )
                }

                is String -> when (right) {
                    is Number -> "$left$right"
                    is String -> "$left$right"
                    is SymbolValue -> when {
                        right.type == VariableType.NUMBER -> left + right.value.toString()
                        right.type == VariableType.STRING -> left + right.value as String
                        else -> throw Exception(
                            "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                        )
                    }

                    else -> throw Exception(
                        "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                    )
                }

                is SymbolValue -> when {
                    left.type == VariableType.NUMBER -> when (right) {
                        is Number -> left.value as Double + right.toDouble()
                        is String -> "$left$right"
                        is SymbolValue -> when {
                            right.type == VariableType.NUMBER -> left.value as Double + right.value as Double
                            right.type == VariableType.STRING -> left.toString() + right.value as String
                            else -> throw Exception(
                                "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                            )
                        }

                        else -> throw Exception(
                            "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                        )
                    }

                    left.type == VariableType.STRING -> when (right) {
                        is Number -> "$left$right"
                        is String -> "$left$right"
                        is SymbolValue -> when {
                            right.type == VariableType.NUMBER -> left.value as String + right.value.toString()
                            right.type == VariableType.STRING -> left.value as String + right.value as String
                            else -> throw Exception(
                                "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                            )
                        }

                        else -> throw Exception(
                            "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                        )
                    }

                    else -> throw Exception(
                        "Error en la linea ${node.line}: Operación '+' no soportada entre ${left::class.simpleName} y ${right::class.simpleName}."
                    )
                }

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
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }

    private fun namedColor(name: ColorType): String = when (name) {
        ColorType.RED -> "#FF0000"
        ColorType.BLUE -> "#0000FF"
        ColorType.GREEN -> "#008000"
        ColorType.PURPLE -> "#800080"
        ColorType.SKY -> "#87CEEB"
        ColorType.YELLOW -> "#FFFF00"
        ColorType.BLACK -> "#000000"
        ColorType.WHITE -> "#FFFFFF"
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