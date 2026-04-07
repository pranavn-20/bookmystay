import java.util.*;

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

// Room class
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

    int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    HashMap<String, Integer> getAllInventory() {
        return inventory;
    }
}

// Search Service (read-only)
class SearchService {

    void searchAvailableRooms(RoomInventory inventory, List<Room> rooms) {
        System.out.println("\nAvailable Rooms:");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.type);

            if (available > 0) {
                room.showRoom();
                System.out.println("Available Count: " + available);
            }
        }
    }
}

// Reservation (booking request)
class Reservation {
    String guestName;
    String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    void showRequest() {
        System.out.println("Guest: " + guestName + " requested " + roomType);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {

    private Queue<Reservation> queue;

    BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add request
    void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added to queue for " + reservation.guestName);
    }

    // View all queued requests
    void showQueue() {
        System.out.println("\nCurrent Booking Queue:");

        for (Reservation r : queue) {
            r.showRequest();
        }
    }
}

// Main class
public class bookmystay {

    /**
     * Goals covered:
     * - Abstraction & Inheritance
     * - HashMap for inventory
     * - Read-only search service
     * - Queue-based booking request handling (FIFO)
     */

    public static void main(String[] args) {

        System.out.println("Welcome to bookmystay ver:1.00");

        // Hotel demo
        Hotel hotel = new LuxuryHotel("Taj Palace", "Chennai");
        hotel.showDetails();

        // Inventory setup
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Deluxe", 10);
        inventory.addRoomType("Suite", 0);

        // Room details
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("Deluxe", 3000));
        rooms.add(new Room("Suite", 5000));

        // Search
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, rooms);

        // Booking Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Guest requests
        bookingQueue.addRequest(new Reservation("Arun", "Deluxe"));
        bookingQueue.addRequest(new Reservation("Priya", "Suite"));
        bookingQueue.addRequest(new Reservation("Rahul", "Deluxe"));

        // Show queue (FIFO order)
        bookingQueue.showQueue();
    }
}