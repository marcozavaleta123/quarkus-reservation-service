CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    dni VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    status_active BOOLEAN
);


CREATE TABLE professionals (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    dni VARCHAR(255),
    speciality VARCHAR(255),
    status_active BOOLEAN
);


CREATE TYPE booking_status AS ENUM (
    'CANCELADA',
    'COMPLETADA',
    'CREADA'
);


CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status booking_status NOT NULL,
    client_id BIGINT,
    professional_id BIGINT,
    CONSTRAINT fk_booking_client
        FOREIGN KEY (client_id)
        REFERENCES clients (id),
    CONSTRAINT fk_booking_professional
        FOREIGN KEY (professional_id)
        REFERENCES professionals (id)
);


CREATE TABLE schedules (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status BOOLEAN NOT NULL,
    professional_id BIGINT,
    CONSTRAINT fk_schedule_professional
        FOREIGN KEY (professional_id)
        REFERENCES professionals (id)
);


CREATE INDEX idx_booking_client ON bookings (client_id);
CREATE INDEX idx_booking_professional ON bookings (professional_id);
CREATE INDEX idx_schedule_professional ON schedules (professional_id);
