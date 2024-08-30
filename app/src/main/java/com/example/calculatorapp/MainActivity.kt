package com.example.calculatorapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculatorapp.databinding.ActivityMainBinding
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var input: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inisialiasi view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //map button angka ke nilai angka
        val numberButtons = mapOf(
            binding.button0 to "0",
            binding.button1 to "1",
            binding.button2 to "2",
            binding.button3 to "3",
            binding.button4 to "4",
            binding.button5 to "5",
            binding.button6 to "6",
            binding.button7 to "7",
            binding.button8 to "8",
            binding.button9 to "9"
        )

        //map button operator ke nilai operator
        val operatorButtons = mapOf(
            binding.buttonAdd to "+",
            binding.buttonSubtract to "-",
            binding.buttonMultiply to "*",
            binding.buttonDivide to "/"
        )

        //set onClickListener untuk button angka
        numberButtons.forEach { (button, value) ->
            button.setOnClickListener { appendNumber(value) }
        }

        //set onClickListener untuk button operator
        operatorButtons.forEach { (button, operator) ->
            button.setOnClickListener { appendOperator(operator) }
        }

        //set onClickListener untuk button sama dengan, clear, dan delete
        binding.buttonEquals.setOnClickListener { calculateResult() }
        binding.buttonClear.setOnClickListener { clearInput() }
        binding.buttonDelete.setOnClickListener { deleteLast() }
    }

    //fungsi untuk menambahkan angka ke input
    private fun appendNumber(number: String) {
        input += number
        binding.displayTextView.text = input
    }

    //fungsi untuk menambahkan operator ke input
    private fun appendOperator(op: String) {
        if (input.isNotEmpty() && !isOperator(input.last())) {
            input += " $op "
            binding.displayTextView.text = input
        }
    }

    //fungsi untuk menghitung hasil dari user input
    private fun calculateResult() {
        try {
            val result = evaluateExpression(input)
            //jika hasilnya adalah bilangan bulat, maka hasilnya akan ditampilkan tanpa desimal
            if (result % 1 == 0.0) {
                binding.resultTextView.text = result.toInt().toString()
                Toast.makeText(this, "Hasil: $result", Toast.LENGTH_SHORT).show()
            } else {
                binding.resultTextView.text = result.toString()
                Toast.makeText(this, "Hasil: $result", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            binding.resultTextView.text = "Error"
        }
    }

    //fungsi untuk menghapus semua input
    private fun clearInput() {
        input = ""
        binding.displayTextView.text = ""
        binding.resultTextView.text = ""
    }

    //fungsi untuk menghapus karakter terakhir dari input
    private fun deleteLast() {
        if (input.isNotEmpty()) {
            input = input.trim().dropLast(1).trim()
            binding.displayTextView.text = input
        }
    }

    //fungsi untuk mengecek apakah karakter tersebut operator
    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '*' || c == '/'
    }

    //fungsi untuk menghitung ekspresi matematika menggunakan Stack
    private fun evaluateExpression(expression: String): Double {
        val tokens = expression.split(" ").filter { it.isNotEmpty() }
        val values = Stack<Double>()
        val operators = Stack<Char>()

        val precedence = mapOf(
            '+' to 1,
            '-' to 1,
            '*' to 2,
            '/' to 2
        )

        fun applyOperator(op: Char) {
            val right = values.pop()
            val left = values.pop()
            val result = when (op) {
                '+' -> left + right
                '-' -> left - right
                '*' -> left * right
                '/' -> left / right
                else -> throw IllegalArgumentException("Invalid operator")
            }
            values.push(result)
        }

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> values.push(token.toDouble())
                token.length == 1 && token[0] in precedence.keys -> {
                    while (operators.isNotEmpty() && precedence[operators.peek()]!! >= precedence[token[0]]!!) {
                        applyOperator(operators.pop())
                    }
                    operators.push(token[0])
                }
            }
        }

        while (operators.isNotEmpty()) {
            applyOperator(operators.pop())
        }

        return values.pop()
    }
}
