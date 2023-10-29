package com.plate.detection.services;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class NumberPlateExtraction {

	private CascadeClassifier plateCascade;
	private Pattern licensePlatePattern;

	public NumberPlateExtraction(String cascadeFile) {
		plateCascade = new CascadeClassifier(cascadeFile);
		if (plateCascade.empty()) {
			System.err.println("Cascade classifier not found");
		}

		// Define a regular expression pattern for the desired license plate format
		licensePlatePattern = Pattern.compile("\\d{2}-[A-Z]{2}-\\d{3}");
	}

	private BufferedImage matToBufferedImage(Mat mat) {
		int width = mat.cols();
		int height = mat.rows();
		int channels = mat.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		mat.get(0, 0, sourcePixels);

		if (mat.channels() > 1) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
			return image;
		} else if (mat.channels() == 1) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
			return image;
		} else {
			return null;
		}
	}

	public String detectAndRecognizeLicensePlate(Mat preprocessedFrame) {
		MatOfRect plates = new MatOfRect();
		if (preprocessedFrame != null) {
			plateCascade.detectMultiScale(preprocessedFrame, plates);

			for (Rect rect : plates.toArray()) {
				// Draw a rectangle around the detected license plate
				Imgproc.rectangle(preprocessedFrame, new Point(rect.x, rect.y),
						new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);

				// Crop the detected license plate region
				Mat licensePlateMat = preprocessedFrame.submat(rect);

				// Perform OCR on the enhanced license plate region
				String licensePlateText = recognizeLicensePlateText(licensePlateMat);

				// Check if the recognized text matches the desired format
				if (!licensePlateText.isEmpty() && isLicensePlateFormatValid(licensePlateText)) {
					return licensePlateText;
				}
			}
		}
		return null;
	}

	private String recognizeLicensePlateText(Mat licensePlateMat) {
		ITesseract tesseract = new Tesseract();
		tesseract.setDatapath("C:/Users/sauravsahu/AppData/Local/Programs/Tesseract-OCR/tessdata"); // Set the path to
																									// your Tesseract
																									// data directory
		tesseract.setLanguage("eng"); // Set the language

		try {
			BufferedImage bufferedImage = matToBufferedImage(licensePlateMat);
			String result = tesseract.doOCR(bufferedImage);
			return result.trim();
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		return "";
	}

	private boolean isLicensePlateFormatValid(String licensePlateText) {
		Matcher matcher = licensePlatePattern.matcher(licensePlateText);
		return matcher.matches();
	}
}
