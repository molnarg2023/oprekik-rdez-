package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class QuizGame {
    private static final int QUESTION_COUNT = 15;
    private static final double PASSING_SCORE = 8.5;

    private JFrame frame;
    private JLabel questionLabel;
    private JButton trueButton, falseButton, skipButton;
    private JLabel feedbackLabel;

    private List<Question> questions;
    private int currentQuestionIndex;
    private double score;

    public QuizGame(String fileName) {
        questions = loadQuestions(fileName);
        Collections.shuffle(questions);
        if (questions.size() > QUESTION_COUNT) {
            questions = questions.subList(0, QUESTION_COUNT);
        }

        score = 0;
        currentQuestionIndex = 0;

        initComponents();
        showNextQuestion();
    }

    private void initComponents() {
        frame = new JFrame("Quiz Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1700, 300);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(questionLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        trueButton = new JButton("Ja");
        falseButton = new JButton("Faszt");
        skipButton = new JButton("Hagyjuk");
        buttonPanel.add(trueButton);
        buttonPanel.add(falseButton);
        buttonPanel.add(skipButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        frame.add(feedbackLabel, BorderLayout.NORTH);

        trueButton.addActionListener(e -> handleAnswer("I"));
        falseButton.addActionListener(e -> handleAnswer("H"));
        skipButton.addActionListener(e -> handleSkip());

        frame.setVisible(true);
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionLabel.setText(question.getText());
            feedbackLabel.setText("");
            enableButtons(true);
        } else {
            showFinalScore();
        }
    }

    private void handleAnswer(String answer) {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            enableButtons(false);
            if (answer.equals(question.getAnswer())) {
                score += 1;
                feedbackLabel.setText("Így van!");
            } else {
                score -= 0.5;
                if (score < 0){ score = 0; }
                feedbackLabel.setText("Hülye! Helyes válasz: " + question.getAnswer());
            }
            currentQuestionIndex++;
            delayNextQuestion();
        }
    }

    private void handleSkip() {
        if (currentQuestionIndex < questions.size()) {
            enableButtons(false);
            feedbackLabel.setText("Kérdés kihagyva.");
            currentQuestionIndex++;
            delayNextQuestion();
        }
    }

    private void delayNextQuestion() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> showNextQuestion());
            }
        }, 1500); // 2 másodperc késleltetés
    }

    private void enableButtons(boolean enable) {
        trueButton.setEnabled(enable);
        falseButton.setEnabled(enable);
        skipButton.setEnabled(enable);
    }

    private void showFinalScore() {
        String result = score >= PASSING_SCORE ? "Fasza" : "Gatya";
        JOptionPane.showMessageDialog(frame, "Fucky Wucky " + score + "\n" + result);
        frame.dispose();
        System.exit(0);
    }

    private List<Question> loadQuestions(String fileName) {
        List<Question> questions = new ArrayList<>();
        try {
            URL resource = getClass().getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new IOException("Fájl nem található: " + fileName);
            }
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(resource.toURI()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 2);
                    if (parts.length == 2) {
                        questions.add(new Question(parts[0].trim(), parts[1].trim()));
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Nem sikerült betölteni a kérdéseket: " + e.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return questions;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizGame("questions.csv"));
    }
}

class Question {
    private String answer;
    private String text;

    public Question(String answer, String text) {
        this.answer = answer;
        this.text = text;
    }

    public String getAnswer() {
        return answer;
    }

    public String getText() {
        return text;
    }
}



