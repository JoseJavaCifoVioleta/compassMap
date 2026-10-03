package com.example.demo.utils;

import java.util.Scanner;
import com.example.demo.model.ProfileType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuestionnaireRunner {
    public String runQuestionnaire(Scanner scan) {
        String[] questions = {
                "Have you written code professionally? (y/n)",
                "Can you read a stack trace? (y/n)",
                "Do you want to deepen architecture/DDD skills? (y/n)",
                "Do you want to ship faster with AI tools? (y/n)",
                "Are you new to programming logic? (y/n)"
        };

        int knowsCode = 0;
        int wantsToLearn = 0;

        for (int i = 0; i < questions.length; i++) {
            System.out.print(questions[i] + " ");
            String answer = scan.nextLine().trim().toLowerCase();
            if (i < 2 && answer.equals("y")) knowsCode++;
            if (i >= 2 && answer.equals("y")) wantsToLearn++;
        }

        if (knowsCode >= 2 && wantsToLearn >= 2) return "Architect";
        if (knowsCode >= 2 && wantsToLearn < 2)  return "AI Practitioner";
        if (knowsCode < 2 && wantsToLearn >= 2)  return "Beginner";
        return "Non-Developer";
    }

    public ProfileType calculateProfile(Scanner scan) {
        String[] questions = {
                "Years of coding experience? (0-20): ",
                "GitHub repos you maintain? (0-50): ",
                "Interest in AI tools (1-10): ",
                "Interest in architecture (1-10): ",
                "Comfort reading documentation (1-10): "
        };
        double[] weights = {0.3, 0.1, 0.2, 0.2, 0.2};
        int[] answers = new int[questions.length];

        for (int i = 0; i < questions.length; i++) {
            System.out.print(questions[i]);
            answers[i] = Integer.parseInt(scan.nextLine());
        }

        // Weighted score
        double score = 0;
        for (int i = 0; i < answers.length; i++) {
            score += answers[i] * weights[i];
        }

        // Derived booleans
        // boolean experienced = (answers[0] >= 2 || answers[1] >= 3);
        // boolean wantsDepth  = answers[3] >= 6;

        // Derived booleans (Corrected with index positions)
        boolean experienced = (answers[0] >= 2 || answers[1] >= 3);
        boolean wantsDepth  = answers[3] >= 6;


        return experienced && wantsDepth   ? ProfileType.ARCHITECT
                : experienced && !wantsDepth  ? ProfileType.AI_PRACTITIONER
                : !experienced && wantsDepth  ? ProfileType.BEGINNER
                :                               ProfileType.NON_DEVELOPER;
    }

    public void saveProfileReport(String customerName, ProfileType profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPASS MAP REPORT ===\n");
        sb.append("Date: ").append(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("Profile:  ").append(profile.name()).append("\n");
        sb.append("Situation: ").append(profile.getSituation()).append("\n");
        sb.append("Focus:     ").append(profile.getFocus()).append("\n");
        sb.append("===========================\n");

        String filename = "report_" + customerName.replaceAll("\\s+", "_") + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(sb.toString());
            System.out.println("Report saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
        }
    }


}
