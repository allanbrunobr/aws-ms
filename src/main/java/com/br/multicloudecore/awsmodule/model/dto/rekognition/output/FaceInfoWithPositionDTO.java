package com.br.multicloudecore.awsmodule.model.dto.rekognition.output;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.result.FaceDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaceInfoWithPositionDTO {
    private FaceDetailDTO faceDetailDTO;
    private BoundingBox boundingBox;
}
