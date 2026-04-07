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

    // Safe decrement
    boolean decrementRoom(String type) {
        int available = getAvailability(type);
        if (available > 0) {
            inventory.put(type, available - 1);
            return true;
        }
        return false;
    }
}

// Search Service
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

// Reservation
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

// Booking Queue
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    void addRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    Reservation getNextRequest() {
        return queue.poll(); // dequeue
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Service (core logic)
class BookingService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private int roomCounter = 1;

    void processBookings(BookingRequestQueue queue, RoomInventory inventory) {

        while (!queue.isEmpty()) {

            Reservation request = queue.getNextRequest();
            System.out.println("\nProcessing request for " + request.guestName);

            // Check availability
            if (inventory.getAvailability(request.roomType) > 0) {

                // Generate unique room ID
                String roomId = request.roomType + "-" + roomCounter++;

                // Prevent reuse (extra safety)
                if (!allocatedRoomIds.contains(roomId)) {
                    allocatedRoomIds.add(roomId);

                    // Decrement inventory
                    inventory.decrementRoom(request.roomType);

                    System.out.println("Booking Confirmed!");
                    System.out.println("Guest: " + request.guestName);
                    System.out.println("Room ID: " + roomId);
                }

            } else {
                System.out.println("Booking Failed for " + request.guestName +
                        " (No availability)");
            }
        }
    }
}

// Main class
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("Welcome to bookmystay ver:1.00");

        // Hotel
        Hotel hotel = new LuxuryHotel("Taj Palace", "Chennai");
        hotel.showDetails();

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Deluxe", 2);
        inventory.addRoomType("Suite", 1);

        // Queue
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Arun", "Deluxe"));
        queue.addRequest(new Reservation("Priya", "Suite"));
        queue.addRequest(new Reservation("Rahul", "Deluxe"));
        queue.addRequest(new Reservation("Anita", "Deluxe")); // should fail

        // Process bookings
        BookingService bookingService = new BookingService();
        bookingService.processBookings(queue, inventory);
    }
}