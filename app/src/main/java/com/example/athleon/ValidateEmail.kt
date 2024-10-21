package com.example.athleon

import java.util.regex.Matcher
import java.util.regex.Pattern



class ValidateEmail {
    companion object {
        // Patrón para validar un correo electrónico.
        private val emailPattern = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )

        // Método para validar si el string proporcionado es un email válido.
        fun isEmail(email: String): Boolean {
            // Utiliza el matcher del patrón sobre el email proporcionado.
            val matcher = emailPattern.matcher(email)
            // Retorna true si el email coincide con el patrón.
            return matcher.matches()
        }
    }
}