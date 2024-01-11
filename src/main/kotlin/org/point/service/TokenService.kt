package org.point.service

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TokenService {
    fun parseToken(): UUID {
        // TODO:
        return UUID.fromString("5b7d3f89-5276-4790-a3e1-ed8248e6c6dd")
    }
}