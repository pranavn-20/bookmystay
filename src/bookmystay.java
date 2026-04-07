abstract class Hotel {
    String name;
    String location;

    // Constructor
    Hotel(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // Abstract method (must be implemented by subclasses)
    abstract void showDetails();
}

// Child class using inheritance
class LuxuryHotel extends Hotel {

    LuxuryHotel(String name, String location) {
        super(name, location);
    }

    @Override
    void showDetails() {
        System.out.println("Luxury Hotel: " + name + ", Location: " + location);
    }
}

public class bookmystay {


    public static void main(String[] args) {
        System.out.println("Welcome to bookmystay ver:1.00");


        Hotel hotel = new LuxuryHotel("Taj Palace", "Chennai");
        hotel.showDetails();
    }
}