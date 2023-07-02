package com.example.javalaba7;

import java.io.*;
import java.util.*;

public class Flat implements Serializable {

    private static final long serialVersionUID = 1L;
    int num;
    public static final String txtNum = "Number of flat";
    int area;
    public static final String txtArea = "Area";
    int floor;
    public static final String txtFloor = "Floor";
    int rooms;
    public static final String txtRooms = "Number of rooms";
    String street;
    public static final String txtStreet = "Street";
    String type;
    public static final String txtType = "Type";
    int lifetime;
    public static final String txtLifetime = "Lifetime";

    static Boolean validNum(int num) { return num > 0 ; }

    static Boolean validArea(int area) {
        return area > 0 ;
    }

    static Boolean validRooms(int rooms) {
        return rooms > 0 ;
    }

    static Boolean validFloor(int floor) {
        return floor > 0 ;
    }

    static Boolean validLifetime(int lifetime) { return lifetime > 0 ; }
    public static boolean nextRead(Scanner in, PrintStream out) {
        return nextRead(txtNum, in, out);
    }

    static boolean nextRead(final String prompt, Scanner in, PrintStream out) {
        out.print(prompt);
        out.print(": ");
        return in.hasNextLine();
    }

    public static Flat read(Scanner in, PrintStream out) throws IOException,
            NumberFormatException {
        String str1;
        Flat flat = new Flat();

        str1 = in.nextLine();
        flat.num = Integer.parseInt(str1);
        if (Flat.validNum(flat.num) == false) {
            throw new IOException("Invalid number: " + flat.num);
        }

        if (!nextRead(txtArea, in, out)) {
            return null;
        }
        str1 = in.nextLine();
        flat.area = Integer.parseInt(str1);
        if (Flat.validArea(flat.area) == false) {
            throw new IOException("Invalid area: " + flat.area);
        }

        if (!nextRead(txtFloor, in, out)) {
            return null;
        }
        str1 = in.nextLine();
        flat.floor = Integer.parseInt(str1);
        if (Flat.validFloor(flat.floor) == false) {
            throw new IOException("Invalid floor: " + flat.floor);
        }

        if (!nextRead(txtStreet, in, out)) {
            return null;
        }
        flat.street=in.nextLine().trim();

        if (!nextRead(txtRooms, in, out)) {
            return null;
        }
        str1 = in.nextLine();
        flat.rooms = Integer.parseInt(str1);
        if (Flat.validRooms(flat.rooms) == false) {
            throw new IOException("Invalid number of rooms: " + flat.rooms);
        }

        if (!nextRead(txtType, in, out)) {
            return null;
        }
        flat.type = in.nextLine();

        if (!nextRead(txtLifetime, in, out)) {
            return null;
        }
        str1 = in.nextLine();
        flat.lifetime = Integer.parseInt(str1);
        if (Flat.validLifetime(flat.lifetime) == false) {
            throw new IOException("Invalid lifetime:" +flat.lifetime);
        }

        return flat;
    }

    public Flat() {
    }
    
    public static final String areaDel = " ";

    @Override
    public String toString() {
        return num + areaDel +
                area + areaDel +
                floor + areaDel +
                street + areaDel +
                rooms + areaDel +
                type + areaDel +
                lifetime;
    }

       void setArea(String key) {
      int newArea=Integer.parseInt(key);
      this.area=newArea;
    }
    
    void setNum(String key) {
      int newNum=Integer.parseInt(key);
      this.num=newNum;
    }
    void setFloor(String key) {
      int newFloor=Integer.parseInt(key);
      this.floor=newFloor;
    }
    void setStreet(String key) {
     
      this.street=key;
    }
    void setRooms(String key) {
      int newRooms=Integer.parseInt(key);
      this.rooms=newRooms;
    }
    
     void setType(String key) {
      this.type=key;
    }
    void setLifetime(String key) {
      int newLifetime=Integer.parseInt(key);
      this.lifetime=newLifetime;
    }
    
   
}
