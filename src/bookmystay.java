import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

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

// Room class (holds details)
class Room {
    String type;
    double price;

    Room(String type, double price) {
        this.type = type;
        this.price = price;
    }

    void showRoom() {
        System.out.println("Room Type: " + type + ", Price: " + price);
    }
}

// Inventory class
class RoomInventory {

    private HashMap<String, Integer> inventory;

    RoomInventory() {
        inventory = new HashMap<>();
    }

    void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    void updateRoom(String type, int count) {
        if (inventory.containsKey(type)) {
            inventory.put(type, count);
        }
    }

    int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    HashMap<String, Integer> getAllInventory() {
        return inventory; // read access (used by search)
    }
}

// Search Service (read-only)
class SearchService {

    void searchAvailableRooms(RoomInventory inventory, List<Room> rooms) {
        System.out.println("\nAvailable Rooms:");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.type);

            if (available > 0) { // filter unavailable rooms
                room.showRoom();
                System.out.println("Available Count: " + available);
            }
        }
    }
}

// Main class
public class bookmystay {

    /**
     * Goal:
     * Abstraction + Inheritance
     *
     * Goal:
     * Centralized inventory using HashMap
     *
     * Goal:
     * Enable read-only search without modifying system state
     */

    public static void main(String[] args) {

        System.out.println("Welcome to bookmystay ver:1.00");

        // Hotel demo
        Hotel hotel = new LuxuryHotel("Taj Palace", "Chennai");
        hotel.showDetails();

        // Inventory setup
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Deluxe", 10);
        inventory.addRoomType("Suite", 0); // unavailable

        // Room details
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("Deluxe", 3000));
        rooms.add(new Room("Suite", 5000));

        // Search (read-only)
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, rooms);
    }
}