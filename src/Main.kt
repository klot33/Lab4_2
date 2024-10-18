import kotlin.random.Random

fun generateTable(random: Boolean): Map<Pair<Char, Char>, String> {
    val alphabet = ('А'..'Я').filterNot { it in listOf('Ё', 'Й') }
    val table = mutableMapOf<Pair<Char, Char>, String>()

    var code = 1

    for (row in alphabet) {
        for (col in alphabet) {
            val codeString = if (random) {
                var randomCode: String
                do {
                    randomCode = Random.nextInt(1, 1000).toString().padStart(3, '0')
                } while (table.values.contains(randomCode))
                randomCode
            } else {
                code.toString().padStart(3, '0')
            }
            table[row to col] = codeString
            code++
        }
    }
    return table
}

fun encryptMessage(message: String, auxChar: Char, table: Map<Pair<Char, Char>, String>): String {
    val cleanedMessage = message.filter { it.isLetter() }.uppercase()
    val messagePairs = cleanedMessage.chunked(2).map {
        if (it.length == 1) it + auxChar else it
    }

    val encryptedMessage = messagePairs.joinToString(" ") { pair ->
        val first = pair[0]
        val second = pair[1]
        table[first to second] ?: "???"
    }

    return encryptedMessage
}

fun printTable(table: Map<Pair<Char, Char>, String>) {
    val alphabet = ('А'..'Я').filterNot { it in listOf('Ё', 'Й') }
    println("\nШифровальная таблица:")
    print("    ")
    alphabet.forEach { print(" $it  ") }
    println()
    println("   +" + "---+".repeat(alphabet.size))

    for (row in alphabet) {
        print("$row | ")
        val rowValues = alphabet.joinToString(" | ") { col ->
            table[row to col] ?: "???"
        }
        println(rowValues)
    }
}

fun isValidMessage(message: String): Boolean {
    return message.all { it in 'А'..'Я' || it in 'а'..'я' }
}

fun isValidAuxChar(auxChar: String): Boolean {
    return auxChar.length == 1 && auxChar.first() in 'А'..'Я'
}

fun isValidTableChoice(choice: String): Boolean {
    return choice.lowercase() == "y" || choice.lowercase() == "n"
}

fun main() {
    var message: String
    var auxChar: Char = 'X'
    var useDefaultTable: Boolean = false

    do {
        println("Введите сообщение (только русские буквы):")
        message = readLine()!!
        if (!isValidMessage(message)) {
            println("Сообщение должно содержать только русские буквы.")
        }
    } while (!isValidMessage(message))

    do {
        println("Введите вспомогательный символ (одна русская буква):")
        val auxInput = readLine()!!.uppercase()
        if (!isValidAuxChar(auxInput)) {
            println("Вспомогательный символ должен быть одной русской буквой.")
        } else {
            auxChar = auxInput.first()
        }
    } while (!isValidAuxChar(auxInput))

    do {
        println("Использовать типовую таблицу (y/n)?")
        val tableChoice = readLine()!!
        if (!isValidTableChoice(tableChoice)) {
            println("Введите 'y' для типовой таблицы или 'n' для случайной.")
        } else {
            useDefaultTable = tableChoice.lowercase() == "y"
        }
    } while (!isValidTableChoice(tableChoice))

    val table = generateTable(!useDefaultTable)

    val encryptedMessage = encryptMessage(message, auxChar, table)

    println("\nИсходное сообщение разбитое по парам букв:")
    println(message.chunked(2).joinToString(" "))

    println("\nЗашифрованное сообщение:")
    println(encryptedMessage)

    printTable(table)
}