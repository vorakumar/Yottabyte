package com.thoughtworks.yottabyte.vehiclecount;

/**
 * Created by kumarvora on 6/15/15.
 */
public class Vehicle {

    public final String type;

    public Vehicle(String vehicleRecord, String columnSeparator) {
        String[] data = vehicleRecord.split(columnSeparator);
        this.type = data[0];
    }

}
