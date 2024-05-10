package com.br.multicloudecore.awsmodule.model.dto.rekognition.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EyeglassesDTO {
    private final float confidence;
    private final boolean value;
}
