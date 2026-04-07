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

    void showDetails() {
        System.out.println("Luxury Hotel: " + name + ", Location: " + location);
    }
}

// Room
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

// Inventory
class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

    void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

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
}

// Queue
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    void addRequest(Reservation r) {
        queue.offer(r);
    }

    Reservation getNextRequest() {
        return queue.poll();
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Record
class BookingRecord {
    String guestName;
    String roomType;
    String roomId;

    BookingRecord(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    void showRecord() {
        System.out.println("Guest: " + guestName +
                ", RoomType: " + roomType +
                ", RoomID: " + roomId);
    }
}

// Booking History
class BookingHistory {
    private List<BookingRecord> records = new ArrayList<>();

    void addRecord(BookingRecord record) {
        records.add(record);
    }

    List<BookingRecord> getAllRecords() {
        return records;
    }
}

// Report Service
class BookingReportService {
    void showAllBookings(BookingHistory history) {
        System.out.println("\n--- Booking History ---");
        for (BookingRecord r : history.getAllRecords()) {
            r.showRecord();
        }
    }

    void showTotalBookings(BookingHistory history) {
        System.out.println("\nTotal Bookings: " + history.getAllRecords().size());
    }
}

// Custom Exception
class InvalidBookingException extends Exception {
    InvalidBookingException(String message) {
        super(message);
    }
}

// Validator
class InvalidBookingValidator {
    void validate(Reservation request, RoomInventory inventory) throws InvalidBookingException {

        if (request == null) {
            throw new InvalidBookingException("Booking request cannot be null");
        }

        if (request.guestName == null || request.guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name is invalid");
        }

        if (request.roomType == null || request.roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Room type is invalid");
        }

        if (inventory.getAvailability(request.roomType) < 0) {
            throw new InvalidBookingException("Invalid inventory state");
        }
    }
}

// Booking Service
class BookingService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private int roomCounter = 1;
    private BookingHistory history;
    private InvalidBookingValidator validator = new InvalidBookingValidator();

    BookingService(BookingHistory history) {
        this.history = history;
    }

    void processBookings(BookingRequestQueue queue, RoomInventory inventory) {
        while (!queue.isEmpty()) {
            processSingleBooking(queue.getNextRequest(), inventory);
        }
    }

    String processSingleBooking(Reservation request, RoomInventory inventory) {

        try {
            validator.validate(request, inventory);

            if (inventory.getAvailability(request.roomType) > 0) {

                String roomId = request.roomType + "-" + roomCounter++;

                if (!allocatedRoomIds.contains(roomId)) {
                    allocatedRoomIds.add(roomId);
                    inventory.decrementRoom(request.roomType);

                    System.out.println("\nBooking Confirmed!");
                    System.out.println("Guest: " + request.guestName);
                    System.out.println("Room ID: " + roomId);

                    history.addRecord(new BookingRecord(
                            request.guestName,
                            request.roomType,
                            roomId
                    ));

                    return roomId;
                }
            } else {
                System.out.println("\nBooking Failed: No availability for " + request.roomType);
            }

        } catch (InvalidBookingException e) {
            System.out.println("\nBooking Error: " + e.getMessage());
        }

        return null;
    }
}

// Add-On Service
class AddOnService {
    String name;
    double price;

    AddOnService(String name, double price) {
        this.name = name;
        this.price = price;
    }
}

// Add-On Manager
class AddOnServiceManager {
    private Map<String, List<AddOnService>> addOnMap = new HashMap<>();

    void addServices(String roomId, List<AddOnService> services) {
        addOnMap.put(roomId, services);
    }

    double calculateTotalCost(String roomId) {
        double total = 0;
        List<AddOnService> services = addOnMap.get(roomId);

        if (services != null) {
            for (AddOnService s : services) {
                total += s.price;
            }
        }
        return total;
    }

    void showServices(String roomId) {
        System.out.println("\nAdd-On Services for Room: " + roomId);

        List<AddOnService> services = addOnMap.get(roomId);
        if (services != null) {
            for (AddOnService s : services) {
                System.out.println(s.name + " - " + s.price);
            }
        } else {
            System.out.println("No add-ons selected.");
        }
    }
}

// Main
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("Welcome to bookmystay ver:1.00");

        Hotel hotel = new LuxuryHotel("Taj Palace", "Chennai");
        hotel.showDetails();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Deluxe", 2);
        inventory.addRoomType("Suite", 1);

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("Deluxe", 3000));
        rooms.add(new Room("Suite", 5000));

        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, rooms);

        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Arun", "Deluxe"));
        queue.addRequest(new Reservation("Priya", "Suite"));
        queue.addRequest(new Reservation("Rahul", "Deluxe"));

        // Invalid test cases
        queue.addRequest(new Reservation("", "Deluxe"));
        queue.addRequest(new Reservation("Ravi", ""));
        queue.addRequest(new Reservation(null, "Suite"));

        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService(history);

        bookingService.processBookings(queue, inventory);

        // Add-ons
        AddOnServiceManager addOnManager = new AddOnServiceManager();
        AddOnService breakfast = new AddOnService("Breakfast", 500);

        String roomId = bookingService.processSingleBooking(
                new Reservation("Kiran", "Deluxe"), inventory);

        if (roomId != null) {
            List<AddOnService> services = new ArrayList<>();
            services.add(breakfast);

            addOnManager.addServices(roomId, services);
            addOnManager.showServices(roomId);

            double total = addOnManager.calculateTotalCost(roomId);
            System.out.println("Total Add-On Cost: " + total);
        }

        // Admin report
        BookingReportService reportService = new BookingReportService();
        reportService.showAllBookings(history);
        reportService.showTotalBookings(history);
    }
}
