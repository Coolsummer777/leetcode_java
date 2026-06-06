package sp_2026.airbnb.low_level_design.amazon_locker;

public class Component {

    private final Size size;
    private LockerStatus status;
    private String cargo;

    public Component(Size size, LockerStatus status) {
        this.size = size;
        this.status = status;
    }

    public synchronized boolean tryReserve(){
        if (!isFree()) return false;
        status = LockerStatus.RESERVED;
        return true;
    }

    public boolean isFree() {
        return status == LockerStatus.FREE;
    }

    public boolean isOccupied() {
        return status == LockerStatus.OCCUPIED;
    }

    public boolean isBroken() {
        return status == LockerStatus.BROKEN;
    }

    public Size getSize() {
        return size;
    }

    public void open(){
    }

    public LockerStatus getStatus() {
        return status;
    }

    public void setUsed() {
        this.status = LockerStatus.OCCUPIED;
    }

    public void setBroken() {
        this.status = LockerStatus.BROKEN;
    }

    public void setFree() {
        this.status = LockerStatus.FREE;
    }
    

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

}
