package connectfour

const val DEFAULT_ROWS = 6
const val DEFAULT_COLS = 7

fun promptForDimensions(): Pair<Int, Int> {
    while (true) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val regex = """\s*(\d+)\s*[x|X]\s*(\d+)\s*""".toRegex()
        val s = readln().trim()
        val (rows, cols) = when {
            regex.matches(s) -> regex.find(s)!!.destructured.toList().map { it.toInt() }
            s.isEmpty() -> listOf(DEFAULT_ROWS, DEFAULT_COLS)
            else -> {
                println("Invalid input")
                return promptForDimensions()
            }
        }
        if (rows !in 5..9) {
            println("Board rows should be from 5 to 9")
            return promptForDimensions()
        }
        if (cols !in 5..9) {
            println("Board columns should be from 5 to 9")
            return promptForDimensions()
        }
        return Pair(rows, cols)
    }
}

fun promptForNumberOfGames(): Int {
    println("""
               Do you want to play single or multiple games?
               For a single game, input 1 or press Enter
               Input a number of games:""".trimIndent())
    val s = readln().trim()
    return when {
        s.isEmpty() -> 1
        s.toIntOrNull() == null || s.toInt() < 1 -> {
            println("Invalid input")
            promptForNumberOfGames()
        }
        else -> s.toInt()
    }
}

data class Player(val name: String, val color: Char, var score: Int = 0)

fun printBoard(rows: Int, cols: Int, board: List<List<Char>>) {
    println(" " + (1..cols).joinToString(" ") + " ")
    for (row in rows - 1 downTo 0) {
        print("|")
        for (col in 0 until cols) {
            if (row >= board[col].size) {
                print(" |")
            } else {
                print("${board[col][row]}|")
            }
        }
        println()
    }
    println(List(2 * cols + 1) { "=" }.joinToString(""))
}

fun printScore(firstPlayer: Player, secondPlayer: Player) {
    println("Score")
    println("${firstPlayer.name}: ${firstPlayer.score} ${secondPlayer.name}: ${secondPlayer.score}")
}

fun checkBoardForWinningCondition(board: List<List<Char>>, rows: Int, cols: Int, color: Char): Boolean {
    for (col in 0 until cols) {
        for (row in 0..rows) {
            val vertical = listOf(
                board[col].getOrElse(row) { ' ' },
                board[col].getOrElse(row + 1) { ' ' },
                board[col].getOrElse(row + 2) { ' ' },
                board[col].getOrElse(row + 3) { ' ' })

            if (vertical.all { it == color }) {
                return true
            }

            if (col > cols - 4) {
                continue
            }

            val horizontal = listOf(
                board[col].getOrElse(row) { ' ' },
                board[col + 1].getOrElse(row) { ' ' },
                board[col + 2].getOrElse(row) { ' ' },
                board[col + 3].getOrElse(row) { ' ' })

            if (horizontal.all { it == color }) {
                return true
            }

            val diagonalUp = listOf(
                board[col].getOrElse(row) { ' ' },
                board[col + 1].getOrElse(row + 1) { ' ' },
                board[col + 2].getOrElse(row + 2) { ' ' },
                board[col + 3].getOrElse(row + 3) { ' ' })

            if (diagonalUp.all { it == color }) {
                return true
            }

            val diagonalDown = listOf(
                board[col].getOrElse(row) { ' ' },
                board[col + 1].getOrElse(row - 1) { ' ' },
                board[col + 2].getOrElse(row - 2) { ' ' },
                board[col + 3].getOrElse(row - 3) { ' ' })

            if (diagonalDown.all { it == color }) {
                return true
            }
        }
    }
    return false
}

fun main() {
    println("Connect Four")
    println("First player's name:")
    val firstPlayer = Player(readln().trim(), 'o')
    println("Second player's name:")
    val secondPlayer = Player(readln().trim(), '*')
    val (rows, cols) = promptForDimensions()
    val numGames = promptForNumberOfGames()
    println("${firstPlayer.name} VS ${secondPlayer.name}")
    println("$rows X $cols Board")
    when (numGames) {
        1 -> println("Single game")
        else -> println("Total $numGames games")
    }
    var currentPlayer = firstPlayer
    var nextPlayer = secondPlayer
    repeat(numGames) { game ->
        if (numGames > 1) {
            println("Game #${game + 1}")
        }
        val board = List(cols) { mutableListOf<Char>() }
        printBoard(rows, cols, board)
        while (true) {
            println("${currentPlayer.name}'s turn:")
            val s = readln().trim().lowercase()
            if (s == "end") {
                println("Game over!")
                return
            } else if (s.toIntOrNull() == null) {
                println("Incorrect column number")
                continue
            } else {
                val col = s.toInt()
                if (col !in 1..cols) {
                    println("The column number is out of range (1 - $cols)")
                    continue
                }
                if (board[col - 1].size == rows) {
                    println("Column $col is full")
                    continue
                }
                board[col - 1].add(currentPlayer.color)
            }
            printBoard(rows, cols, board)
            currentPlayer = nextPlayer.also { nextPlayer = currentPlayer  }
            if (checkBoardForWinningCondition(board, rows, cols, nextPlayer.color)) {
                println("Player ${nextPlayer.name} won")
                nextPlayer.score += 2
                printScore(firstPlayer, secondPlayer)
                break
            }
            if (board.all { it.size == rows }) {
                println("It is a draw")
                firstPlayer.score += 1
                secondPlayer.score += 1
                printScore(firstPlayer, secondPlayer)
                break
            }
        }
    }
    println("Game over!")
}