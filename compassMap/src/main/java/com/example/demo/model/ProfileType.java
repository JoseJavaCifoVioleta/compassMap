package com.example.demo.model;

public enum ProfileType {
    ARCHITECT("Experienced developer", "Architecture, DDD, best practices, controlled AI"),
    AI_PRACTITIONER("Coder who ships fast", "AI-assisted development, rapid prototyping"),
    BEGINNER("Absolute beginner", "Logic, algorithms, computational thinking"),
    NON_DEVELOPER("Non-technical professional", "Architectural literacy, tech communication");

    private final String situation;
    private final String focus;

    ProfileType(String situation, String focus) {
        this.situation = situation;
        this.focus = focus;
    }

    public String getSituation() { return situation; }
    public String getFocus() { return focus; }

    public void printCard() {
        System.out.println("╔═════════════════════════════════════════════════════════════════╗");
        System.out.printf( "║ Profile: %-55s║%n", this.name());
        System.out.printf( "║ Who:     %-55s║%n", situation);
        System.out.printf( "║ Focus:   %-55s║%n", focus);
        System.out.println("╚═════════════════════════════════════════════════════════════════╝");
    }
}
