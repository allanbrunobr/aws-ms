package com.br.multicloudecore.awsmodule.model.dto.rekognition.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EyeDirectionDTO {
    private final float confidence;
    private final float pitch;
    private final float yaw;
}
