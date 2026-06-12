package chatapppoepart1;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.json.JSONObject;

public class AssignmentPOEPart1 {

    static final String FILE_PATH = "user_database.txt";  
    static final String MESSAGE_FILE = "messages.json";  
    static boolean loggedIn = false;  
    static String loggedInUser = "";  

    // ================= : GLOBAL COLLECTIONS/ARRAYS =================  
    static ArrayList<Message> sentMessagesArray = new ArrayList<>();  
    static ArrayList<Message> disregardedMessagesArray = new ArrayList<>();  
    static ArrayList<Message> storedMessagesArray = new ArrayList<>();  
    static ArrayList<String> messageHashArray = new ArrayList<>();  
    static ArrayList<String> messageIdArray = new ArrayList<>();  

    public static void main(String[] args) {  
        // Load existing stored messages from JSON into our arrays right at startup
        loadStoredMessagesFromJSON();

        Scanner scanner = new Scanner(System.in);  

        while (true) {  
            System.out.println("\n===== MENU =====");  
            System.out.println("1. Register");  
            System.out.println("2. Login");  
            System.out.println("3. Exit");  
            System.out.print("Choose option: ");  

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
                continue;
            }
            int choice = scanner.nextInt();  
            scanner.nextLine();  

            switch (choice) {  
                case 1 -> registerUser(scanner);
                case 2 -> {
                    loginUser(scanner);
                    if (loggedIn) {
                        loggedInUserMenu(scanner);
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

    // Updated menu handling to accommodate the new standalone Stored Messages hub
    private static void loggedInUserMenu(Scanner scanner) {
        while (loggedIn) {
            System.out.println("\n===== USER MENU =====");  
            System.out.println("1. QuickChat (Send Messages)");  
            System.out.println("2. Stored Messages ");  
            System.out.println("3. Exit");  
            System.out.print("Choose option: ");  

            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                continue;
            }
            int choice = scanner.nextInt();  
            scanner.nextLine();  

            switch (choice) {
                case 1 -> quickChatMenu(scanner);
                case 2 -> storedMessagesMenu(scanner);
                case 3 -> {
                    System.out.println("Logged out successfully.");
                    loggedIn = false;
                    loggedInUser = "";
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void quickChatMenu(Scanner scanner) {  
        System.out.println("\nWelcome to QuickChat.");  

        System.out.print("How many messages do you wish to send? ");  
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid amount.");
            scanner.nextLine();
            return;
        }
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

        System.out.println("\nTotal messages sent this session: " + sentMessagesArray.size());  
    }  

    // ================= : STORED MESSAGES SUBMENU =================  
    private static void storedMessagesMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n===== STORED MESSAGES DASHBOARD =====");
            System.out.println("1. Display sender and recipient of all stored messages");
            System.out.println("2. Display the longest stored message");
            System.out.println("3. Search for a Message ID");
            System.out.println("4. Search for all messages by recipient");
            System.out.println("5. Delete a message using Message Hash");
            System.out.println("6. Display full report of all stored messages");
            System.out.println("7. Return to main dashboard");
            System.out.print("Choose option: ");

            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                continue;
            }
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    displaySendersAndRecipients();
                    break;
                case 2:
                    displayLongestMessage();
                    break;
                case 3:
                    System.out.print("Enter Message ID to search: ");
                    searchByMessageId(scanner.nextLine().trim());
                    break;
                case 4:
                    System.out.print("Enter Recipient cell number (+27...): ");
                    searchByRecipient(scanner.nextLine().trim());
                    break;
                case 5:
                    System.out.print("Enter Message Hash to delete: ");
                    deleteByHash(scanner.nextLine().trim());
                    break;
                case 6:
                    displayFullReport();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // --- SUBMENU IMPLEMENTATION LOGIC ---

    // 2.a Display sender and recipient of all stored messages
    private static void displaySendersAndRecipients() {
        System.out.println("\n--- Stored Messages Contacts ---");
        if (storedMessagesArray.isEmpty()) {
            System.out.println("No stored messages available.");
            return;
        }
        for (Message msg : storedMessagesArray) {
            System.out.println("Sender: " + loggedInUser + " -> Recipient: " + msg.getRecipient());
        }
    }

    // 2.b Display the longest stored message
    private static void displayLongestMessage() {
        System.out.println("\n--- Longest Stored Message ---");
        if (storedMessagesArray.isEmpty()) {
            System.out.println("No stored messages available.");
            return;
        }
        Message longest = storedMessagesArray.get(0);
        for (Message msg : storedMessagesArray) {
            if (msg.getMessageText().length() > longest.getMessageText().length()) {
                longest = msg;
            }
        }
        System.out.println("The system returns: \"" + longest.getMessageText() + "\"");
    }

    // 2.c Search for a message ID and display the corresponding recipient and message
    private static void searchByMessageId(String id) {
        System.out.println("\n--- Message ID Search Result ---");
        int index = messageIdArray.indexOf(id);
        if (index != -1 && index < storedMessagesArray.size()) {
            Message msg = storedMessagesArray.get(index);
            System.out.println("Recipient: " + msg.getRecipient());
            System.out.println("The system returns: \"" + msg.getMessageText() + "\"");
        } else {
            System.out.println("Message ID not found among stored items.");
        }
    }

    // 2.d Search for all the messages stored for a particular recipient
    private static void searchByRecipient(String targetRecipient) {
        System.out.println("\n--- Search Results for Recipient: " + targetRecipient + " ---");
        boolean found = false;
        
        // Output format matching your precise assignment test criteria expectation
        for (Message msg : storedMessagesArray) {
            if (msg.getRecipient().equals(targetRecipient)) {
                System.out.println("The system returns: \"" + msg.getMessageText() + "\"");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No stored messages found matching that recipient cell.");
        }
    }

    // 2.e Delete a message using the message hash
    private static void deleteByHash(String hash) {
        System.out.println("\n--- Delete Message via Hash ---");
        int targetIndex = -1;
        for (int i = 0; i < storedMessagesArray.size(); i++) {
            if (storedMessagesArray.get(i).getMessageHash().equalsIgnoreCase(hash)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex != -1) {
            Message removedMsg = storedMessagesArray.remove(targetIndex);
            messageHashArray.remove(targetIndex);
            messageIdArray.remove(targetIndex);
            
            // Rewrite updated array back to JSON storage file
            rewriteJsonStorageFile();
            System.out.println("Message: \"" + removedMsg.getMessageText() + "\" successfully deleted.");
        } else {
            System.out.println("Message hash not found.");
        }
    }

    // 2.f Display a report that lists the full details of all stored messages
    private static void displayFullReport() {
        System.out.println("\n--- Full Stored Messages Report ---");
        if (storedMessagesArray.isEmpty()) {
            System.out.println("No stored messages recorded.");
            return;
        }
        for (Message msg : storedMessagesArray) {
            System.out.println("------------------------------------");
            System.out.println(msg.printMessages());
        }
        System.out.println("------------------------------------");
    }

    // ================= : READ JSON FILE INTO ARRAY =================  
    private static void loadStoredMessagesFromJSON() {
        try {
            File file = new File(MESSAGE_FILE);
            if (!file.exists() || file.length() == 0) return;

            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JSONArray arr = new JSONArray(content);

            storedMessagesArray.clear();
            messageHashArray.clear();
            messageIdArray.clear();

            for (int i = 0; i < arr.length(); i++) {
                Object obj = arr.getJSONObject(i);
                
                // Reconstruct the message object cleanly
                Message msg = new Message(
                    obj.getString("messageID"),
                    obj.getString("recipient"),
                    obj.getString("message"),
                    obj.getString("messageHash")
                );
                
                storedMessagesArray.add(msg);
                messageHashArray.add(msg.getMessageHash());
                messageIdArray.add(msg.getMessageID());
            }
        } catch (IOException e) {
            System.out.println("Notice: Could not sync initial stored messages array: " + e.getMessage());
        }
    }

    private static void rewriteJsonStorageFile() {
        try {
            JSONArray arr = new JSONArray();
            for (int i = 0; i < storedMessagesArray.size(); i++) {
                Message msg = storedMessagesArray.get(i);
                JSONObject obj = new JSONObject();
                obj.put("messageID", msg.getMessageID());
                obj.put("messageHash", msg.getMessageHash());
                obj.put("recipient", msg.getRecipient());
                obj.put("message", msg.getMessageText());
                obj.put("messageNumber", (i + 1));
                arr.put(obj);
            }
            try (FileWriter writer = new FileWriter(MESSAGE_FILE)) {
                writer.write(arr.toString(4));
            }
        } catch (IOException e) {
            System.out.println("Error syncing changes to disk: " + e.getMessage());
        }
    }

    // ================= REGISTRATION & USER SESSIONS =================  
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
            loggedInUser = username;  
        } else {  
            System.out.println("❌ Invalid login details.");  
            loggedIn = false;  
        }  
    }  

    private static boolean validateName(String name) {  
        return name.matches("[a-zA-Z]{1,5}_.*");  
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
        File file = new File(FILE_PATH);
        if (!file.exists()) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {  
            String line;  
            while ((line = reader.readLine()) != null) {  
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

    // ================= REFACTORED MESSAGE CLASS =================  
    static final class Message {  
        private String messageID;  
        private String recipient;  
        private String messageText;  
        private String messageHash;  

        // Main constructor for fresh interface processing
        public Message(String recipient, String messageText) {  
            this.messageID = generateMessageID();  
            this.recipient = recipient;  
            this.messageText = messageText;  
            this.messageHash = createMessageHash();  
        }  

        // Overloaded constructor specifically for parsing existing records from files/JSON
        public Message(String messageID, String recipient, String messageText, String messageHash) {
            this.messageID = messageID;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageHash = messageHash;
        }

        public String getMessageID() { return messageID; }
        public String getRecipient() { return recipient; }
        public String getMessageText() { return messageText; }
        public String getMessageHash() { return messageHash; }

        private String generateMessageID() {  
            long num = ThreadLocalRandom.current().nextLong(100000L, 10000000L);  
            return String.valueOf(num);  
        }  

        public String checkRecipientCell() {  
            if (recipient.matches("\\+27\\d{9}")) {  
                return "Cell phone number successfully captured";  
            } else {  
                return "Cell phone number is incorrectly formatted or does not contain an international code";  
            }
        }

        public String createMessageHash() {  
            String firstTwo = (messageID.length() >= 2) ? messageID.substring(0, 2) : "00";  
            String trimmed = messageText.trim();
            String[] words = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");  
            
            String firstWord = words.length > 0 ? words[0].toUpperCase() : "MSG";  
            String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : firstWord;  
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
                    sentMessagesArray.add(this);
                    return "Message successfully sent";
                }
                case 2 -> {
                    disregardedMessagesArray.add(this);
                    return "Message disregarded.";
                }
                case 3 -> {
                    storedMessagesArray.add(this);
                    messageHashArray.add(this.messageHash);
                    messageIdArray.add(this.messageID);
                    saveSingleMessageToJSON();
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

        private void saveSingleMessageToJSON() {  
            try {  
                JSONArray arr = new JSONArray();  
                File file = new File(MESSAGE_FILE);  
                if (file.exists() && file.length() > 0) {  
                    String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));  
                    arr = new JSONArray(content);  
                }  

                JSONObject obj = new JSONObject();  
                obj.put("messageID", messageID);  
                obj.put("messageHash", messageHash);  
                obj.put("recipient", recipient);  
                obj.put("message", messageText);  
                obj.put("messageNumber", arr.length() + 1);  

                arr.put(obj);  

                try (FileWriter writer = new FileWriter(MESSAGE_FILE)) {  
                    writer.write(arr.toString(4));  
                }  
            } catch (IOException e) {  
                System.out.println("Error writing storage update: " + e.getMessage());  
            }  
        }  
    }
}