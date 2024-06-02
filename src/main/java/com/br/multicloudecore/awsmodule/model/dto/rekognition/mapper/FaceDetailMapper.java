package com.br.multicloudecore.awsmodule.model.dto.rekognition.mapper;

import com.amazonaws.services.rekognition.model.Emotion;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Landmark;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.output.*;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.result.FaceDetailDTO;

import java.util.ArrayList;
import java.util.List;

public class FaceDetailMapper {

    public FaceDetailDTO map(FaceDetail face) {
        AgeRangeDTO ageRange = new AgeRangeDTO(face.getAgeRange().getHigh(), face.getAgeRange().getLow());
        BeardDTO beard = new BeardDTO(face.getBeard().getConfidence(), face.getBeard().getValue());

        BoundingBoxDTO boundingBox = new BoundingBoxDTO(
                face.getBoundingBox().getHeight(),
                face.getBoundingBox().getLeft(),
                face.getBoundingBox().getTop(),
                face.getBoundingBox().getWidth()
        );
        float confidence = face.getConfidence();
        List<EmotionDTO> emotions = new ArrayList<>();
        for (Emotion emotion : face.getEmotions()) {
            emotions.add(new EmotionDTO(emotion.getConfidence(), emotion.getType()));
        }
        EyeDirectionDTO eyeDirection = new EyeDirectionDTO(
                face.getPose().getPitch(),
                face.getPose().getRoll(),
                face.getPose().getYaw()
        );
        EyeglassesDTO eyeglasses = new EyeglassesDTO(face.getEyeglasses().getConfidence(), face.getEyeglasses().getValue());
        EyeOpenDTO eyesOpen = new EyeOpenDTO(face.getEyesOpen().getConfidence(), face.getEyesOpen().getValue());
        FaceOccludedDTO faceOccluded = new FaceOccludedDTO(face.getFaceOccluded().getConfidence(), face.getFaceOccluded().getValue());
        GenderDTO gender = new GenderDTO(face.getGender().getConfidence(), face.getGender().getValue());
        List<LandmarkDTO> landmarks = new ArrayList<>();
        for (Landmark landmark : face.getLandmarks()) {
            landmarks.add(new LandmarkDTO(landmark.getType(), landmark.getX(), landmark.getY()));
        }
        MouthOpenDTO mouthOpen = new MouthOpenDTO(face.getMouthOpen().getConfidence(), face.getMouthOpen().getValue());
        MustacheDTO mustache = new MustacheDTO(face.getMustache().getConfidence(), face.getMustache().getValue());
        PoseDTO pose = new PoseDTO(face.getPose().getPitch(), face.getPose().getRoll(), face.getPose().getYaw());
        QualityDTO quality = new QualityDTO(face.getQuality().getBrightness(), face.getQuality().getSharpness());
        SmileDTO smile = new SmileDTO(face.getSmile().getConfidence(), face.getSmile().getValue());
        SunglassesDTO sunglasses = new SunglassesDTO(face.getSunglasses().getConfidence(), face.getSunglasses().getValue());

        FaceDetailDTO faceDetailDTO = new FaceDetailDTO();
        faceDetailDTO.setAgeRange(ageRange);
        faceDetailDTO.setBeard(beard);
        faceDetailDTO.setBoundingBox(boundingBox);
        faceDetailDTO.setConfidence(confidence);
        faceDetailDTO.setEmotions(emotions);
        faceDetailDTO.setEyeDirection(eyeDirection);
        faceDetailDTO.setEyeGlasses(eyeglasses);
        faceDetailDTO.setEyeOpen(eyesOpen);
        faceDetailDTO.setFaceOccluded(faceOccluded);
        faceDetailDTO.setGender(gender);
        faceDetailDTO.setLandmarks(landmarks);
        faceDetailDTO.setMouthOpen(mouthOpen);
        faceDetailDTO.setMustache(mustache);
        faceDetailDTO.setPose(pose);
        faceDetailDTO.setQuality(quality);
        faceDetailDTO.setSmile(smile);
        faceDetailDTO.setSunglasses(sunglasses);

        return faceDetailDTO;
    }

}

