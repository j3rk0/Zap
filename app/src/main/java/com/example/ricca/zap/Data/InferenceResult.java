package com.example.ricca.zap.Data;

public class InferenceResult implements Comparable<InferenceResult>{
    private String title;
    private float confidence;

    public InferenceResult(String title, float confidence) {
        this.title = title;
        this.confidence = confidence;
    }


    public String getTitle() {
        return title;
    }

    public float getConfidence() {
        return confidence;
    }

    @Override
    public int compareTo(InferenceResult o) {
        return (int) ((10*o.getConfidence())-(this.confidence*10));
    }
}
