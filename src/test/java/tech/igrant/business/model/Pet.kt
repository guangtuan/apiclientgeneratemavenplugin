package tech.igrant.business.model

data class Pet(
        private val name: String,
        private val id: Long
)

data class PetDto(
        private val name: String
)

data class QueryCondition(
        private val name: String
)