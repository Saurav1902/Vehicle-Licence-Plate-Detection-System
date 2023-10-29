package com.plate.detection.services;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

public class FramePreprocessor {

    public static Mat preprocessFrame(Mat frame) {
    	Mat grayFrame = new Mat();
    	Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // Enhance contrast using CLAHE with adjusted parameters
        Mat enhancedFrame = enhanceContrast(grayFrame);

        // Reduce noise using Gaussian blur with adjusted parameters
        Mat noiseReducedFrame = reduceNoise(enhancedFrame);

        // Apply sharpening to make the image clearer
        Mat sharpenedFrame = sharpenImage(noiseReducedFrame);

        // Increase overall brightness
        adjustBrightness(sharpenedFrame, 0.4); // Adjust brightness factor as needed

        return sharpenedFrame;
    }

    private static Mat enhanceContrast(Mat grayFrame) {
        Mat enhancedFrame = new Mat();
        CLAHE clahe = Imgproc.createCLAHE(2.0, new Size(8, 8)); // Adjust clip limit and tile size
        clahe.apply(grayFrame, enhancedFrame);
        return enhancedFrame;
    }

    private static Mat reduceNoise(Mat frame) {
        Mat noiseReducedFrame = new Mat();
        Imgproc.GaussianBlur(frame, noiseReducedFrame, new Size(3, 3), 0); // Adjust Gaussian blur kernel size
        return noiseReducedFrame;
    }

    private static Mat sharpenImage(Mat frame) {
        Mat sharpenedFrame = new Mat();
        Imgproc.GaussianBlur(frame, sharpenedFrame, new Size(0, 0), 3);
        Core.addWeighted(frame, 1.5, sharpenedFrame, -0.5, 0, sharpenedFrame);
        return sharpenedFrame;
    }

    private static void adjustBrightness(Mat frame, double brightnessFactor) {
        frame.convertTo(frame, -1, brightnessFactor, 0); // Adjust brightness using the brightnessFactor
    }
}
