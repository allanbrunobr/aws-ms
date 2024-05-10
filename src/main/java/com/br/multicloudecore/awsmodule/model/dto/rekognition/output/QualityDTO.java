package com.br.multicloudecore.awsmodule.model.dto.rekognition.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class QualityDTO {
    private final float brightness;
    private final float sharpness;
}
