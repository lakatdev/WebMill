package hu.keszeglab.webmill

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform