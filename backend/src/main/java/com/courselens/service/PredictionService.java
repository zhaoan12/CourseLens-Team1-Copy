package com.courselens.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.courselens.model.Course;
import com.courselens.model.HistoricalDataPoint;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private OrtEnvironment env;
    private OrtSession session;

    @PostConstruct
    public void init() {
        try {
            env = OrtEnvironment.getEnvironment();
            InputStream modelStream = getClass().getResourceAsStream("/ml/course_prediction_model.onnx");
            if (modelStream == null) {
                throw new RuntimeException("Model not found in resources: /ml/course_prediction_model.onnx");
            }
            Path tempFile = Files.createTempFile("model", ".onnx");
            Files.copy(modelStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            session = env.createSession(tempFile.toString(), new OrtSession.SessionOptions());
            System.out.println("ONNX model loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            session = null;
            System.err.println("Failed to load ONNX model. PredictionService will use fallback logic.");
        }
    }

    public boolean isCourseLikelyToClose(Course course) {
        if (session == null || course.getHistoricalData() == null || course.getHistoricalData().isEmpty()) {
            // Fallback logic
            return (double) course.getCurrentEnrollment() / course.getCapacity() >= 0.95;
        }

        try {
            // Use the latest data point for prediction
            HistoricalDataPoint latestData = course.getHistoricalData().get(course.getHistoricalData().size() - 1);

            // 1. Feature Engineering
            float[] features = createFeatures(course, latestData);

            // 2. Create Input Tensor
            String inputName = session.getInputInfo().keySet().iterator().next();
            long[] shape = {1, features.length};
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(features), shape);

            // 3. Run Prediction
            OrtSession.Result results = session.run(Collections.singletonMap(inputName, inputTensor));

            // 4. Process Output
            // The model outputs two values: the predicted label and the probabilities. We want the label.
            long[][] output = (long[][]) results.get(0).getValue();
            return output[0][0] == 1;

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to dummy logic if prediction fails
            return (double) course.getCurrentEnrollment() / course.getCapacity() >= 0.95;
        }
    }

    private float[] createFeatures(Course course, HistoricalDataPoint latestData) {
        List<HistoricalDataPoint> history = course.getHistoricalData();
        int historySize = history.size();

        // 'enrollment_ratio'
        float enrollmentRatio = (float) latestData.getEnrollment() / course.getCapacity();

        // 'days_until_end' - Assuming a 90-day term for this mock data
        LocalDate startDate = history.get(0).getDate();
        LocalDate endDate = startDate.plusDays(90);
        float daysUntilEnd = ChronoUnit.DAYS.between(latestData.getDate(), endDate);

        // 'day_of_week'
        float dayOfWeek = latestData.getDate().getDayOfWeek().getValue();

        // 'days_since_start'
        float daysSinceStart = ChronoUnit.DAYS.between(startDate, latestData.getDate());

        // Lag features
        float enrollmentLag1 = historySize > 1 ? (float) history.get(historySize - 2).getEnrollment() : 0;
        float enrollmentLag3 = historySize > 3 ? (float) history.get(historySize - 4).getEnrollment() : 0;
        float enrollmentLag7 = historySize > 7 ? (float) history.get(historySize - 8).getEnrollment() : 0;

        // Rolling window features
        List<Double> last7DaysEnrollment = history.stream()
            .skip(Math.max(0, historySize - 7))
            .map(p -> (double) p.getEnrollment())
            .collect(Collectors.toList());

        float rolMean7 = (float) last7DaysEnrollment.stream().mapToDouble(d -> d).average().orElse(0.0);

        double sumOfSquares = last7DaysEnrollment.stream().mapToDouble(d -> (d - rolMean7) * (d - rolMean7)).sum();
        float rolStd7 = (float) Math.sqrt(sumOfSquares / Math.max(1, last7DaysEnrollment.size()));

        return new float[]{
            enrollmentRatio,
            daysUntilEnd,
            dayOfWeek,
            daysSinceStart,
            enrollmentLag1,
            enrollmentLag3,
            enrollmentLag7,
            rolMean7,
            rolStd7
        };
    }
}
