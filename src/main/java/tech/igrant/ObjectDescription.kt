package tech.igrant

data class FieldDescription(
        private val type: String,
        private val require: Boolean
)

data class ObjectDescription(
        private val className: String,
        private val listOfFields: List<FieldDescription>
)