package com.prismhealth.Models

import lombok.Data
import org.bson.types.Binary
import org.springframework.data.annotation.Id

@Data
class Photos {
    @Id
    var id: String? = null
    var photo: Binary? = null
}