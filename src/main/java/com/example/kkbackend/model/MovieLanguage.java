package com.example.kkbackend.model;

public enum MovieLanguage {
    RussianVoiceover("русская озвучка"),
    RussianSubtitles("русские субтитры");

    final String name;

    MovieLanguage(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
