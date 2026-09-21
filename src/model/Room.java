package model;

public class Room {
    private int roomId;
    private String roomNumber;
    private int floor;
    private String roomType; // Single, Double, Triple, Four Sharing
    private int capacity;
    private int occupiedBeds;
    private int availableBeds;
    private String status; // AVAILABLE, PARTIALLY OCCUPIED, FULL

    public Room() {}

    public Room(int roomId, String roomNumber, int floor, String roomType, int capacity, int occupiedBeds, int availableBeds, String status) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.roomType = roomType;
        this.capacity = capacity;
        this.occupiedBeds = occupiedBeds;
        this.availableBeds = capacity - occupiedBeds;
        this.status = calculateStatus();
    }

    public String calculateStatus() {
        if (occupiedBeds >= capacity) {
            return "FULL";
        } else if (occupiedBeds > 0) {
            return "PARTIALLY OCCUPIED";
        } else {
            return "AVAILABLE";
        }
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        this.availableBeds = this.capacity - this.occupiedBeds;
        this.status = calculateStatus();
    }

    public int getOccupiedBeds() { return occupiedBeds; }
    public void setOccupiedBeds(int occupiedBeds) {
        this.occupiedBeds = occupiedBeds;
        this.availableBeds = this.capacity - this.occupiedBeds;
        this.status = calculateStatus();
    }

    public int getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(int availableBeds) { this.availableBeds = availableBeds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + " - Avail: " + (capacity - occupiedBeds) + ")";
    }
}

