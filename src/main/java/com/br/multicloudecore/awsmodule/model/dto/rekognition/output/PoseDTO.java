package com.br.multicloudecore.awsmodule.model.dto.rekognition.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PoseDTO {
    private final float pitch;
    private final float roll;
    private final float yaw;
}
