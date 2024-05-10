package com.br.multicloudecore.awsmodule.model.dto.rekognition.result;

import com.br.multicloudecore.awsmodule.model.dto.rekognition.output.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FaceDetailDTO {

    private AgeRangeDTO ageRange;

    private BeardDTO beard;

    private BoundingBoxDTO boundingBox;

    private float confidence;

    private List<EmotionDTO> emotions;

    private EyeDirectionDTO eyeDirection;
    private EyeglassesDTO eyeGlasses;
    private EyeOpenDTO eyeOpen;
    private FaceOccludedDTO faceOccluded;
    private GenderDTO gender;

    private List<LandmarkDTO> landmarks;

    private MouthOpenDTO mouthOpen;
    private MustacheDTO mustache;
    private PoseDTO pose;
    private QualityDTO quality;
    private SmileDTO smile;
    private SunglassesDTO sunglasses;
}
