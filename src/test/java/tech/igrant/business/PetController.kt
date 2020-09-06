package tech.igrant.business

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.igrant.business.model.Pet
import tech.igrant.business.model.PetDto
import tech.igrant.business.model.QueryCondition
import java.util.*

@RestController
@RequestMapping("api/v1/pets")
class PetController {

    @GetMapping()
    fun list(): ResponseEntity<List<Pet>> {
        return ResponseEntity.ok(listOf())
    }

    @PostMapping()
    fun create(@RequestBody petDto: PetDto): ResponseEntity<Pet> {
        return ResponseEntity.of(Optional.empty())
    }

    @DeleteMapping("{id}")
    fun del(@PathVariable("id") id: Long): ResponseEntity<Any> {
        return ResponseEntity.noContent().build<Any>();
    }

    @GetMapping(params = ["by=name"])
    fun queryByName(@RequestParam("name") name: String): ResponseEntity<List<Pet>> {
        return ResponseEntity.ok(listOf())
    }

    @PostMapping("/query")
    fun queryByCondition(@RequestBody(required = false) queryCondition: QueryCondition): ResponseEntity<List<Pet>> {
        return ResponseEntity.ok(listOf())
    }

}