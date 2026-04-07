import java.util.*;

// Inventory (shared resource)
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    synchronized void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    synchronized int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    // Critical section
    synchronized boolean bookRoom(String type) {
        int available = getAvailability(type);
        if (available > 0) {
            inventory.put(type, available - 1);
            return true;
        }
        return false;
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

// Thread-safe Queue
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    synchronized void addRequest(Reservation r) {
        queue.offer(r);
    }

    synchronized Reservation getRequest() {
        return queue.poll();
    }

    synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking History
class BookingRecord {
    String guestName;
    String roomId;

    BookingRecord(String guestName, String roomId) {
        this.guestName = guestName;
        this.roomId = roomId;
    }
}

class BookingHistory {
    private List<BookingRecord> records = new ArrayList<>();

    synchronized void addRecord(BookingRecord record) {
        records.add(record);
    }

    synchronized void showAll() {
        System.out.println("\n--- Booking History ---");
        for (BookingRecord r : records) {
            System.out.println(r.guestName + " -> " + r.roomId);
        }
    }
}

// Booking Service (shared)
class BookingService {
    private int counter = 1;

    synchronized String generateRoomId(String type) {
        return type + "-" + counter++;
    }
}

// Worker Thread
class BookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;
    private BookingService service;
    private BookingHistory history;

    BookingProcessor(BookingRequestQueue queue,
                     RoomInventory inventory,
                     BookingService service,
                     BookingHistory history) {
        this.queue = queue;
        this.inventory = inventory;
        this.service = service;
        this.history = history;
    }

    @Override
    public void run() {

        while (true) {

            Reservation request;

            // synchronized queue access
            synchronized (queue) {
                if (queue.isEmpty()) break;
                request = queue.getRequest();
            }

            if (request == null) continue;

            // Critical section (inventory + allocation)
            synchronized (inventory) {

                if (inventory.bookRoom(request.roomType)) {

                    String roomId = service.generateRoomId(request.roomType);

                    history.addRecord(new BookingRecord(
                            request.guestName, roomId
                    ));

                    System.out.println(Thread.currentThread().getName() +
                            " booked " + roomId + " for " + request.guestName);

                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " FAILED for " + request.guestName);
                }
            }
        }
    }
}

// Main
public class bookmystay {

    public static void main(String[] args) throws InterruptedException {

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Deluxe", 2);

        BookingRequestQueue queue = new BookingRequestQueue();

        // Multiple guests (simultaneous requests)
        queue.addRequest(new Reservation("Arun", "Deluxe"));
        queue.addRequest(new Reservation("Priya", "Deluxe"));
        queue.addRequest(new Reservation("Rahul", "Deluxe"));
        queue.addRequest(new Reservation("Anita", "Deluxe"));

        BookingService service = new BookingService();
        BookingHistory history = new BookingHistory();

        // Multiple threads (concurrent processing)
        Thread t1 = new Thread(new BookingProcessor(queue, inventory, service, history), "T1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory, service, history), "T2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // Final state
        history.showAll();

        System.out.println("\nFinal Availability: " +
                inventory.getAvailability("Deluxe"));
    }
}