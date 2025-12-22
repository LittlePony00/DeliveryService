package com.nikita.api.exception

class ResourceNotFoundException(resource: String, id: Any) : RuntimeException("$resource with $id ID not found")
