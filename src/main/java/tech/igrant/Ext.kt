package tech.igrant

fun String.slashToDot(): String {
    return replace("/", ".")
}

fun String.canonicalNameToSimpleName(): String {
    if (this.contains(".")) {
        return this.split(".").last()
    }
    return this
}

fun String.classNameToVariableName(): String {
    val simpleName = this.canonicalNameToSimpleName()
    return simpleName.first().toLowerCase() + simpleName.substring(IntRange(1, simpleName.length - 1))
}