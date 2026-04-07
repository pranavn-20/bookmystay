import java.util.HashMap;

// Abstract class
abstract class Hotel {
    String name;
    String location;

    Hotel(String name, String location) {
        this.name = name;
        this.location = location;
    }

    abstract void showDetails();
}

// Child class
class LuxuryHotel extends Hotel {

    LuxuryHotel(String name, String location) {
        super(name, location);
    }

    @Override
    void showDetails() {
        System.out.println("Luxury Hotel: " + name + ", Location: " + location);
    }
}

// New class for centralized inventory management
class RoomInventory {

    private HashMap<String, Integer> inventory;

    // Initialize inventory
    RoomInventory() {
        inventory = new HashMap<>();
    }

    // Register room type
    void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    // Update availability
    void updateRoom(String type, int count) {
        if (inventory.containsKey(type)) {
            inventory.put(type, count);
        } else {
            System.out.println("Room type not found.");
        }
    }

    // Get availability
    int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    // Display all inventory
    void showInventory() {
        System.out.println("Current Room Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// Main class
public class bookmystay {

    /**
     * Goal:
     * Introduce object modeling through inheritance and abstraction.
     *
     * Goal:
     * Introduce centralized inventory management using HashMap.
     */

    public static void main(String[] args) {

        System.out.println("Welcome to bookmystay ver:1.00");

        // OOP demo
        Hotel hotel = new LuxuryHotel("Taj Palace", "Chennai");
        hotel.showDetails();

        // Inventory system demo
        RoomInventory inventory = new RoomInventory();

        inventory.addRoomType("Deluxe", 10);
        inventory.addRoomType("Suite", 5);

        inventory.showInventory();

        inventory.updateRoom("Deluxe", 8);

        System.out.println("Updated Availability (Deluxe): " +
                inventory.getAvailability("Deluxe"));

        inventory.showInventory();
    }
}