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

    void incrementRoom(String type) {
        inventory.put(type, getAvailability(type) + 1);
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

// Booking Record (with status)
class BookingRecord {
    String guestName;
    String roomType;
    String roomId;
    String status; // CONFIRMED / CANCELLED

    BookingRecord(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.status = "CONFIRMED";
    }

    void cancel() {
        this.status = "CANCELLED";
    }

    void showRecord() {
        System.out.println("Guest: " + guestName +
                ", RoomType: " + roomType +
                ", RoomID: " + roomId +
                ", Status: " + status);
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

    BookingRecord findByRoomId(String roomId) {
        for (BookingRecord r : records) {
            if (r.roomId.equals(roomId)) return r;
        }
        return null;
    }
}

// Booking Service
class BookingService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private int roomCounter = 1;
    private BookingHistory history;

    BookingService(BookingHistory history) {
        this.history = history;
    }

    String book(Reservation request, RoomInventory inventory) {

        if (inventory.getAvailability(request.roomType) > 0) {

            String roomId = request.roomType + "-" + roomCounter++;

            if (!allocatedRoomIds.contains(roomId)) {
                allocatedRoomIds.add(roomId);
                inventory.decrementRoom(request.roomType);

                BookingRecord record = new BookingRecord(
                        request.guestName,
                        request.roomType,
                        roomId
                );

                history.addRecord(record);

                System.out.println("Booking Confirmed: " + roomId);
                return roomId;
            }
        }

        System.out.println("Booking Failed");
        return null;
    }
}

// Cancellation Service
class CancellationService {

    void cancelBooking(String roomId, BookingHistory history, RoomInventory inventory) {

        BookingRecord record = history.findByRoomId(roomId);

        if (record == null) {
            System.out.println("Cancellation Failed: Booking not found");
            return;
        }

        if (record.status.equals("CANCELLED")) {
            System.out.println("Cancellation Failed: Already cancelled");
            return;
        }

        // Rollback
        inventory.incrementRoom(record.roomType);
        record.cancel();

        System.out.println("Booking Cancelled Successfully: " + roomId);
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
}

// Main
public class bookmystay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Deluxe", 2);

        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService(history);
        CancellationService cancellationService = new CancellationService();

        // Bookings
        String id1 = bookingService.book(new Reservation("Arun", "Deluxe"), inventory);
        String id2 = bookingService.book(new Reservation("Priya", "Deluxe"), inventory);

        // Cancel one booking
        cancellationService.cancelBooking(id1, history, inventory);

        // Try cancelling again (edge case)
        cancellationService.cancelBooking(id1, history, inventory);

        // Report
        BookingReportService report = new BookingReportService();
        report.showAllBookings(history);

        System.out.println("Available Deluxe Rooms: " +
                inventory.getAvailability("Deluxe"));
    }
}