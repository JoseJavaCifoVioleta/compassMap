package com.example.demo.utils;

import com.example.demo.model.RoadMap;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.util.HashMap;
import com.example.demo.model.ProfileType;

public class BackOffice {

    // it is the entry-point for the backoffice class
    // and it has the main MENU, main options
    // and CRUD operation for our model and fake data
    public static void startBackOffice(PopulatorDB populatorDB) {
        // We will create just one scanner object
        // and it will pass to another methods
        Scanner scan = new Scanner(System.in);

        while (true) {
            mainMenu();

            String option = askMenuOption(scan);

            switch (option) {
                case "1":
                    // customerLoop(scan, populatorDB, populatorDB.customerService);
                    customerSubMenu(scan, populatorDB, populatorDB.customerService);
                    break;
                case "2":
                    // System.out.println("Roadmap - not implemented yet.");
                    // 1. Cargamos las rutas desde la fábrica usando populatorDB
                    List<RoadMap> roadmaps = populatorDB.createRoadmaps();

                    // 2. Pintamos la cabecera de la tabla perfectamente formateada
                    System.out.printf("%-20s %-35s %5s %9s%n", "NAME", "DESCRIPTION", "STEPS", "DONE");
                    System.out.println("-".repeat(72));

                    // 3. Procesamos y ordenamos con Streams (de mayor a menor pasos)
                    roadmaps.stream()
                            .sorted(java.util.Comparator.comparingInt(com.example.demo.model.RoadMap::getSteps).reversed())
                            .forEach(r -> System.out.printf("%-20s %-35s %5d %9s%n",
                                    r.getName(), r.getDescription(), r.getSteps(),
                                    r.isCompleted() ? "✓" : "✗"));

                    // 4. Filtramos las pendientes y mostramos el conteo final
                    long pending = roadmaps.stream().filter(r -> !r.isCompleted()).count();
                    System.out.println("\nPending roadmaps: " + pending);
                    break;
                case "3":
                    //System.out.println("Profile - not implemented yet.");
                    // String profile = new QuestionnaireRunner();

                    // 2
                    /*
                    QuestionnaireRunner runner = new QuestionnaireRunner();
                    String profile = runner.runQuestionnaire(scan);
                    System.out.println("Your profile is: " + profile);
                    */

                    // 3
                    /*
                    // 1. Ejecutamos el cuestionario de la Práctica 1
                    QuestionnaireRunner runner = new QuestionnaireRunner();
                    String result = runner.runQuestionnaire(scan);

                    // 2. Creamos el diccionario (HashMap)
                    //java.util.HashMap<String, com.example.demo.model.ProfileType> profileMap = new java.util.HashMap<>();
                    HashMap<String, ProfileType> profileMap = new HashMap<>();
                    */
                    // 3. Llenamos el diccionario asociando Texto -> Enumerado
                        /*
                        profileMap.put("Architect", com.example.demo.model.ProfileType.ARCHITECT);
                        profileMap.put("AI Practitioner", com.example.demo.model.ProfileType.AI_PRACTITIONER);
                        profileMap.put("Beginner", com.example.demo.model.ProfileType.BEGINNER);
                        profileMap.put("Non-Developer", com.example.demo.model.ProfileType.NON_DEVELOPER);
                        */
                    /*
                    profileMap.put("Architect", ProfileType.ARCHITECT);
                    profileMap.put("AI Practitioner", ProfileType.AI_PRACTITIONER);
                    profileMap.put("Beginner", ProfileType.BEGINNER);
                    profileMap.put("Non-Developer", ProfileType.NON_DEVELOPER);

                    // 4. Buscamos en el diccionario usando el resultado del cuestionario
                    //com.example.demo.model.ProfileType profile = profileMap.get(result);
                    ProfileType profile = profileMap.get(result);

                    // 5. Pintamos la ficha bonita en la consola
                    if (profile != null) {
                        profile.printCard();
                    } else {
                        System.out.println("Error: Profile not found.");
                    }
                    break;

                     */
                    // 5
                    /*
                    // 1. Ejecutamos la nueva calculadora numérica de la Práctica 5
                    QuestionnaireRunner runner = new QuestionnaireRunner();
                    ProfileType profile = runner.calculateProfile(scan);

                    // 2. Pintamos la ficha bonita directamente en la consola
                    if (profile != null) {
                        profile.printCard();
                    } else {
                        System.out.println("Error: Profile could not be calculated.");
                    }
                    break;
                    */

                    // 1. Ejecutamos la calculadora numérica del perfil
                    QuestionnaireRunner runner = new QuestionnaireRunner();
                    com.example.demo.model.ProfileType profile = runner.calculateProfile(scan);

                    // 2. Pintamos la ficha bonita en la consola
                    if (profile != null) {
                        profile.printCard();

                        // 3. Pedimos el nombre del cliente para el informe real
                        System.out.print("\nEnter your name for the report: ");
                        String customerName = scan.nextLine();

                        // 4. Generamos y guardamos el archivo de texto en el disco
                        runner.saveProfileReport(customerName, profile);
                    } else {
                        System.out.println("Error: Profile could not be calculated.");
                    }

                    System.out.println("\nReturning to main menu...");
                    break;
                case "4":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }
    /*
    public static void customerLoop(Scanner scan, PopulatorDB populatorDB, CustomerService customerService) {
        boolean inCustomerMenu = true;
        while (inCustomerMenu) {
            customerMenu();
            String custOption = scan.nextLine();
            switch (custOption) {
                case "1":
                    System.out.print("How many customers? ");
                    int count = Integer.parseInt(scan.nextLine());
                    populatorDB.createAndSaveCustomer(count);
                    System.out.println(count + " customers created and saved.");
                    break;
                case "2":
                    System.out.print("Customer ID to delete: ");
                    String id = scan.nextLine();
                    customerService.deleteCustomer(id);
                    System.out.println("Customer " + id + " deleted.");
                    break;
                case "3":
                    customerService.deleteAllCustomers();
                    System.out.println("All customers deleted.");
                    break;
                case "4":
                    long total = customerService.countCustomers();
                    System.out.println("Total customers: " + total);
                    break;
                case "5":
                    inCustomerMenu = false;
                    break;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }
    */

    /*
    public static void customerSubMenu(Scanner scan, PopulatorDB populatorDB, CustomerService customerService) {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("a. Populate fake customers");
            System.out.println("b. List all customers");
            System.out.println("c. Find customer by ID");
            System.out.println("d. Delete customer by ID");
            System.out.println("e. Back");
            System.out.print("Choose: ");

            String opt = scan.nextLine().trim().toLowerCase();
            switch (opt) {
                case "a":
                    System.out.print("How many? ");
                    try {
                        int count = Integer.parseInt(scan.nextLine());
                        populatorDB.createAndSaveCustomer(count);
                        System.out.println(count + " customers created.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                    break;
                case "b":
                    java.util.List<com.example.demo.model.Customer> all = new java.util.ArrayList<>();
                    customerService.findAll().forEach(all::add);
                    if (all.isEmpty()) {
                        System.out.println("No customers found.");
                    } else {
                        all.forEach(c -> System.out.println("  " + c));
                    }
                    break;
                case "c":
                    System.out.print("Enter ID: ");
                    String id = scan.nextLine();
                    try {
                        com.example.demo.model.Customer found = customerService.getCustomerById(id);
                        System.out.println("Found: " + found);
                    } catch (java.util.NoSuchElementException e) {
                        System.out.println("Customer not found with ID: " + id);
                    }
                    break;
                case "d":
                    System.out.print("Enter ID to delete: ");
                    customerService.deleteCustomer(scan.nextLine());
                    System.out.println("Deleted.");
                    break;
                case "e":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    */

    public static void customerSubMenu(Scanner scan, PopulatorDB populatorDB, CustomerService customerService) {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("a. Populate fake customers");
            System.out.println("b. List all customers");
            System.out.println("c. Find customer by ID");
            System.out.println("d. Delete customer by ID");
            System.out.println("e. Back");
            System.out.print("Choose: ");

            String opt = scan.nextLine().trim().toLowerCase();
            switch (opt) {
                case "a":
                    System.out.print("How many? ");
                    try {
                        int count = Integer.parseInt(scan.nextLine());
                        populatorDB.createAndSaveCustomer(count);
                        System.out.println(count + " customers created.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                    }
                    break;
                case "b":
                    List<Customer> all = new ArrayList<>();
                    customerService.findAll().forEach(all::add);
                    if (all.isEmpty()) {
                        System.out.println("No customers found.");
                    } else {
                        all.forEach(c -> System.out.println("  " + c));
                    }
                    break;
                case "c":
                    System.out.print("Enter ID: ");
                    String id = scan.nextLine();
                    try {
                        Customer found = customerService.getCustomerById(id);
                        System.out.println("Found: " + found);
                    } catch (java.util.NoSuchElementException e) {
                        System.out.println("Customer not found with ID: " + id);
                    }
                    break;
                case "d":
                    System.out.print("Enter ID to delete: ");
                    customerService.deleteCustomer(scan.nextLine());
                    System.out.println("Deleted.");
                    break;
                case "e":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }


    public static void roadMapLoop(Scanner scan){
        while(true){
            String option = askMenuOption(scan);
            switch (option){
            }
        }
    }

    public static void customerMenu() {
        System.out.println("\n===== CUSTOMER MENU =====");
        System.out.println("1. Create customers");
        System.out.println("2. Delete customer");
        System.out.println("3. Delete all customers");
        System.out.println("4. Count customers");
        System.out.println("5. Quit");
    }

    public static void mainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Customer");
        System.out.println("2. Roadmap");
        System.out.println("3. Profile");
        System.out.println("4. Quit");

    }

    public static String askMenuOption(Scanner scan){
        System.out.print("Select an option: ");
        String option = scan.nextLine();
        return option;
    }


}
