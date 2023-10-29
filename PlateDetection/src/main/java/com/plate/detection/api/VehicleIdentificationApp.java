package com.plate.detection.api;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import com.plate.detection.services.FramePreprocessor;
import com.plate.detection.services.LicensePlateDetector;
import com.plate.detection.services.NumberPlateExtraction;


public class VehicleIdentificationApp {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture videoCapture = new VideoCapture("C:/Users/sauravsahu/Downloads/test1.mp4"); 

        if (!videoCapture.isOpened()) {
            System.out.println("Error: Couldn't open video source.");
            System.exit(1);
        }

        // Create an instance of the LicensePlateDetector
        LicensePlateDetector detector = new LicensePlateDetector("C:/Users/sauravsahu/Downloads/haarcascade_russian_plate_number.xml");

        NumberPlateExtraction plate = new NumberPlateExtraction("C:/Users/sauravsahu/Downloads/indian_license_plate.xml");
        Mat frame = new Mat();

        while (true) {
            if (videoCapture.read(frame)) {
                Mat preprocessedFrame = FramePreprocessor.preprocessFrame(frame);
                Mat licensePlateMat = detector.detectAndRecognizeLicensePlate(preprocessedFrame);
                String realPlate = plate.detectAndRecognizeLicensePlate(licensePlateMat);
          
                if (preprocessedFrame != null) {  // Check if licensePlateMat is not null
                    HighGui.imshow("License Plate Detection", preprocessedFrame);
                    if (HighGui.waitKey(1) == 'q') {
                        break;
                    }
                }
                if (realPlate != null) {
                    System.out.println("Detected License Plate: " + realPlate);
                } else {
                    System.out.println("No license plate detected.");
                }
            } else {
                System.out.println("End of video stream.");
                break;
            }
        }

        videoCapture.release();
        HighGui.destroyAllWindows();
    }
}



