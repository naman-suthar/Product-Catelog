package app.naman.lumostest.domain.model

sealed class AppError(open val message: String) {
    data class Network(override val message: String = "No internet connection.") : AppError(message)
    data class Timeout(override val message: String = "Request timed out.") : AppError(message)
    data class Http(val code: Int, override val message: String) : AppError(message)
    data class Unknown(override val message: String = "Something went wrong.") : AppError(message)
}

