package com.br.multicloudecore.awsmodule.model.dto.rekognition.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BoundingBoxDTO {
    private final float height;
    private final float left;
    private final float top;
    private final float width;

}
