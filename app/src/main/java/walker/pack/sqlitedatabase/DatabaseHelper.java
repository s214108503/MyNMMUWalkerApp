package walker.pack.sqlitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import walker.pack.classes.Building;
import walker.pack.classes.Entrance;
import walker.pack.classes.POI;
import walker.pack.classes.QRCode;
import walker.pack.classes.Staff;
import walker.pack.classes.Venue;

/**
 * Created by s214108503 on 2017/07/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int database_version = 1;

    // Database Name
    public static final String database_name = "NMMUWalkerDB.db";

    // Table names
    private static final String table_staff = "Staff",
            table_venue = "Venue",
            table_building = "Building",
            table_QRCode = "QRCode", table_POI = "POI", table_Entrance = "EntranceExit";

    private static final String table_fav_staff = "Fav_Staff",
            table_fav_venue = "Fav_Venue",
            table_fav_poi = "Fav_POI";


    //=================================Table Columns names==========================================

    private static final String Key_Fav_PK = "ID",
            Key_Fav_Staff_ID = "Fav_Staff_ID",
            Key_Fav_Venue_DoorID = "Fav_Venue_DoorID",
            Key_Fav_Venue_Floor_Level = "Fav_Venue_Floor_Level",
            Key_Fav_Venue_Building = "Fav_Venue_Building",
            Key_Fav_POI_ID = "Fav_POI_ID";

    // Staff
    private static final String Key_Staff_ID = "Staff_ID" //pk
            ,Key_Door_ID = "Door_ID", //fk
            Key_Floor_Number = "Floor_Number", //fk
            Key_Building_Number = "Building_Number" //fk
            ,Key_Name = "Name",
            Key_Surname = "Surname",
            Key_Position = "Position",
            Key_Department = "Department",
            Key_Campus = "Campus",
            Key_Phone = "Phone",
            Key_Email = "Email",
            Key_Image_URL = "Image_URL";
    // Venue
    private static final String Key_Venue_Door_ID = "Door_ID", //pk
            Key_Venue_Floor_Number = "Floor_Number", //pk
            Key_Venue_Building_Number = "Building_Number" //pk
            ,Key_Venue_Type = "Type"
            ,Key_Venue_Alternative_Doors = "Alternative_Doors"
            ,Key_Venue_Latitude = "Latitude"
            ,Key_Venue_Longitude = "Longitude";
    // Building
    private static final String Key_Building_Building_Number = "Building_Number",
            Key_Building_Second_Name = "Second_Name",
            Key_Building_Latitude = "Latitude",
            Key_Building_Longitude = "Longitude";
    // QRCode
    private static final String Key_QR_QR_ID = "QR_ID", //pk
            Key_QR_Building_Number = "Building_Number", //fk
            Key_QR_Description = "Description",
            Key_QR_Image_URL = "Image_URL",
            Key_QR_Latitude = "Latitude",
            Key_QR_Longitude = "Longitude",
            Key_QR_Floor_Level = "Floor_Level";
    // POI
    private static final String Key_POI_POI_ID = "POI_ID", //pk
            Key_POI_Door_ID = "Door_ID", // fk1
            Key_POI_Floor_Level = "Floor_Level", // fk1
            Key_POI_Building_Number = "Building_Number", // fk1
            Key_POI_QR_ID = "QR_ID", // fk2
            Key_POI_Type = "Type",
            Key_POI_Description = "Description";

    // EntranceExit
    private static final String Key_Entrance_Building = "Building",
            Key_Entrance_Floor = "Floor",
            Key_Entrance_ID = "Door",
            Key_Entrance_X = "x",
            Key_Entrance_Y = "y",
            Key_Entrance_Latitude = "Latitude",
            Key_Entrance_Longitude = "Longitude";
    //==============================================================================================




    public DatabaseHelper(Context context) {
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String create_entrance_table = "CREATE TABLE "+ table_Entrance
                +" ( "
                +Key_Entrance_Building+" INTEGER NOT NULL, "
                +Key_Entrance_Floor+" INTEGER NOT NULL, "
                +Key_Entrance_ID+" TEXT NOT NULL, "
                +Key_Entrance_X+" REAL NOT NULL, "
                +Key_Entrance_Y+" REAL NOT NULL, "
                +Key_Entrance_Latitude+" REAL, "
                +Key_Entrance_Longitude+" REAL, "
                + "PRIMARY KEY (" + Key_Entrance_Building + "," + Key_Entrance_Floor + "," + Key_Entrance_ID + "," + Key_Entrance_X + "," + Key_Entrance_Y +")"
                + " )";
        sqLiteDatabase.execSQL(create_entrance_table);

        // Create Building table
        String create_building_table = "CREATE TABLE " + table_building
                + "("
                + Key_Building_Building_Number + " TEXT NOT NULL, "
                + Key_Building_Second_Name + " TEXT, "
                + Key_Building_Latitude + " REAL, "
                + Key_Building_Longitude + " REAL, "
                + "PRIMARY KEY (" + Key_Building_Building_Number + ")"
                + ")";
        sqLiteDatabase.execSQL(create_building_table);

        // Create venue
        String create_venue_table = "CREATE TABLE " + table_venue
                + "("
                + Key_Venue_Door_ID + " TEXT NOT NULL, "
                + Key_Venue_Floor_Number + " TEXT NOT NULL, "
                + Key_Venue_Building_Number + " TEXT NOT NULL, "
                + Key_Venue_Type + " TEXT, "
                + Key_Venue_Alternative_Doors + " TEXT, "
                + Key_Venue_Latitude + " REAL, "
                + Key_Venue_Longitude + " REAL, "
                + " PRIMARY KEY (" + Key_Venue_Door_ID + "," + Key_Venue_Floor_Number + "," + Key_Venue_Building_Number + "), "
                + " FOREIGN KEY (" + Key_Venue_Building_Number + ") REFERENCES " + table_building + "(" + Key_Building_Building_Number +")"
                + ")";
        sqLiteDatabase.execSQL(create_venue_table);

        // Create staff table
        String create_staff_table = "CREATE TABLE " + table_staff
                + "("
                + Key_Staff_ID + " TEXT NOT NULL, "
                + Key_Door_ID + " TEXT NOT NULL, "
                + Key_Floor_Number + " TEXT NOT NULL, "
                + Key_Building_Number + " TEXT NOT NULL, "
                + Key_Name + " TEXT, "
                + Key_Surname + " TEXT, "
                + Key_Position + " TEXT, "
                + Key_Department + " TEXT, "
                + Key_Campus + " TEXT, "
                + Key_Phone + " TEXT, "
                + Key_Email + " TEXT, "
                + Key_Image_URL + " TEXT, "
                + " PRIMARY KEY (" + Key_Staff_ID + "), "
                + " FOREIGN KEY (" + Key_Door_ID + ") REFERENCES " + table_venue + "(" + Key_Venue_Door_ID + "), "
                + " FOREIGN KEY (" + Key_Floor_Number + ") REFERENCES " + table_venue + "(" + Key_Venue_Floor_Number+ "), "
                + " FOREIGN KEY (" + Key_Building_Number + ") REFERENCES " + table_venue + "(" + Key_Venue_Building_Number+ ")"
                +")";
        sqLiteDatabase.execSQL(create_staff_table);

        // Create QRCode
        String create_qr_code_table = "CREATE TABLE " + table_QRCode
                + "("
                + Key_QR_QR_ID + " TEXT NOT NULL, "
                + Key_QR_Building_Number + " TEXT, "
                + Key_QR_Description + " TEXT, "
                + Key_QR_Image_URL + " TEXT, "
                + Key_QR_Latitude + " REAL, "
                + Key_QR_Longitude + " REAL, "
                + Key_QR_Floor_Level + " INTEGER, "
                + " PRIMARY KEY(" + Key_QR_QR_ID + "), "
                + " FOREIGN KEY(" + Key_QR_Building_Number + ") REFERENCES " + table_building + "(" + Key_Building_Building_Number + ")"
                + ")";
        sqLiteDatabase.execSQL(create_qr_code_table);

        // Create POI
        String create_poi_table = "CREATE TABLE " + table_POI
                + "("
                + Key_POI_POI_ID + " TEXT NOT NULL, "
                + Key_POI_Door_ID + " TEXT, "
                + Key_POI_Floor_Level + " TEXT, "
                + Key_POI_Building_Number + " TEXT, "
                + Key_POI_QR_ID + " TEXT, "
                + Key_POI_Type + " TEXT, "
                + Key_POI_Description + " TEXT, "
                + " PRIMARY KEY(" + Key_POI_POI_ID + "), "
                + " FOREIGN KEY(" + Key_POI_Door_ID + ") REFERENCES " + table_venue + "(" + Key_Venue_Door_ID + "), "
                + " FOREIGN KEY(" + Key_POI_Floor_Level + ") REFERENCES " + table_venue + "(" + Key_Venue_Floor_Number + "), "
                + " FOREIGN KEY(" + Key_POI_Building_Number + ") REFERENCES  " + table_venue + "(" + Key_Venue_Building_Number + "), "
                + " FOREIGN KEY(" + Key_POI_Building_Number + ") REFERENCES " + table_building + "(" + Key_Building_Building_Number + "), "
                + " FOREIGN KEY(" + Key_POI_QR_ID + ") REFERENCES " + table_QRCode + "(" + Key_QR_QR_ID + ")"
                + ")";
        sqLiteDatabase.execSQL(create_poi_table);

        String create_fav_staff_table = "CREATE TABLE "+ table_fav_staff
                + "("
                + Key_Fav_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Key_Fav_Staff_ID + " REFERENCES " + table_staff + "("+Key_Staff_ID+")"
                + ")";
        sqLiteDatabase.execSQL(create_fav_staff_table);

        String create_fav_venue_table = "CREATE TABLE "+ table_fav_venue
                + "("
                + Key_Fav_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Key_Fav_Venue_DoorID + " TEXT REFERENCES " + table_venue+ "("+Key_Venue_Door_ID+"), "
                + Key_Fav_Venue_Floor_Level+ " TEXT REFERENCES " + table_venue+ "("+Key_Venue_Floor_Number+"), "
                + Key_Fav_Venue_Building+ " TEXT REFERENCES " + table_venue+ "("+Key_Venue_Building_Number+") "
                + ")";
        sqLiteDatabase.execSQL(create_fav_venue_table);

        String create_fav_poi_table = "CREATE TABLE "+ table_fav_poi
                + "("
                + Key_Fav_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Key_Fav_POI_ID + " TEXT REFERENCES " + table_POI + "("+Key_POI_POI_ID+")"
                + ")";
        sqLiteDatabase.execSQL(create_fav_poi_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_staff);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_POI);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_QRCode);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_venue);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_building);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_fav_staff);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_fav_venue);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_fav_poi);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_Entrance);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    //==============================================================================================
    // CRUD Staff
    // Adds new staff member
    public void addStaffMember(Staff staff_member) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Staff_ID, staff_member.getStaff_ID()); // Staff ID
        values.put(Key_Door_ID, staff_member.getDoor_ID()); // Staff Door ID
        values.put(Key_Floor_Number, staff_member.getFloor_Number()); // Staff Floor Level
        values.put(Key_Building_Number, staff_member.getBuilding_Number()); // Staff Building Number
        values.put(Key_Name, staff_member.getName()); // Staff Name
        values.put(Key_Surname, staff_member.getSurname()); // Staff Surname
        values.put(Key_Position, staff_member.getPosition()); // Staff Position
        values.put(Key_Department, staff_member.getDepartment()); // Staff Department
        values.put(Key_Campus, staff_member.getCampus()); // Staff Campus
        values.put(Key_Phone, staff_member.getPhone()); // Staff Phone number
        values.put(Key_Email, staff_member.getEmail()); // Staff Email
        values.put(Key_Image_URL, staff_member.getImage_URL()); // Staff Image url

        // Inserting Row
        db.insert(table_staff, null, values);
        db.close(); // Closing database connection
    }

    // Gets staff member
    public Staff getStaffMember(String staff_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the result column projection.
        String[] result_columns = new String[]
                {Key_Staff_ID,
                Key_Door_ID,
                Key_Floor_Number,
                Key_Building_Number,
                Key_Name,
                Key_Surname,
                Key_Position,
                Key_Department,
                Key_Campus,
                Key_Phone,
                Key_Email,
                Key_Image_URL};

        String where_clause = Key_Staff_ID + " =? ";
        String whereArgs[] = new String[] {staff_id};
        String groupBy = null;
        String having = null;
        String order = null;

        Cursor cursor = db.query(table_staff, result_columns , where_clause,
               whereArgs, groupBy, having, order);

        Staff resultant_staff = null;
        if (cursor != null) {
            cursor.moveToFirst();
            resultant_staff = new Staff(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11));

            cursor.close();
        }

        return resultant_staff;
    }

    // Gets all staff members
    public ArrayList<Staff> getStaffMembers() {
        ArrayList<Staff> staff_list = new ArrayList<>();

        // Select all query
        String query = "SELECT * FROM "+ table_staff;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            do {
                Staff cur = new Staff(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11));

                // Add current to list
                staff_list.add(cur);
            } while (cursor.moveToNext());

        // close cursor after using it
        cursor.close();

        return staff_list;
    }

    // Gets staff member count
    public int getStaffCount() {
        String countQuery = "SELECT  * FROM " + table_staff;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updates single staff member details
    public int updateStaffMember(Staff staff_member) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Staff_ID, staff_member.getStaff_ID()); // Staff ID
        values.put(Key_Door_ID, staff_member.getDoor_ID()); // Staff Door ID
        values.put(Key_Floor_Number, staff_member.getFloor_Number()); // Staff Floor Level
        values.put(Key_Building_Number, staff_member.getBuilding_Number()); // Staff Building Number
        values.put(Key_Name, staff_member.getName()); // Staff Name
        values.put(Key_Surname, staff_member.getSurname()); // Staff Surname
        values.put(Key_Position, staff_member.getPosition()); // Staff Position
        values.put(Key_Department, staff_member.getDepartment()); // Staff Department
        values.put(Key_Campus, staff_member.getCampus()); // Staff Campus
        values.put(Key_Phone, staff_member.getPhone()); // Staff Phone number
        values.put(Key_Email, staff_member.getEmail()); // Staff Email
        values.put(Key_Image_URL, staff_member.getImage_URL()); // Staff Image url

        // Updating row
        int result = db.update(table_staff,
                values,
                Key_Staff_ID + " =? ",
                new String[] { staff_member.getStaff_ID()});

        // Close db
        db.close();

        // Results of update
        return result;
    }

    // Deletes single staff member details
    public void deleteStaffMember(Staff staff_member) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_staff,
                Key_Staff_ID + " =? ",
                new String[] { staff_member.getStaff_ID()});
        db.close();
    }
    //==============================================================================================

    //==============================================================================================
    // CRUD Venue
    // Adds new venue
    public void addVenue(Venue venue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Venue_Door_ID, venue.getDoor_ID());
        values.put(Key_Venue_Floor_Number, venue.getFloor_Number());
        values.put(Key_Venue_Building_Number, venue.getBuilding_Number());
        values.put(Key_Venue_Type, venue.getType());
        values.put(Key_Venue_Alternative_Doors, venue.getAlternative_Doors_String());
        values.put(Key_Venue_Latitude, venue.getLatitude());
        values.put(Key_Venue_Longitude, venue.getLongitude());

        // Inserting Row
        db.insert(table_venue, null, values);
        // Closing database connection
        db.close();
    }

    // Gets venue matching
    public Venue getVenue(String door_id, String floor_level, String building_number) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the result column projection.
        String[] result_columns = new String[]
                {Key_Venue_Door_ID,
                        Key_Venue_Floor_Number,
                        Key_Venue_Building_Number,
                        Key_Venue_Type,
                        Key_Venue_Alternative_Doors,
                        Key_Venue_Latitude,
                        Key_Venue_Longitude};

        String where_clause = Key_Venue_Door_ID + "=? AND "+ Key_Venue_Floor_Number +" =? AND " + Key_Venue_Building_Number +" =? ";
        String whereArgs[] = new String[] {door_id, floor_level, building_number};
        String groupBy = null;
        String having = null;
        String order = null;

        Cursor cursor = db.query(table_venue, result_columns , where_clause,
                whereArgs, groupBy, having, order);

        Venue resultant_venue = null;
        if (cursor != null) {
            cursor.moveToFirst();
            resultant_venue = new Venue(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6));

            cursor.close();
        }
        return resultant_venue;
    }

    // Gets all venues
    public ArrayList<Venue> getVenues() {
        ArrayList<Venue> venues = new ArrayList<>();

        // Select all query
        String query = "SELECT * FROM "+ table_venue;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            do {
                Venue cur = new Venue(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6));

                // Add current to list
                venues.add(cur);
            } while (cursor.moveToNext());

        // close cursor after using it
        cursor.close();

        return venues;
    }

    // Gets venue count
    public int getVenueCount() {
        String countQuery = "SELECT  * FROM " + table_venue;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updates a venue's details
    public int updateVenue(Venue venue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Venue_Door_ID, venue.getDoor_ID());
        values.put(Key_Venue_Floor_Number, venue.getFloor_Number());
        values.put(Key_Venue_Building_Number, venue.getBuilding_Number());
        values.put(Key_Venue_Type, venue.getType());
        values.put(Key_Venue_Alternative_Doors, venue.getAlternative_Doors_String());
        values.put(Key_Venue_Latitude, venue.getLatitude());
        values.put(Key_Venue_Longitude, venue.getLongitude());

        // Updating row
        int result = db.update(table_venue,
                values,
                Key_Venue_Door_ID + " =? AND "+ Key_Venue_Floor_Number+ " =? AND " + Key_Venue_Building_Number + " =?",
                new String[] { venue.getDoor_ID(), venue.getFloor_Number(), venue.getBuilding_Number()});

        // Close db
        db.close();

        // Results of update
        return result;
    }

    // Deletes single venue
    public void deleteVenue(Venue venue) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_venue,
                Key_Venue_Door_ID + " =? AND "+ Key_Venue_Floor_Number+ " =? AND " + Key_Venue_Building_Number + " =?",
                new String[] { venue.getDoor_ID(), venue.getFloor_Number(), venue.getBuilding_Number()});
        db.close();
    }
    //==============================================================================================

    // CRUD Building
    //==============================================================================================
    // Adds new building
    public void addBuilding(Building building) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Building_Building_Number, building.getBuilding_Number());
        values.put(Key_Building_Second_Name, building.getSecond_Name());
        values.put(Key_Building_Latitude, building.getLatitude());
        values.put(Key_Building_Longitude, building.getLongitude());

        // Inserting Row
        db.insert(table_building, null, values);
        // Closing database connection
        db.close();
    }

    // Gets matching building
    public Building getBuilding(String building_number) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the result column projection.
        String[] result_columns = new String[]
                {Key_Building_Building_Number,
                        Key_Building_Second_Name,
                        Key_Building_Latitude,
                        Key_Building_Longitude};

        String where_clause = Key_Building_Building_Number + "=?";
        String whereArgs[] = new String[] {building_number};
        String groupBy = null;
        String having = null;
        String order = null;

        Cursor cursor = db.query(table_building, result_columns , where_clause,
                whereArgs, groupBy, having, order);

        Building resultant_building = null;
        if (cursor != null) {
            cursor.moveToFirst();
            resultant_building = new Building(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3));

            cursor.close();
        }
        return resultant_building;
    }

    // Gets all building
    public ArrayList<Building> getBuildings() {
        ArrayList<Building> buildings = new ArrayList<>();

        // Select all query
        String query = "SELECT * FROM "+ table_building;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            do {
                Building cur = new Building(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3));

                // Add current to list
                buildings.add(cur);
            } while (cursor.moveToNext());

        // close cursor after using it
        cursor.close();

        return buildings;
    }

    // Gets building count
    public int getBuildingCount() {
        String countQuery = "SELECT  * FROM " + table_building;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updates a building's details
    public int updateBuilding(Building building) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Building_Building_Number, building.getBuilding_Number());
        values.put(Key_Building_Second_Name, building.getSecond_Name());
        values.put(Key_Building_Latitude, building.getLatitude());
        values.put(Key_Building_Longitude, building.getLongitude());

        String where_clause = Key_Building_Building_Number + " =? ";
        String whereArgs[] = new String[] {building.getBuilding_Number()};

        // Updating row
        int result = db.update(table_building, values, where_clause, whereArgs);

        // Close db
        db.close();

        // Results of update
        return result;
    }

    // Deletes single building
    public void deleteBuilding(Building building) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where_clause = Key_Building_Building_Number + " =? ";
        String whereArgs[] = new String[] {building.getBuilding_Number()};

        db.delete(table_building, where_clause, whereArgs);

        db.close();
    }
    //==============================================================================================

    // CRUD QR Code
    //==============================================================================================
    // Adds new qr code
    public void addQRCode(QRCode qrCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_QR_QR_ID, qrCode.getQR_ID());
        values.put(Key_QR_Building_Number, qrCode.getBuilding_Number());
        values.put(Key_QR_Description, qrCode.getDescription());
        values.put(Key_QR_Image_URL, qrCode.getImage_URL());
        values.put(Key_QR_Latitude, qrCode.getLatitude());
        values.put(Key_QR_Longitude, qrCode.getLongitude());
        values.put(Key_QR_Floor_Level, qrCode.getFloor_Level());

        // Inserting Row
        db.insert(table_QRCode, null, values);
        // Closing database connection
        db.close();
    }

    // Gets matching QR Code
    public QRCode getQRCode(String qr_code_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the result column projection.
        String[] result_columns = new String[]
                {       Key_QR_QR_ID,
                        Key_QR_Building_Number,
                        Key_QR_Description,
                        Key_QR_Image_URL,
                        Key_QR_Latitude,
                        Key_QR_Longitude,
                        Key_QR_Floor_Level };

        String where_clause = Key_QR_QR_ID + " =? ";
        String whereArgs[] = new String[] {qr_code_id};
        String groupBy = null;
        String having = null;
        String order = null;

        Cursor cursor = db.query(table_QRCode, result_columns , where_clause, whereArgs, groupBy, having, order);

        QRCode resultant_qr_code = null;
        if (cursor != null) {
            cursor.moveToFirst();
            resultant_qr_code = new QRCode(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5),
                    cursor.getInt(6) );

            cursor.close();
        }
        return resultant_qr_code;
    }

    // Gets all QR Codes
    public ArrayList<QRCode> getQRCodes() {
        ArrayList<QRCode> qrCodes = new ArrayList<>();

        // Select all query
        String query = "SELECT * FROM "+ table_QRCode;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            do {
                QRCode cur = new QRCode(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getInt(6) );

                // Add current to list
                qrCodes.add(cur);
            } while (cursor.moveToNext());

        // close cursor after using it
        cursor.close();

        return qrCodes;
    }

    // Gets QR Code count
    public int getQRCodeCount() {
        String countQuery = "SELECT  * FROM " + table_QRCode;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updates a QR Code's details
    public int updateQRCode(QRCode qrCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_QR_QR_ID, qrCode.getQR_ID());
        values.put(Key_QR_Building_Number, qrCode.getBuilding_Number());
        values.put(Key_QR_Description, qrCode.getDescription());
        values.put(Key_QR_Image_URL, qrCode.getImage_URL());
        values.put(Key_QR_Latitude, qrCode.getLatitude());
        values.put(Key_QR_Longitude, qrCode.getLongitude());
        values.put(Key_QR_Floor_Level, qrCode.getFloor_Level());

        String where_clause = Key_QR_QR_ID + " =? ";
        String whereArgs[] = new String[] {qrCode.getQR_ID()};

        // Updating row
        int result = db.update(table_QRCode, values, where_clause, whereArgs);

        // Closes the Cursor, releasing all of its resources and making it completely invalid.
        db.close();

        // Results of update
        return result;
    }

    // Deletes single QR Code
    public void deleteQRCode(QRCode qrCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where_clause = Key_QR_QR_ID + " =? ";
        String whereArgs[] = new String[] {qrCode.getQR_ID()};

        db.delete(table_QRCode, where_clause, whereArgs);

        // Closing database connection
        db.close();
    }
    //==============================================================================================

    // CRUD QR Code
    //==============================================================================================
    /*private static final String Key_POI_POI_ID = "POI_ID", //pk
            Key_POI_Door_ID = "Door_ID", // fk1
            Key_POI_Floor_Level = "Floor_Level", // fk1
            Key_POI_Building_Number = "Building_Number", // fk1
            Key_POI_QR_ID = "QR_ID", // fk2
            Key_POI_Type = "Type",
            Key_POI_Description = "Description";*/

    // Adds new POI
    public void addPOI(POI poi) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_POI_POI_ID, poi.getPOI_ID());
        values.put(Key_POI_Door_ID, poi.getDoor_ID());
        values.put(Key_POI_Floor_Level, poi.getFloor_Level());
        values.put(Key_POI_Building_Number, poi.getBuilding_Number());
        values.put(Key_POI_QR_ID, poi.getQR_ID());
        values.put(Key_POI_Type, poi.getType());
        values.put(Key_POI_Description, poi.getDescription());

        // Inserting Row
        db.insert(table_POI, null, values);

        // Closing database connection
        db.close();
    }

    // Gets matching POI
    public POI getPOI(String poi_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the result column projection.
        String[] result_columns = new String[]
                {       Key_POI_POI_ID,
                        Key_POI_Door_ID,
                        Key_POI_Floor_Level,
                        Key_POI_Building_Number,
                        Key_POI_QR_ID,
                        Key_POI_Type,
                        Key_POI_Description};

        String where_clause = Key_POI_POI_ID + " =? ";
        String whereArgs[] = new String[] {poi_id};
        String groupBy = null;
        String having = null;
        String order = null;

        Cursor cursor = db.query(table_POI, result_columns , where_clause, whereArgs, groupBy, having, order);

        POI resultant_poi = null;
        if (cursor != null) {
            cursor.moveToFirst();
            resultant_poi = new POI(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6));

            cursor.close();
        }
        return resultant_poi;
    }

    // Gets all POIs
    public ArrayList<POI> getPOIs() {
        ArrayList<POI> pois = new ArrayList<>();

        // Select all query
        String query = "SELECT * FROM "+ table_POI;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            do {
                POI cur = new POI(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));

                // Add current to list
                pois.add(cur);
            } while (cursor.moveToNext());

        // close cursor after using it
        cursor.close();

        return pois;
    }

    // Gets POI count
    public int getPOICount() {
        String countQuery = "SELECT  * FROM " + table_POI;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Updates a POI's details
    public int updatePOI(POI poi) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_POI_POI_ID, poi.getPOI_ID());
        values.put(Key_POI_Door_ID, poi.getDoor_ID());
        values.put(Key_POI_Floor_Level, poi.getFloor_Level());
        values.put(Key_POI_Building_Number, poi.getBuilding_Number());
        values.put(Key_POI_QR_ID, poi.getQR_ID());
        values.put(Key_POI_Type, poi.getType());
        values.put(Key_POI_Description, poi.getDescription());

        String where_clause = Key_POI_POI_ID + " =? ";
        String whereArgs[] = new String[] {poi.getPOI_ID()};

        // Updating row
        int result = db.update(table_POI, values, where_clause, whereArgs);

        // Closes the Cursor, releasing all of its resources and making it completely invalid.
        db.close();

        // Results of update
        return result;
    }

    // Deletes single POI
    public void deletePOI(POI poi) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where_clause = Key_POI_POI_ID + " =? ";
        String whereArgs[] = new String[] {poi.getPOI_ID()};

        db.delete(table_POI, where_clause, whereArgs);

        // Closing database connection
        db.close();
    }

    //==============================================================================================
    public void addFavStaff(Staff s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Fav_Staff_ID, s.getStaff_ID());

        // Inserting Row
        db.insert(table_fav_staff, null, values);

        // Closing database connection
        db.close();
    }

    public ArrayList<String> getFavStaffIDs() {
        ArrayList<String> fav_staff = new ArrayList<>();
        // Select all query
        String query = "SELECT * FROM "+ table_fav_staff;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            do {
                fav_staff.add(cursor.getString(1));
            } while (cursor.moveToNext());
        // close cursor after using it
        cursor.close();
        return fav_staff;
    }

    public void deleteFavStaff(Staff s) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where_clause = Key_Fav_Staff_ID + " =? ";
        String whereArgs[] = new String[] {s.getStaff_ID()};
        db.delete(table_fav_staff, where_clause, whereArgs);
        // Closing database connection
        db.close();
    }

    public void addFavVenue(Venue v) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Key_Fav_Venue_DoorID, v.getDoor_ID());
        values.put(Key_Fav_Venue_Floor_Level, v.getFloor_Number());
        values.put(Key_Fav_Venue_Building, v.getBuilding_Number());
        // Inserting Row
        db.insert(table_fav_venue, null, values);
        // Closing database connection
        db.close();
    }

    public ArrayList<String> getFavVenueID() {
        ArrayList<String> fav_venues = new ArrayList<>();
        // Select all query
        String query = "SELECT * FROM "+ table_fav_venue;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            do {
                String sss = cursor.getString(0);
                String ssss = cursor.getString(1);
                String sssss = cursor.getString(2);
                String ssssss = cursor.getString(3);
                fav_venues.add(cursor.getString(1)+"_"+cursor.getString(2)+"_"+cursor.getString(3));
            } while (cursor.moveToNext());
        // close cursor after using it
        cursor.close();
        return fav_venues;
    }

    public void deleteFavVenue(Venue v) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where_clause = Key_Fav_Venue_DoorID + " =? AND "
                + Key_Fav_Venue_Floor_Level + " =? AND "
                + Key_Fav_Venue_Building + " =?";
        String whereArgs[] = new String[] {v.getDoor_ID(), v.getFloor_Number(), v.getBuilding_Number()};

        db.delete(table_fav_venue, where_clause, whereArgs);

        // Closing database connection
        db.close();
    }

    public void addFavPOI(POI p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Key_Fav_POI_ID, p.getPOI_ID());
        // Inserting Row
        db.insert(table_fav_poi, null, values);
        // Closing database connection
        db.close();
    }

    public ArrayList<String> getFavPOIIDs() {
        ArrayList<String> fav_pois = new ArrayList<>();
        // Select all query
        String query = "SELECT * FROM "+ table_fav_poi;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            do {
                fav_pois.add(cursor.getString(1));
            } while (cursor.moveToNext());
        // close cursor after using it
        cursor.close();
        return fav_pois;
    }

    public void deleteFavPoi(POI p) {
        SQLiteDatabase db = this.getWritableDatabase();

        String where_clause = Key_Fav_POI_ID + " =? ";
        String whereArgs[] = new String[] {p.getPOI_ID()};

        db.delete(table_fav_poi, where_clause, whereArgs);

        // Closing database connection
        db.close();
    }

    //==============================================================================================

    public void addEntrance(Entrance e) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Key_Entrance_Building, e.Building);
        values.put(Key_Entrance_Floor, e.Floor);
        values.put(Key_Entrance_ID, e.Door);
        values.put(Key_Entrance_X, e.X);
        values.put(Key_Entrance_Y, e.Y);
        values.put(Key_Entrance_Latitude, e.Latitude);
        values.put(Key_Entrance_Longitude, e.Longitude);
        // Inserting Row
        db.insert(table_Entrance, null, values);
        // Closing database connection
        db.close();
    }

    public ArrayList<Entrance> getEntrances() {
        ArrayList<Entrance> temp = new ArrayList<>();
        // Select all query
        String query = "SELECT * FROM "+ table_Entrance;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            do {
                temp.add(new Entrance(cursor.getInt(0), cursor.getInt(1), cursor.getString(3),
                        cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5),
                        cursor.getDouble(6)));
            } while (cursor.moveToNext());
        // close cursor after using it
        cursor.close();
        return temp;
    }
    public Entrance getEntranceByLocalXY(double x, double y){
        ArrayList<Entrance> temp = getEntrances();
        for (Entrance entrance: temp){
            if (entrance.X==x && entrance.Y==y)
                return entrance;
        }
        return null;
    }
    public Entrance getEntranceByLatLong(double lat, double lon){
        for (Entrance entrance: getEntrances()){
            if (entrance.Latitude==lat && entrance.Longitude==lon)
                return entrance;
        }
        return null;
    }
    public Entrance getEntranceByBFD_ID(String s){
        String[] arrayS = s.split("_");
        for (Entrance entrance: getEntrances()){
            if (entrance.Building.equals(Integer.valueOf(arrayS[0])) &&
                    entrance.Floor.equals(Integer.valueOf(arrayS[1])) &&
                    entrance.Door.equals(arrayS[2]))
                return entrance;
        }
        return null;
    }

    //==============================================================================================
}

