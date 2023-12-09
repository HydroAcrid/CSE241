[[Final Project Guidelines]]

# Tables 
-- Drop tables with foreign key references
	DROP TABLE Payment;
	DROP TABLE Lease;
	DROP TABLE Pet;
	DROP TABLE Apartment_Amenities;
	DROP TABLE Property_Amenities;
	DROP TABLE Private_Amenities;
	DROP TABLE Common_Amenities;
	DROP TABLE Apartment;
	DROP TABLE Chargeable_Amenities; 
	DROP TABLE Visitor; -- If it does not reference Tenant

-- Drop referenced tables (parents)
	DROP TABLE Tenant;
	DROP TABLE Amenity;
	DROP TABLE Property;

-- Drop tables with no dependencies
	DROP TABLE Address;
	DROP TABLE Tenant_Privileges; -- Not using anymore


-- Address Table
CREATE TABLE Address ( 
	addr_id INT PRIMARY KEY, 
	state VARCHAR(50), 
	town VARCHAR(100), 
	street VARCHAR(200), 
	zip_code VARCHAR(10) 
);

-- Chargeable Amenities Table
CREATE TABLE Chargeable_Amenities (
    chargeable_amen_id NUMBER PRIMARY KEY,
    amen_name VARCHAR2(100) CHECK (amen_name IN ('gym', 'parking')),
    cost NUMBER,
    prop_id NUMBER,
    FOREIGN KEY (prop_id) REFERENCES Property(prop_id)
);

-- Amenity Table
CREATE TABLE Amenity (
    amen_id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    type VARCHAR2(10) CHECK (type IN ('private', 'common'))
    --ADD CONDITION HERE ("POOR, GOOD, GREAT")
);

-- Tenant Table
CREATE TABLE Tenant (
    ten_id NUMBER PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    phone_num VARCHAR2(15) NOT NULL
    email_addr VARCHAR2(255) UNIQUE NOT NULL 
);

-- Tenant Privileges Table
--NOT USED ANYMORE 
CREATE TABLE Tenant_Privileges (
    tpriv_id NUMBER PRIMARY KEY,
    name VARCHAR2(255),
    title VARCHAR2(255)
);

-- Visitor Table
CREATE TABLE Visitor (
    visit_id NUMBER PRIMARY KEY,
    age NUMBER,
    salary NUMBER,
    visit_name VARCHAR2(255)
);

-- Property Table
CREATE TABLE Property (
    prop_id NUMBER PRIMARY KEY,
    manager VARCHAR2(100),
    common_amen VARCHAR2(255),
    addr_id NUMBER,
    FOREIGN KEY (addr_id) REFERENCES Address(addr_id)
);

-- Apartment Table
CREATE TABLE Apartment (
    apt_id NUMBER PRIMARY KEY,
    bed_num NUMBER,
    bath_num NUMBER,
    rent NUMBER,
    size NUMBER,
    sec_dep NUMBER,
    priv_amen CHAR(1) CHECK (priv_amen IN ('Y', 'N')),
    prop_id NUMBER,
    FOREIGN KEY (prop_id) REFERENCES Property(prop_id)
);

-- Private Amenities Table
CREATE TABLE Private_Amenities (
    amen_id NUMBER, 
    wash_dry CHAR(1) CHECK (wash_dry IN ('Y', 'N')),
    ac CHAR(1) CHECK (ac IN ('Y', 'N')),
    dishwasher CHAR(1) CHECK (dishwasher IN ('Y', 'N')),
    fireplace CHAR(1) CHECK (fireplace IN ('Y', 'N')),
    FOREIGN KEY (amen_id) REFERENCES Amenity(amen_id)
);

-- Common Amenities Table
CREATE TABLE Common_Amenities (
    amen_id NUMBER,
    gym CHAR(1) CHECK (gym IN ('Y', 'N')),
    pool CHAR(1) CHECK (pool IN ('Y', 'N')),
    parking CHAR(1) CHECK (parking IN ('Y', 'N')),
    grill CHAR(1) CHECK (grill IN ('Y', 'N')),
    FOREIGN KEY (amen_id) REFERENCES Amenity(amen_id)
);

-- Lease Table
CREATE TABLE Lease (
    lease_id NUMBER PRIMARY KEY,
    lease_start_date DATE,
    lease_end_date DATE,
    rent_amt NUMBER,
    ten_id NUMBER,
    FOREIGN KEY (ten_id) REFERENCES Tenant(ten_id)
);

-- Payment Method Table
CREATE TABLE PaymentMethod (
    pay_id NUMBER PRIMARY KEY,
    method_name VARCHAR2(50),
    provider VARCHAR2(50),
    transaction_fee NUMBER
);

ALTER TABLE PaymentMethod ADD (
    ten_id NUMBER,
    FOREIGN KEY (ten_id) REFERENCES Tenant(ten_id)
);

-- Payment Table
CREATE TABLE Payment (
    pay_id NUMBER PRIMARY KEY,
    pay_date DATE,
    amount NUMBER,
    payment_method VARCHAR2(50),
    lease_id NUMBER,
    FOREIGN KEY (lease_id) REFERENCES Lease(lease_id)
);

ALTER TABLE Payment DROP COLUMN payment_method;

ALTER TABLE Payment ADD (
    payment_method_id NUMBER,
    FOREIGN KEY (payment_method_id) REFERENCES PaymentMethod(pay_id)
);

-- Debit Card Table
CREATE TABLE DebitCard (
    card_id NUMBER PRIMARY KEY,
    card_name VARCHAR2(255),
    card_num VARCHAR2(19), -- Assuming 16 digits + 3 spaces for formatting
    security_code NUMBER(3), -- Assuming 3 digits for CVV
    expiry_date DATE,
    payment_method_id NUMBER,
    FOREIGN KEY (payment_method_id) REFERENCES PaymentMethod(pay_id)
);

-- Credit Card Table
CREATE TABLE CreditCard (
    card_id NUMBER PRIMARY KEY,
    card_name VARCHAR2(255),
    card_num VARCHAR2(19), -- Same assumption as for DebitCard
    credit_limit NUMBER,
    expiry_date DATE,
    security_code NUMBER(4), -- Assuming 3 digits for CVV
    interest_rate NUMBER,
    payment_method_id NUMBER,
    FOREIGN KEY (payment_method_id) REFERENCES PaymentMethod(pay_id)
);

-- Bank Transfer Table
CREATE TABLE BankTransfer (
    transfer_id NUMBER PRIMARY KEY,
    acct_name VARCHAR2(255),
    acct_num VARCHAR2(34), -- IBAN can be up to 34 characters
    routing_num VARCHAR2(9), -- Routing number is 9 digits in the U.S.
    transfer_limit NUMBER,
    payment_method_id NUMBER,
    FOREIGN KEY (payment_method_id) REFERENCES PaymentMethod(pay_id)
);

ALTER TABLE DebitCard ADD (
    ten_id NUMBER,
    FOREIGN KEY (ten_id) REFERENCES Tenant(ten_id)
);

ALTER TABLE CreditCard ADD (
    ten_id NUMBER,
    FOREIGN KEY (ten_id) REFERENCES Tenant(ten_id)
);

ALTER TABLE BankTransfer ADD (
    ten_id NUMBER,
    FOREIGN KEY (ten_id) REFERENCES Tenant(ten_id)
);


-- Pet Table
CREATE TABLE Pet (
    pet_id NUMBER PRIMARY KEY,
    pet_name VARCHAR2(255),
    ten_id NUMBER,
    FOREIGN KEY (ten_id) REFERENCES Tenant(ten_id)
);

--Property amenities
--Need to add constraints here to make sure only common amenities can go in here 
CREATE TABLE Property_Amenities (
    prop_id NUMBER,
    amen_id NUMBER,
    PRIMARY KEY (prop_id, amen_id),
    FOREIGN KEY (prop_id) REFERENCES Property(prop_id),
    FOREIGN KEY (amen_id) REFERENCES Amenity(amen_id)
);

--Apartment amenities
CREATE TABLE Apartment_Amenities (
    apt_id NUMBER,
    amen_id NUMBER,
    PRIMARY KEY (apt_id, amen_id),
    FOREIGN KEY (apt_id) REFERENCES Apartment(apt_id),
    FOREIGN KEY (amen_id) REFERENCES Amenity(amen_id) 
);

ALTER TABLE Lease
ADD apt_id NUMBER;

ALTER TABLE Lease
ADD CONSTRAINT fk_lease_apartment FOREIGN KEY (apt_id) REFERENCES Apartment(apt_id);




# Sequences 
CREATE SEQUENCE paymentmethod_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE debitcard_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE creditcard_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE banktransfer_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE payment_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE tenant_seq START WITH 26 INCREMENT BY 1;

CREATE SEQUENCE pet_seq START WITH 26 INCREMENT BY 1;

CREATE SEQUENCE visitor_seq START WITH 26 INCREMENT BY 1;

CREATE SEQUENCE lease_seq START WITH 26 INCREMENT BY 1;





# Triggers 
--Trigger to make sure only common amenities go in property amenities 
CREATE OR REPLACE TRIGGER trg_before_insert_update_property_amenities
BEFORE INSERT OR UPDATE ON Property_Amenities
FOR EACH ROW
DECLARE
    v_amen_type VARCHAR2(10);
BEGIN
    SELECT type
    INTO v_amen_type
    FROM Amenity
    WHERE amen_id = :NEW.amen_id;

    IF v_amen_type <> 'common' THEN
        RAISE_APPLICATION_ERROR(-20001, 'Only common amenities are allowed in Property_Amenities');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20002, 'Amenity ID not found');
END;
/

--Trigger to make sure only private amenities go in apartment amenities 
CREATE OR REPLACE TRIGGER trg_before_insert_update_apartment_amenities
BEFORE INSERT OR UPDATE ON Apartment_Amenities
FOR EACH ROW
DECLARE
    v_amen_type VARCHAR2(10);
BEGIN
    SELECT type
    INTO v_amen_type
    FROM Amenity
    WHERE amen_id = :NEW.amen_id;

    IF v_amen_type <> 'private' THEN
        RAISE_APPLICATION_ERROR(-20001, 'Only private amenities are allowed in Apartment_Amenities');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20002, 'Amenity ID not found');
END;
/

--trigger to auto increment payment_method 
CREATE OR REPLACE TRIGGER paymentmethod_trg
BEFORE INSERT ON PaymentMethod
FOR EACH ROW
BEGIN
  SELECT paymentmethod_seq.NEXTVAL INTO :new.pay_id FROM dual;
END;
/

-- Trigger for DebitCard table
CREATE OR REPLACE TRIGGER debitcard_trg
BEFORE INSERT ON DebitCard
FOR EACH ROW
BEGIN
  SELECT debitcard_seq.NEXTVAL INTO :new.card_id FROM dual;
END;
/

-- Trigger for CreditCard table
CREATE OR REPLACE TRIGGER creditcard_trg
BEFORE INSERT ON CreditCard
FOR EACH ROW
BEGIN
  SELECT creditcard_seq.NEXTVAL INTO :new.card_id FROM dual;
END;
/

-- Trigger for BankTransfer table
CREATE OR REPLACE TRIGGER banktransfer_trg
BEFORE INSERT ON BankTransfer
FOR EACH ROW
BEGIN
  SELECT banktransfer_seq.NEXTVAL INTO :new.transfer_id FROM dual;
END;
/

--trigger to use the tenant sequence 
CREATE OR REPLACE TRIGGER tenant_bir 
BEFORE INSERT ON Tenant
FOR EACH ROW
BEGIN
    SELECT tenant_seq.NEXTVAL
    INTO   :new.ten_id
    FROM   dual;
END;

--trigger for pet sequence 
CREATE OR REPLACE TRIGGER pet_id_trg
BEFORE INSERT ON Pet
FOR EACH ROW
BEGIN
  SELECT pet_seq.NEXTVAL
  INTO   :new.pet_id
  FROM   dual;
END;

--trigger for visitor
CREATE OR REPLACE TRIGGER visitor_id_trg
BEFORE INSERT ON Visitor
FOR EACH ROW
BEGIN
  SELECT visitor_seq.NEXTVAL
  INTO   :new.visit_id
  FROM   dual;
END;

--trigger for lease
CREATE OR REPLACE TRIGGER lease_id_trg
BEFORE INSERT ON Lease
FOR EACH ROW
BEGIN
  SELECT lease_seq.NEXTVAL
  INTO   :new.lease_id
  FROM   dual;
END;




# Data 

## Address
INSERT ALL
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (1, 'California', 'San Francisco', '123 Golden Gate Ave', 94102)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (2, 'Texas', 'Austin', '456 Lone Star Blvd', 78701)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (3, 'New York', 'Brooklyn', '789 Empire St', 11201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (4, 'Florida', 'Miami', '101 Ocean Drive', 33139)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (5, 'Illinois', 'Chicago', '202 Windy Rd', 60601)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (6, 'Washington', 'Seattle', '303 Rainy Ln', 98101)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (7, 'Georgia', 'Atlanta', '404 Peachtree St', 30303)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (8, 'Oregon', 'Portland', '505 Forest Blvd', 97201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (9, 'Colorado', 'Denver', '606 Mountain Rd', 80201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (10, 'Ohio', 'Columbus', '707 Buckeye St', 43201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (11, 'Massachusetts', 'Boston', '808 Liberty Ave', 02101)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (12, 'Arizona', 'Phoenix', '909 Desert Blvd', 85001)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (13, 'Nevada', 'Las Vegas', '110 Strip Ave', 89101)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (14, 'Tennessee', 'Nashville', '211 Country Rd', 37201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (15, 'Minnesota', 'Minneapolis', '312 Lake St', 55401)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (16, 'Wisconsin', 'Milwaukee', '413 Brew Ave', 53201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (17, 'Michigan', 'Detroit', '514 Motor St', 48201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (18, 'North Carolina', 'Charlotte', '615 Queen Rd', 28201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (19, 'Virginia', 'Richmond', '716 Capital Blvd', 23201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (20, 'Indiana', 'Indianapolis', '817 Speedway Ln', 46201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (21, 'Missouri', 'Kansas City', '918 Jazz St', 64101)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (22, 'Pennsylvania', 'Philadelphia', '1019 Liberty Bell Blvd', 19101)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (23, 'Maryland', 'Baltimore', '1120 Harbor Rd', 21201)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (24, 'South Carolina', 'Charleston', '1221 Palmetto Ave', 29401)
INTO ADDRESS (ADDR_ID, STATE, TOWN, STREET, ZIP_CODE) VALUES (25, 'New Jersey', 'Newark', '1313 Garden St', 07101)
SELECT 1 FROM DUAL;

## Apartment
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (1, 2, 1, 1500, 700, 'Y', 1); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (2, 3, 2, 2100, 950, 'N', 1); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (3, 1, 1, 1200, 550, 'N', 1); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (4, 2, 2, 1800, 800, 'Y', 2); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (5, 3, 1, 2000, 900, 'N', 2); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (6, 1, 1, 1300, 600, 'Y', 2); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (7, 3, 3, 2500, 1000, 'N', 3); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (8, 2, 2, 1800, 850, 'Y', 3); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (9, 3, 2, 2300, 950, 'Y', 4); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (10, 2, 1, 1600, 750, 'N', 4); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (11, 1, 1, 1400, 650, 'Y', 4); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (12, 3, 3, 2400, 1050, 'N', 5); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (13, 2, 2, 1700, 800, 'Y', 5); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (14, 3, 1, 2200, 920, 'N', 6); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (15, 1, 1, 1350, 630, 'Y', 6); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (16, 2, 1, 1650, 780, 'N', 7); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (17, 3, 2, 2250, 960, 'Y', 7); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (18, 1, 1, 1380, 640, 'Y', 8); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (19, 2, 2, 1750, 820, 'N', 8); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (20, 3, 3, 2450, 1060, 'Y', 9); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (21, 2, 1, 1580, 760, 'Y', 9); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (22, 1, 1, 1420, 660, 'N', 10); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (23, 3, 2, 2350, 970, 'N', 10); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (24, 2, 2, 1720, 810, 'Y', 10); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (25, 1, 1, 1390, 650, 'N', 11); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (26, 2, 1, 1620, 770, 'Y', 11); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (27, 3, 3, 2480, 1080, 'N', 12); 
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (28, 2, 2, 1780, 830, 'Y', 12);
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (29, 1, 1, 1430, 670, 'Y', 13);
INSERT INTO APARTMENT(APT_ID, BED_NUM, BATH_NUM, RENT, ROOM_SIZE, PRIV_AMEN, PROP_ID) VALUES (30, 3, 1, 2270, 980, 'N', 13);


## Property
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (1, 'John Doe', 'Pool,Gym', 1);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (2, 'Jane Smith', 'Gym,Parking', 2);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (3, 'Alice Johnson', 'Pool,Grill', 3);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (4, 'Bob Williams', 'Gym', 4);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (5, 'Charlie Brown', 'Parking,Grill', 5);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (6, 'Eve Davis', 'Pool', 6);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (7, 'Frank Wilson', 'Gym,Parking', 7);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (8, 'Grace Lee', 'Grill', 8);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (9, 'Harry White', 'Pool,Gym', 9);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (10, 'Ivy Green', 'Parking', 10);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (11, 'Jack Black', 'Pool,Grill', 11);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (12, 'Kathy Gray', 'Gym', 12);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (13, 'Leo King', 'Parking,Grill', 13);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (14, 'Mia Queen', 'Pool', 14);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (15, 'Nick Stone', 'Gym,Parking', 15);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (16, 'Olivia Knight', 'Grill', 16);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (17, 'Peter Gold', 'Pool,Gym', 17);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (18, 'Quinn Silver', 'Parking', 18);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (19, 'Rachel Bronze', 'Pool,Grill', 19);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (20, 'Steve Iron', 'Gym', 20);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (21, 'Tina Copper', 'Parking,Grill', 21);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (22, 'Ursula Brass', 'Pool', 22);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (23, 'Victor Steel', 'Gym,Parking', 23);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (24, 'Wendy Zinc', 'Grill', 24);
INSERT INTO PROPERTY(PROP_ID, MANAGER, COMMON_AMEN, ADDR_ID) VALUES (25, 'Xander Mercury', 'Pool,Gym', 25);

## Tenant 
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (1, 'John Doe', '123-456-7890', 'john.doe@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (2, 'Jane Smith', '123-456-7891', 'jane.smith@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (3, 'Alice Johnson', '123-456-7892', 'alice.johnson@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (4, 'Bob Wilson', '123-456-7893', 'bob.wilson@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (5, 'Charlie Brown', '123-456-7894', 'charlie.brown@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (6, 'Diana Ross', '123-456-7895', 'diana.ross@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (7, 'Eva Green', '123-456-7896', 'eva.green@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (8, 'Frank Castle', '123-456-7897', 'frank.castle@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (9, 'Grace Kelly', '123-456-7898', 'grace.kelly@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (10, 'Henry Ford', '123-456-7899', 'henry.ford@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (11, 'Isabella Rossellini', '123-456-7888', 'isabella.rossellini@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (12, 'Jack Nicholson', '123-456-7877', 'jack.nicholson@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (13, 'Katie Holmes', '123-456-7866', 'katie.holmes@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (14, 'Louis Armstrong', '123-456-7855', 'louis.armstrong@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (15, 'Monica Bellucci', '123-456-7844', 'monica.bellucci@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (16, 'Natalie Portman', '123-456-7833', 'natalie.portman@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (17, 'Oliver Stone', '123-456-7822', 'oliver.stone@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (18, 'Patty Smith', '123-456-7811', 'patty.smith@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (19, 'Quincy Jones', '123-456-7800', 'quincy.jones@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (20, 'Rebecca Hall', '123-456-7770', 'rebecca.hall@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (21, 'Steve Martin', '123-456-7771', 'steve.martin@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (22, 'Tina Turner', '123-456-7772', 'tina.turner@gmail.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (23, 'Ursula Andress', '123-456-7773', 'ursula.andress@icloud.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (24, 'Vincent Vega', '123-456-7774', 'vincent.vega@yahoo.com');
INSERT INTO Tenant (ten_id, name, phone_num, email_addr) VALUES (25, 'Whitney Houston', '123-456-7775', 'whitney.houston@gmail.com');

## Visitor 
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (1, 25, 50000, 'Aaron White');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (2, 30, 52000, 'Bella Thompson');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (3, 35, 55000, 'Charlie Martin');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (4, 28, 60000, 'Diana Wilson');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (5, 40, 65000, 'Edward Johnson');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (6, 32, 56000, 'Fiona Walker');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (7, 29, 57000, 'George Hall');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (8, 38, 59000, 'Hannah Moore');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (9, 45, 70000, 'Ian Taylor');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (10, 27, 51000, 'Jasmine Clark');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (11, 33, 64000, 'Kevin Patel');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (12, 31, 54000, 'Laura Evans');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (13, 34, 62000, 'Mason Brown');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (14, 36, 67000, 'Nora Adams');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (15, 41, 68000, 'Oliver Smith');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (16, 39, 71000, 'Penny Roberts');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (17, 42, 72000, 'Quincy Davis');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (18, 37, 58000, 'Rita Lewis');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (19, 43, 73000, 'Steven King');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (20, 44, 75000, 'Tina Turner');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (21, 26, 49000, 'Ulysses Grant');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (22, 46, 74000, 'Victoria Lee');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (23, 24, 48000, 'Walter Miller');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (24, 23, 47000, 'Xena Prince');
INSERT INTO visitor (VISIT_ID, AGE, SALARY, VISIT_NAME) VALUES (25, 47, 76000, 'Yves Saint Laurent');

## Amenity
-- Private Amenities
INSERT INTO Amenity (amen_id, name, type) VALUES (1, 'wash_dry', 'private');
INSERT INTO Amenity (amen_id, name, type) VALUES (2, 'ac', 'private');
INSERT INTO Amenity (amen_id, name, type) VALUES (3, 'dishwasher', 'private');
INSERT INTO Amenity (amen_id, name, type) VALUES (4, 'fireplace', 'private');

-- Common Amenities
INSERT INTO Amenity (amen_id, name, type) VALUES (5, 'gym', 'common');
INSERT INTO Amenity (amen_id, name, type) VALUES (6, 'pool', 'common');
INSERT INTO Amenity (amen_id, name, type) VALUES (7, 'parking', 'common');
INSERT INTO Amenity (amen_id, name, type) VALUES (8, 'grill', 'common');

## Chargeable Amenity
INSERT INTO Chargeable_Amenities (chargeable_amen_id, amen_name, cost) VALUES (1, 'parking', 50);  -- assuming $50 monthly cost for parking
INSERT INTO Chargeable_Amenities (chargeable_amen_id, amen_name, cost) VALUES (2, 'gym', 30);      -- assuming $30 monthly cost for gym access

## Lease 
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (1, TO_DATE('2023-01-01', 'YYYY-MM-DD'), TO_DATE('2024-01-01', 'YYYY-MM-DD'), 1200, 1);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (2, TO_DATE('2023-01-15', 'YYYY-MM-DD'), TO_DATE('2024-01-15', 'YYYY-MM-DD'), 1100, 2);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (3, TO_DATE('2023-02-01', 'YYYY-MM-DD'), TO_DATE('2024-02-01', 'YYYY-MM-DD'), 1000, 3);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (3, TO_DATE('2023-02-01', 'YYYY-MM-DD'), TO_DATE('2024-02-01', 'YYYY-MM-DD'), 1000, 4);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (4, TO_DATE('2023-02-20', 'YYYY-MM-DD'), TO_DATE('2024-02-20', 'YYYY-MM-DD'), 1050, 5);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (5, TO_DATE('2023-03-10', 'YYYY-MM-DD'), TO_DATE('2024-03-10', 'YYYY-MM-DD'), 1150, 6);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (6, TO_DATE('2023-04-01', 'YYYY-MM-DD'), TO_DATE('2024-04-01', 'YYYY-MM-DD'), 950, 7);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (6, TO_DATE('2023-04-01', 'YYYY-MM-DD'), TO_DATE('2024-04-01', 'YYYY-MM-DD'), 950, 8);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (7, TO_DATE('2023-04-25', 'YYYY-MM-DD'), TO_DATE('2024-04-25', 'YYYY-MM-DD'), 980, 9);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (7, TO_DATE('2023-04-25', 'YYYY-MM-DD'), TO_DATE('2024-04-25', 'YYYY-MM-DD'), 980, 10);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (8, TO_DATE('2023-05-10', 'YYYY-MM-DD'), TO_DATE('2024-05-10', 'YYYY-MM-DD'), 1030, 11);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (9, TO_DATE('2023-06-01', 'YYYY-MM-DD'), TO_DATE('2024-06-01', 'YYYY-MM-DD'), 1070, 12);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (10, TO_DATE('2023-07-01', 'YYYY-MM-DD'), TO_DATE('2024-07-01', 'YYYY-MM-DD'), 1110, 13);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (10, TO_DATE('2023-07-01', 'YYYY-MM-DD'), TO_DATE('2024-07-01', 'YYYY-MM-DD'), 1110, 14);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (11, TO_DATE('2023-08-01', 'YYYY-MM-DD'), TO_DATE('2024-08-01', 'YYYY-MM-DD'), 1130, 15);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (12, TO_DATE('2023-09-01', 'YYYY-MM-DD'), TO_DATE('2024-09-01', 'YYYY-MM-DD'), 1080, 16);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (13, TO_DATE('2023-10-01', 'YYYY-MM-DD'), TO_DATE('2024-10-01', 'YYYY-MM-DD'), 1140, 17);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (14, TO_DATE('2023-11-01', 'YYYY-MM-DD'), TO_DATE('2024-11-01', 'YYYY-MM-DD'), 1120, 18);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (15, TO_DATE('2023-12-01', 'YYYY-MM-DD'), TO_DATE('2024-12-01', 'YYYY-MM-DD'), 1090, 19);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (16, TO_DATE('2023-12-15', 'YYYY-MM-DD'), TO_DATE('2024-12-15', 'YYYY-MM-DD'), 1160, 20);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (17, TO_DATE('2023-01-05', 'YYYY-MM-DD'), TO_DATE('2024-01-05', 'YYYY-MM-DD'), 1170, 21);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (18, TO_DATE('2023-02-05', 'YYYY-MM-DD'), TO_DATE('2024-02-05', 'YYYY-MM-DD'), 1060, 22);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (19, TO_DATE('2023-03-05', 'YYYY-MM-DD'), TO_DATE('2024-03-05', 'YYYY-MM-DD'), 1190, 23);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (20, TO_DATE('2023-04-05', 'YYYY-MM-DD'), TO_DATE('2024-04-05', 'YYYY-MM-DD'), 1200, 24);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (21, TO_DATE('2023-05-05', 'YYYY-MM-DD'), TO_DATE('2024-05-05', 'YYYY-MM-DD'), 1040, 25);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (22, TO_DATE('2023-06-05', 'YYYY-MM-DD'), TO_DATE('2024-06-05', 'YYYY-MM-DD'), 1050, 1);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (23, TO_DATE('2023-07-05', 'YYYY-MM-DD'), TO_DATE('2024-07-05', 'YYYY-MM-DD'), 1180, 2);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (24, TO_DATE('2023-08-05', 'YYYY-MM-DD'), TO_DATE('2024-08-05', 'YYYY-MM-DD'), 1020, 3);
INSERT INTO lease (lease_id, lease_start_date, lease_end_date, rent_amt, ten_id) VALUES (25, TO_DATE('2023-09-05', 'YYYY-MM-DD'), TO_DATE('2024-09-05', 'YYYY-MM-DD'), 1010, 4);


## Pet 
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (1, 'Buddy', 1);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (2, 'Bella', 1);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (3, 'Charlie', 2);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (4, 'Luna', 2);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (5, 'Max', 3);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (6, 'Daisy', 4);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (7, 'Oliver', 4);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (8, 'Coco', 5);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (9, 'Molly', 6);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (10, 'Milo', 7);

INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (11, 'Lucy', 7);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (12, 'Tiger', 8);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (13, 'Rocky', 8);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (14, 'Sadie', 9);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (15, 'Bailey', 10);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (16, 'Leo', 10);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (17, 'Zoe', 11);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (18, 'Jasper', 11);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (19, 'Ruby', 12);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (20, 'Chloe', 13);

INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (21, 'Riley', 14);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (22, 'Rosie', 14);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (23, 'Duke', 15);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (24, 'Lily', 16);
INSERT INTO pet (PET_ID, PET_NAME, TEN_ID) VALUES (25, 'Oscar', 17);

## Payment 
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (1, TO_DATE('2023-01-10', 'YYYY-MM-DD'), 1200, 'Credit Card', 1);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (2, TO_DATE('2023-01-15', 'YYYY-MM-DD'), 900, 'Debit Card', 2);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (3, TO_DATE('2023-02-05', 'YYYY-MM-DD'), 1100, 'Bank Transfer', 3);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (4, TO_DATE('2023-02-12', 'YYYY-MM-DD'), 850, 'Credit Card', 4);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (5, TO_DATE('2023-03-03', 'YYYY-MM-DD'), 950, 'PayPal', 5);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (6, TO_DATE('2023-03-14', 'YYYY-MM-DD'), 1000, 'Credit Card', 6);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (7, TO_DATE('2023-04-05', 'YYYY-MM-DD'), 1050, 'Debit Card', 7);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (8, TO_DATE('2023-04-18', 'YYYY-MM-DD'), 1200, 'Bank Transfer', 8);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (9, TO_DATE('2023-05-10', 'YYYY-MM-DD'), 800, 'Credit Card', 9);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (10, TO_DATE('2023-05-25', 'YYYY-MM-DD'), 950, 'Debit Card', 10);

INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (11, TO_DATE('2023-06-10', 'YYYY-MM-DD'), 1000, 'Bank Transfer', 11);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (12, TO_DATE('2023-06-20', 'YYYY-MM-DD'), 1100, 'Credit Card', 12);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (13, TO_DATE('2023-07-05', 'YYYY-MM-DD'), 850, 'Debit Card', 13);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (14, TO_DATE('2023-07-15', 'YYYY-MM-DD'), 950, 'Bank Transfer', 14);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (15, TO_DATE('2023-08-02', 'YYYY-MM-DD'), 1150, 'Credit Card', 15);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (16, TO_DATE('2023-08-19', 'YYYY-MM-DD'), 900, 'Debit Card', 16);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (17, TO_DATE('2023-09-01', 'YYYY-MM-DD'), 1100, 'PayPal', 17);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (18, TO_DATE('2023-09-20', 'YYYY-MM-DD'), 950, 'Credit Card', 18);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (19, TO_DATE('2023-10-03', 'YYYY-MM-DD'), 1000, 'Bank Transfer', 19);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (20, TO_DATE('2023-10-18', 'YYYY-MM-DD'), 1200, 'Credit Card', 20);

INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (21, TO_DATE('2023-11-10', 'YYYY-MM-DD'), 1100, 'Debit Card', 21);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (22, TO_DATE('2023-11-25', 'YYYY-MM-DD'), 950, 'PayPal', 22);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (23, TO_DATE('2023-12-05', 'YYYY-MM-DD'), 1150, 'Bank Transfer', 23);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (24, TO_DATE('2023-12-20', 'YYYY-MM-DD'), 1000, 'Credit Card', 24);
INSERT INTO payment (PAY_ID, PAY_DATE, AMOUNT, PAYMENT_METHOD, LEASE_ID) VALUES (25, TO_DATE('2023-12-28', 'YYYY-MM-DD'), 900, 'Debit Card', 25);

# Payment Method
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (1, 'Debit Card', 'Bank of Example', 2.50, 1);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (2, 'Credit Card', 'Bank of Example', 2.75, 2);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (3, 'Bank Transfer', 'Bank of Example', 1.50, 3);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (4, 'Credit Card', 'Amex', 3.00, 4);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (5, 'Debit Card', 'Discover Bank', 2.00, 5);

INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (6, 'Debit Card', 'Bank of Example', 2.50, 6);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (7, 'Credit Card', 'Bank of Example', 2.75, 7);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (8, 'Bank Transfer', 'Bank of Example', 1.50, 8);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (9, 'Credit Card', 'Amex', 3.00, 9);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (10, 'Debit Card', 'Discover Bank', 2.00, 10);

INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (11, 'Debit Card', 'Bank of Example', 2.50, 11);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (12, 'Credit Card', 'Bank of Example', 2.75, 12);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (13, 'Bank Transfer', 'Bank of Example', 1.50, 13);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (14, 'Credit Card', 'Amex', 3.00, 14);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (15, 'Debit Card', 'Discover Bank', 2.00, 15);

INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (16, 'Debit Card', 'Bank of Example', 2.50, 16);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (17, 'Credit Card', 'Bank of Example', 2.75, 17);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (18, 'Bank Transfer', 'Bank of Example', 1.50, 18);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (19, 'Credit Card', 'Amex', 3.00, 19);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (20, 'Debit Card', 'Discover Bank', 2.00, 20);

INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (21, 'Debit Card', 'Bank of Example', 2.50, 21);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (22, 'Credit Card', 'Bank of Example', 2.75, 22);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (23, 'Bank Transfer', 'Bank of Example', 1.50, 23);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (24, 'Credit Card', 'Amex', 3.00, 24);
INSERT INTO PaymentMethod (pay_id, method_name, provider, transaction_fee, ten_id) VALUES (25, 'Debit Card', 'Discover Bank', 2.00, 25);



# THESE MUST BE FIXED. Amen_id must be 1-4 or 4-8 for private and public 
## Apartment Amenities 
INSERT ALL
-- apt 1 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (1, 1)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (1, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (1, 3)
-- apt 2 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (2, 1)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (2, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (2, 3)
-- apt 3 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (3, 1)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (3, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (3, 3)
-- apt 4 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (4, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (4, 4)
-- apt 5 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (5, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (5, 4)
-- apt 6 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (6, 3)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (6, 4)
-- apt 7 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (7, 3)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (7, 4)
-- apt 8 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (8, 1)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (8, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (8, 3)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (8, 4)
-- apt 9 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (9, 1)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (9, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (9, 3)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (9, 4)
-- apt 10 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (10, 1)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (10, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (10, 3)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (10, 4)
-- apt 11 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (11, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (11, 3)
-- apt 12 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (12, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (12, 3)
-- apt 13 amenities
INTO Apartment_Amenities (apt_id, amen_id) VALUES (13, 2)
INTO Apartment_Amenities (apt_id, amen_id) VALUES (13, 3)
SELECT 1 FROM DUAL;


## Property Amenities 
INSERT ALL
-- apt 1 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (1, 5)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (1, 6)
-- apt 2 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (2, 7)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (2, 8)
-- apt 3 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (3, 5)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (3, 6)
-- apt 4 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (4, 7)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (4, 8)
-- apt 5 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (5, 5)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (5, 6)
-- apt 6 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (6, 7)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (6, 8)
-- apt 7 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (7, 5)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (7, 6)
-- apt 8 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (8, 7)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (8, 8)
-- apt 9 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (9, 5)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (9, 6)
-- apt 10 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (10, 7)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (10, 8)
-- apt 11 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (11, 5)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (11, 6)
-- apt 12 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (12, 7)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (12, 8)
-- apt 13 amenities
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (13, 5)
INTO property_amenities (PROP_ID, AMEN_ID) VALUES (13, 6)
SELECT 1 FROM DUAL;







