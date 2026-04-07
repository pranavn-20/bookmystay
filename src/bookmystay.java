import java.io.*;
import java.util.*;

// Serializable Inventory
class RoomInventory implements Serializable {
    private Map<String, Integer> inventory = new HashMap<>();

    void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    boolean bookRoom(String type) {
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

    Map<String, Integer> getAll() {
        return inventory;
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

// Booking Record
class BookingRecord implements Serializable {
    String guestName;
    String roomType;
    String roomId;
    String status;

    BookingRecord(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.status = "CONFIRMED";
    }

    void cancel() {
        status = "CANCELLED";
    }

    void show() {
        System.out.println(guestName + " | " + roomType + " | " + roomId + " | " + status);
    }
}

// Booking History
class BookingHistory implements Serializable {
    private List<BookingRecord> records = new ArrayList<>();

    void add(BookingRecord r) {
        records.add(r);
    }

    List<BookingRecord> getAll() {
        return records;
    }

    BookingRecord find(String roomId) {
        for (BookingRecord r : records) {
            if (r.roomId.equals(roomId)) return r;
        }
        return null;
    }
}

// Booking Service
class BookingService {
    private int counter = 1;
    private BookingHistory history;

    BookingService(BookingHistory history) {
        this.history = history;
    }

    String book(Reservation req, RoomInventory inv) {
        if (inv.bookRoom(req.roomType)) {
            String id = req.roomType + "-" + counter++;

            BookingRecord record = new BookingRecord(
                    req.guestName, req.roomType, id
            );
            history.add(record);

            System.out.println("Booked: " + id);
            return id;
        }
        System.out.println("Booking failed");
        return null;
    }
}

// Cancellation
class CancellationService {
    void cancel(String roomId, BookingHistory history, RoomInventory inv) {

        BookingRecord r = history.find(roomId);

        if (r == null || r.status.equals("CANCELLED")) {
            System.out.println("Invalid cancellation");
            return;
        }

        inv.incrementRoom(r.roomType);
        r.cancel();

        System.out.println("Cancelled: " + roomId);
    }
}

// ✅ Persistence Service
class PersistenceService {

    private static final String FILE = "system_state.dat";

    // Save system state
    void save(RoomInventory inv, BookingHistory history) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE))) {

            oos.writeObject(inv);
            oos.writeObject(history);

            System.out.println("System state saved.");

        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    // Load system state
    Object[] load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE))) {

            RoomInventory inv = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();

            System.out.println("System state restored.");
            return new Object[]{inv, history};

        } catch (Exception e) {
            System.out.println("No previous state found. Starting fresh.");
            return null;
        }
    }
}

// Main
public class bookmystay {

    public static void main(String[] args) {

        PersistenceService ps = new PersistenceService();

        RoomInventory inventory;
        BookingHistory history;

        // 🔁 LOAD STATE (startup)
        Object[] state = ps.load();

        if (state != null) {
            inventory = (RoomInventory) state[0];
            history = (BookingHistory) state[1];
        } else {
            inventory = new RoomInventory();
            history = new BookingHistory();

            inventory.addRoomType("Deluxe", 2);
        }

        BookingService booking = new BookingService(history);
        CancellationService cancel = new CancellationService();

        // Operations
        String id1 = booking.book(new Reservation("Arun", "Deluxe"), inventory);
        String id2 = booking.book(new Reservation("Priya", "Deluxe"), inventory);

        cancel.cancel(id1, history, inventory);

        // Show history
        System.out.println("\n--- History ---");
        for (BookingRecord r : history.getAll()) {
            r.show();
        }

        System.out.println("\nAvailable Deluxe: " +
                inventory.getAvailability("Deluxe"));

        // 💾 SAVE STATE (shutdown)
        ps.save(inventory, history);
    }
}