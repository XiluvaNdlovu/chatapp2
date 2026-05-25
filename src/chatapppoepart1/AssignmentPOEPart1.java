package chatapppoepart1;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AssignmentPOEPart1 {

    static final String FILE_PATH = "user_database.txt";
    static final String MESSAGE_FILE = "messages.json";
    static ArrayList<Message> sentMessages = new ArrayList<>();
    static int totalMessagesSent = 0;
    static boolean loggedIn = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registerUser(scanner);
                case 2 -> {
                    loginUser(scanner);
                    if (loggedIn) {
                        quickChatMenu(scanner);
                    }
                }
                case 3 -> {
                    System.out.println("Goodbye 👋");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ================= QUICKCHAT MENU =================
    private static void quickChatMenu(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");

        System.out.print("How many messages do you wish to send? ");
        int numMessages = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");
            
            System.out.print("Enter recipient cell number (+27XXXXXXXXX): ");
            String recipient = scanner.nextLine();
            
            System.out.print("Enter message (max 250 chars): ");
            String messageText = scanner.nextLine();

            Message msg = new Message(recipient, messageText);
            
            String result = msg.sentMessage(scanner);
            System.out.println(result);

            if (result.equals("Message successfully sent") || result.equals("Message successfully stored")) {
                System.out.println("\n--- Message Details ---");
                System.out.println(msg.printMessages());
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    // ================= REGISTER =================
    private static void registerUser(Scanner scanner) {
        System.out.println("\n--- REGISTER ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        if (!validateName(username)) {
            System.out.println("Invalid Username! Must contain underscore and max 5 characters before it.");
            return;
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (!validatePassword(password)) {
            System.out.println("Invalid Password! Must be 8+ chars, include capital, number, special character.");
            return;
        }

        System.out.print("Enter SA Cellphone (+27...): ");
        String cellphone = scanner.nextLine();

        if (!validateCellphone(cellphone)) {
            System.out.println("Invalid Cellphone Number!");
            return;
        }

        if (saveUser(username, password, cellphone)) {
            System.out.println("✅ Registration Successful!");
        } else {
            System.out.println("❌ Error saving user.");
        }
    }

    // ================= LOGIN =================
    private static void loginUser(Scanner scanner) {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (checkUser(username, password)) {
            System.out.println("✅ Login Successful!");
            System.out.println("Welcome " + username + " 🎉");
            loggedIn = true;
        } else {
            System.out.println("❌ Invalid login details.");
            loggedIn = false;
        }
    }

    // ================= VALIDATION =================
    private static boolean validateName(String name) {
        return name.matches("[a-zA-Z]{1,5}_");
    }

    private static boolean validatePassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()\\-_=+<>?/].*");
    }

    private static boolean validateCellphone(String cellphone) {
        return cellphone.matches("\\+27\\d{9}");
    }

    // ================= FILE HANDLING =================
    private static boolean saveUser(String username, String password, String cellphone) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + password + "," + cellphone);
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean checkUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine())!= null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading database.");
        }
        return false;
    }

    // ================= MESSAGE CLASS =================
    static class Message {
        private final String messageID;
        private final String recipient;
        private final String messageText;
        private final String messageHash;
        private final int messageNumber;

        public Message(String recipient, String messageText) {
            this.messageID = generateMessageID();
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageHash = createMessageHash();
            this.messageNumber = sentMessages.size() + 1;
        }

        private String generateMessageID() {
            long num;
            num = ThreadLocalRandom.current().nextLong(1000000L, 9999999999L);
            return String.valueOf(num);
        }

        public boolean checkMessageID() {
            return messageID.length() <= 10;
        }

        public String checkRecipientCell() {
            if (recipient.matches("\\+27\\d{9}")) {
                return "Cell phone number successfully captured";
            } else {
                return "Cell phone number is incorrectly formatted or does not contain an international code";
            }
        }

        public final String createMessageHash() {
            String firstTwo = messageID.substring(0, 2);
            String[] words = messageText.trim().split("\\s+");
            String firstWord = words.length > 0? words[0].toUpperCase() : "MSG";
            String lastWord = words.length > 1? words[words.length - 1].toUpperCase() : firstWord;
            return firstTwo + ":" + firstWord + ":" + lastWord;
        }

        public String sentMessage(Scanner scanner) {
            if (messageText.length() > 250) {
                return "Please enter a message of less than 250 characters.";
            }

            if (!checkRecipientCell().contains("successfully")) {
                return checkRecipientCell();
            }

            System.out.println("\n1. Send Message");
            System.out.println("2. Disregard Message");
            System.out.println("3. Store Message to send later");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    sentMessages.add(this);
                    totalMessagesSent++;
                    storeMessage();
                    return "Message successfully sent";
                }
                case 2 -> {
                    return "Press 0 to delete the message";
                }
                case 3 -> {
                    storeMessage();
                    return "Message successfully stored";
                }
                default -> {
                    return "Invalid option";
                }
            }
        }

        public String printMessages() {
            return "Message ID: " + messageID + "\n" +
                   "Message Hash: " + messageHash + "\n" +
                   "Recipient: " + recipient + "\n" +
                   "Message: " + messageText;
        }

        public static int returnTotalMessages() {
            return totalMessagesSent;
        }

        private void storeMessage() {
            try {
                JSONArray arr = new JSONArray();
                File file = new File(MESSAGE_FILE);
                if (file.exists()) {
                    String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                    if (!content.isEmpty()) {
                        arr = new JSONArray(content);
                    }
                }

                JSONObject obj = new JSONObject();
                obj.put("messageID", messageID);
                obj.put("messageHash", messageHash);
                obj.put("recipient", recipient);
                obj.put("message", messageText);
                obj.put("messageNumber", messageNumber);

                arr.put(obj);

                try (FileWriter writer = new FileWriter(MESSAGE_FILE)) {
                    writer.write(arr.toString(4));
                }
            } catch (IOException e) {
                System.out.println("Error storing message: " + e.getMessage());
            }
        }
    }
}