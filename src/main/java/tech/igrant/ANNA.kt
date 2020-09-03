package tech.igrant

enum class ANNA(val canonicalName: String) {

    REST_CONTROLLER("org.springframework.web.bind.annotation.RestController"),
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping"),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping"),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping"),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping"),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping"),
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping"),

}