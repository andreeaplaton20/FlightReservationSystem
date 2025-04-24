package org.example.flightreservationsystem;

import org.example.flightreservationsystem.model.*;
import org.example.flightreservationsystem.repository.*;
import org.example.flightreservationsystem.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.CompositeUriComponentsContributor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

@SpringBootApplication
public class FlightReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightReservationSystemApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(AirlineRepository airlineRepository,
                               AirportRepository airportRepository,
                               AirplaneRepository airplaneRepository,
                               FlightRepository flightRepository, CompositeUriComponentsContributor compositeUriComponentsContributor, UserRepository userRepository) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            User currentUser = null;
            boolean authenticated = false;

            while (running) {
                System.out.println("=============MENIU PRINCIPAL=============");
                System.out.println("1. Logare in cont");
                System.out.println("2. Cumpara bilet");
                System.out.println("0. Iesire");
                System.out.println("Alege o optiune:");
                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        System.out.println("Logare in cont:");
                        currentUser = handleLoginOrRegister(scanner, userRepository);
                        break;
                    case  "2":
                        if (currentUser == null) {
                            System.out.println("Trebuie sa te loghezi inainte de a cumpara un bilet.");
                            currentUser = handleLoginOrRegister(scanner, userRepository);
                        }
                        if (currentUser != null && currentUser.getRole().equals("user")) {
                            buyTicket(scanner, flightRepository);
                        } else {
                            System.out.println("Trebuie sa ai rol de utilizator pentru a cumpara bilete.");
                        }
                        break;
                    case "0":
                        System.out.println("Iesire din aplicatie.");
                        running = false;
                        break;
                    default:
                        System.out.println("Optiune invalida. Te rog sa alegi din nou.");
                }

                while(currentUser != null && running) {
                    if (currentUser.getRole().equals("admin") && currentUser.getUsername().equals("andreea20")) {
                        System.out.println("=============MENIU ADMIN=============");
                        System.out.println("1. Adauga zbor");
                        System.out.println("2. Vizualizeaza zboruri");
                        System.out.println("0. Logout");
                        System.out.println("Alege o optiune:");
                        String adminOption = scanner.nextLine();

                        switch (adminOption) {
                            case "1":
                                addFlight(scanner, airlineRepository, airportRepository, airplaneRepository, flightRepository);
                                break;
                            case "2":
                                flightRepository.findAll().forEach(System.out::println);
                                break;
                            case "0":
                                currentUser = null;
                                break;
                            default:
                                System.out.println("Optiune invalida. Te rog sa alegi din nou.");
                        }
                    } else if (currentUser.getRole().equals("user")) {
                        System.out.println("=============MENIU UTILIZATOR=============");
                        System.out.println("1. Cumpara bilet");
                        System.out.println("0. Logout");
                        System.out.println("Alege o optiune:");
                        String userOption = scanner.nextLine();

                        switch (userOption) {
                            case "1":
                                buyTicket(scanner, flightRepository);
                                break;
                            case "0":
                                currentUser = null;
                                break;
                            default:
                                System.out.println("Optiune invalida. Te rog sa alegi din nou.");
                        }
                    }
                }
            }
        };
    }

    private User handleLoginOrRegister(Scanner scanner, UserRepository userRepository) {
        UserService userService = new UserService();
        userService.setUserRepository(userRepository);
        System.out.println("Alegeti o optiune:");
        System.out.println("1. Logare");
        System.out.println("2. Inregistrare(Creare cont)");
        String option = scanner.nextLine();

        if (option.equals("1")) {
            System.out.print("Introduceti username: ");
            String username = scanner.nextLine();
            System.out.print("Introduceti parola: ");
            String password = scanner.nextLine();

            if (userService.authenticate(username, password)) {
                System.out.println("Logare reusita!");
                return userRepository.findByUsername(username).orElse(null);
            } else {
                System.out.println("Username sau parola gresite.");
                return null;
            }
        } else if (option.equals("2")) {
            System.out.print("Introduceti username: ");
            String username = scanner.nextLine();
            System.out.print("Introduceti email: ");
            String email = scanner.nextLine();
            System.out.print("Introduceti parola: ");
            String password = scanner.nextLine();

            String role;
            while (true) {
                System.out.print("Rol (admin/user): ");
                role = scanner.nextLine();
                if (role.equals("admin") || role.equals("user")) break;
                System.out.println("Rol invalid. Scrie 'admin' sau 'user'.");
            }

            User user = userService.register(username, email, password, role);
            System.out.println("Cont creat cu succes!");
            return user;
        } else {
            System.out.println("Optiune invalida.");
            return null;
        }
    }

    private void addFlight(Scanner scanner, AirlineRepository airlineRepository, AirportRepository airportRepository, AirplaneRepository airplaneRepository, FlightRepository flightRepository) {
        System.out.println("Introduceti numele liniei aeriene:");
        String airlineName = scanner.nextLine();
        System.out.println("Introduceti codul liniei aeriene:");
        String airlineCode = scanner.nextLine();
        System.out.println("Introduceti tara liniei aeriene:");
        String airlineCountry = scanner.nextLine();
        Airline airline = airlineRepository.save(new Airline(airlineName, airlineCode, airlineCountry));

        System.out.println("Introduceti numele aeroportului de plecare:");
        String departureName = scanner.nextLine();
        System.out.println("Introduceti codul aeroportului de plecare:");
        String departureCode = scanner.nextLine();
        System.out.println("Introduceti orasul de plecare:");
        String departureCity = scanner.nextLine();
        System.out.println("Introduceti tara de plecare:");
        String departureCountry = scanner.nextLine();
        Airport departureAirport = airportRepository.save(new Airport(departureName, departureCode, departureCity, departureCountry));

        System.out.println("Introduceti numele aeroportului de sosire:");
        String arrivalName = scanner.nextLine();
        System.out.println("Introduceti codul aeroportului de sosire:");
        String arrivalCode = scanner.nextLine();
        System.out.println("Introduceti orasul de sosire:");
        String arrivalCity = scanner.nextLine();
        System.out.println("Introduceti tara de sosire:");
        String arrivalCountry = scanner.nextLine();
        Airport arrivalAirport = airportRepository.save(new Airport(arrivalName, arrivalCode, arrivalCity, arrivalCountry));

        System.out.println("Introduceti modelul avionului:");
        String airplaneModel = scanner.nextLine();
        System.out.println("Introduceti capacitatea avionului:");
        int airplaneCapacity = Integer.parseInt(scanner.nextLine());
        Airplane airplane = airplaneRepository.save(new Airplane(airplaneModel, airplaneCapacity, airline));

        System.out.println("Cate zboruri vrei sa adaugi?");
        int numberOfFlights = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < numberOfFlights; i++) {
            System.out.println("Introduceti numarul zborului:");
            int flightNumber = Integer.parseInt(scanner.nextLine());

            System.out.println("Introduceti data si ora plecarii (yyyy-MM-dd HH:mm):");
            LocalDateTime departureTime = LocalDateTime.parse(scanner.nextLine().replace(" ", "T"));

            System.out.println("Introduceti data si ora sosirii (yyyy-MM-dd HH:mm):");
            LocalDateTime arrivalTime = LocalDateTime.parse(scanner.nextLine().replace(" ", "T"));

            System.out.println("Introduceti numarul de locuri disponibile:");
            int availableSeats = Integer.parseInt(scanner.nextLine());

            System.out.println("Introduceti pretul biletului:");
            BigDecimal price = new BigDecimal(scanner.nextLine());

            Flight flight = new Flight(flightNumber, departureAirport, arrivalAirport, departureTime, arrivalTime, availableSeats, price);
            flight.setAirplane(airplane);
            flightRepository.save(flight);

            System.out.println("Zborul a fost adaugat cu succes!");
        }

        System.out.println("Toate zborurile au fost salvate.");
    }

    private void buyTicket(Scanner scanner, FlightRepository flightRepository) {
        System.out.println("Zboruri disponibile:\n");
        flightRepository.findAll().forEach(flight -> {
            System.out.println("ID: " + flight.getId() +
                    " | Zbor " + flight.getFlightNumber() +
                    " | De la " + flight.getDepartureAirport().getCity() +
                    " la " + flight.getArrivalAirport().getCity() +
                    " | Data plecare: " + flight.getDepartureTime() +
                    " | Pret: " + flight.getPrice() +
                    " | Locuri disponibile: " + flight.getAvailableSeats());
        });

        System.out.print("\nIntrodu ID-ul zborului dorit: ");
        Long flightId = Long.parseLong(scanner.nextLine());

        flightRepository.findById(flightId).ifPresentOrElse(flight -> {
            System.out.print("Cate bilete doresti sa cumperi? ");
            int ticketCount = Integer.parseInt(scanner.nextLine());

            if (flight.getAvailableSeats() >= ticketCount) {
                flight.setAvailableSeats(flight.getAvailableSeats() - ticketCount);
                flightRepository.save(flight);
                System.out.println("Ai cumparat cu succes " + ticketCount + " bilet(e) pentru zborul " + flight.getFlightNumber() + "!");
            } else {
                System.out.println("Nu sunt suficiente locuri disponibile. Mai sunt doar " + flight.getAvailableSeats() + " locuri.");
            }
        }, () -> System.out.println("Zborul nu a fost gasit."));
    }
}

