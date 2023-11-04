package com.example.oops;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoController implements Initializable {
    public ImageView VideoOut;

    public Label TextOutput;
    public Button start;
    public Button stop;
    private VideoCapture capture;
    private boolean capturing = false;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        capture = new VideoCapture(0);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 400);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 200);
        capture.set(Videoio.CAP_PROP_FPS, 10);
        if (capture.isOpened()) {
            capturing = true;
            startCapture();
            startText();

        } else {
            System.err.println("Camera not found.");
        }

    }

    public void stopCapture() {
        capturing = false;
    }


    private void startText() {
        executor.execute(() -> {
            try {
                textStream();
            } catch (IOException e) {
                System.out.println("Error in Text Stream");
            }
        });

        }


    public void textStream() throws IOException {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("./src/main/resources/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("./src/main/resources/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("./src/main/resources/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        configuration.setGrammarPath("./src/main/resources/");
        configuration.setGrammarName("grammar");
        configuration.setUseGrammar(true);

        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);

        recognizer.startRecognition(true);

        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            {
                SpeechResult finalResult = result;
                Platform.runLater(() -> {
                    String output;
                    switch(finalResult.getHypothesis())
                    {

                        case "hello":
                        case "hi":
                        case "hey":output="Hello there User";
                            break;
                        case "left": output="Moving left";
                        break;
                        case "right":output="Moving right";
                            break;
                        case "up":
                        case "top":output="Moving up";
                            break;
                        case "down":
                        case "bottom":output="Moving down";
                            break;
                        case "select":output="Selecting choice";
                            break;
                        case "remove":
                        case "delete":output="Deleting choice";
                            break;
                        case "create":output="Creating choice";
                            break;
                        case "copy":output="Copying choice";
                            break;
                        case "paste":output="Pasting choice";
                            break;
                        case "minimize":output="Minimizing window";
                            ((Stage)(TextOutput.getScene()).getWindow()).setIconified(true);
                            break;
                        case "maximize":output="Maximizing window";
                            ((Stage)(TextOutput.getScene()).getWindow()).setIconified(false);
                            break;
                        default:output="";
                        break;

                    }




                    TextOutput.setText(output);
                });


            }
        }
        recognizer.stopRecognition();
    }

    public void startCapture()
    {
        capturing = true;
        beginCapture();
    }



    public void beginCapture() {
        if (capturing) {

            CascadeClassifier cascadeClassifier = new CascadeClassifier();
            cascadeClassifier.load("./src/main/resources/myhaar.xml");
            executor.execute(() -> {
                        Mat frame = new Mat();
                        while (capturing) {
                            if (capture.read(frame)) {
                                Mat newframe = detectFace(frame,cascadeClassifier);
                                Image imageToShow = mat2Image(newframe);
                                updateImageView(VideoOut, imageToShow);
                            }
                        }
                    }
            );

        }
    }

    private Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    private void updateImageView(ImageView view, Image image) {
        if (view != null && image != null) {
            view.setImage(image);
        }
    }

    public static Mat detectFace(Mat inputImage, CascadeClassifier cascadeClassifier) {
        MatOfRect personDetected = new MatOfRect();
        int minFaceSize = Math.round(inputImage.rows() );
        cascadeClassifier.detectMultiScale(inputImage,
                personDetected,
                1.2,
                6,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize* 0.2f, minFaceSize* 0.2f),
                new Size()
        );



        Rect[] personArray =  personDetected.toArray();

        for(Rect face : personArray) {
            Imgproc.rectangle(inputImage, face.tl(), face.br(), new Scalar(0, 255, 0), 2 );

            Point labelPosition = new Point(face.tl().x, face.tl().y - 10); // Adjust the position as needed
            Imgproc.putText(inputImage, "Face", labelPosition, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
        }

        return inputImage;
    }
}