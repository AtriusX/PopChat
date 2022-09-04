package xyz.atrius.message

sealed class PasswordMessage(
    message: String,
    error: Boolean = true
) : ServerMessage(
    message = message,
    error = error
) {

    constructor(require: String) : this(message = "Password must include at least one $require!")

    object NoPasswordProvided : PasswordMessage(message = "Password must be provided!")

    class ImproperSize(min: Int, max: Int) : PasswordMessage(message = "Password must be between $min and $max characters!")

    object RequiresCapital : PasswordMessage(require = "capital letter")

    object RequiresNumber : PasswordMessage(require = "digit")

    object RequiresSpecial : PasswordMessage(require = "special character")
}