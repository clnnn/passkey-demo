package com.github.clnnn

import kotlinx.html.*

fun HTML.registrationPage() {
    head {
        title("Registration")
        script(src = "/static/registration.js") {}
        link(rel = "stylesheet", href = "/static/styles.css")
    }
    body {
        h1 { +"Registration" }
        form {
            id = "registerForm"
            input {
                id = "username"
                type = InputType.text
                name = "username"
                placeholder = "Enter Username"
            }
            button {
                id = "registerButton"
                type = ButtonType.submit
                +"Submit"
            }
        }
    }
}

fun HTML.loginPage() {
    head {
        title("Authentication")
        script(src = "/static/authentication.js") {}
        link(rel = "stylesheet", href = "/static/styles.css")
    }
    body {
        h1 { +"Authentication" }
        form {
            id = "authForm"
            input {
                id = "username"
                type = InputType.text
                placeholder = "Enter username"
            }
            button {
                id = "loginButton"
                +"Login"
            }
        }
    }

}