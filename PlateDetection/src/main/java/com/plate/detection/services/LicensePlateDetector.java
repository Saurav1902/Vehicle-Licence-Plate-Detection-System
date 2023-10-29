package com.plate.detection.services;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.objdetect.CascadeClassifier;

public class LicensePlateDetector {

    private CascadeClassifier plateCascade;

    public LicensePlateDetector(String cascadeFile) {
        plateCascade = new CascadeClassifier(cascadeFile);
        if (plateCascade.empty()) {
            System.err.println("Cascade classifier not found");
        }
    }

    public Mat detectAndRecognizeLicensePlate(Mat preprocessedFrame) {
        MatOfRect plates = new MatOfRect();
        plateCascade.detectMultiScale(preprocessedFrame, plates);
        
        Mat licensePlateMat = null;  // Initialize licensePlateMat as null
        
        for (Rect rect : plates.toArray()) {
            // Draw a rectangle around the detected license plate
            Imgproc.rectangle(preprocessedFrame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);

            // Crop the detected license plate region
            licensePlateMat = preprocessedFrame.submat(rect);
        }
        
        return licensePlateMat;  
    }


}







