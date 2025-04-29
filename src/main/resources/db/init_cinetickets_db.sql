-- Script de inicialización para la base de datos CineTickets en PostgreSQL
-- Este script crea el esquema y carga datos iniciales para desarrollo

-- Crear extensión para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Eliminar tablas existentes si es necesario (en orden inverso de dependencia)
DROP TABLE IF EXISTS combo_items CASCADE;
DROP TABLE IF EXISTS combos CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS product_categories CASCADE;
DROP TABLE IF EXISTS promotions CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS reserved_seats CASCADE;
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS ticket_types CASCADE;
DROP TABLE IF EXISTS shows CASCADE;
DROP TABLE IF EXISTS seats CASCADE;
DROP TABLE IF EXISTS rooms CASCADE;
DROP TABLE IF EXISTS movies CASCADE;
DROP TABLE IF EXISTS cinemas CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Crear tablas en orden de dependencia

-- Tabla de Usuarios
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    profile_image VARCHAR(255),
    loyalty_points INTEGER DEFAULT 0,
    auth_provider VARCHAR(50),
    auth_provider_id VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    last_login TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    marketing_consent BOOLEAN DEFAULT FALSE
);

-- Tabla de Cines
CREATE TABLE cinemas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    website VARCHAR(255),
    logo_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Salas
CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cinema_id UUID NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    is_accessible BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    UNIQUE(cinema_id, name)
);

-- Tabla de Asientos
CREATE TABLE seats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    row VARCHAR(10) NOT NULL,
    number VARCHAR(10) NOT NULL,
    seat_type VARCHAR(50) DEFAULT 'REGULAR',
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    UNIQUE(room_id, row, number)
);

-- Tabla de Películas
CREATE TABLE movies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255),
    synopsis TEXT,
    duration_minutes INTEGER NOT NULL,
    release_date DATE,
    end_date DATE,
    director VARCHAR(255),
    cast TEXT,
    genre VARCHAR(100),
    rating VARCHAR(20),
    poster_url VARCHAR(255),
    trailer_url VARCHAR(255),
    language VARCHAR(50),
    is_3d BOOLEAN DEFAULT FALSE,
    is_subtitled BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Funciones (Shows)
CREATE TABLE shows (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    movie_id UUID NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    is_3d BOOLEAN DEFAULT FALSE,
    is_subtitled BOOLEAN DEFAULT FALSE,
    language VARCHAR(50),
    status VARCHAR(20) DEFAULT 'SCHEDULED' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Tipos de Entrada
CREATE TABLE ticket_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cinema_id UUID NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Reservas
CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    show_id UUID NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Asientos Reservados
CREATE TABLE reserved_seats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reservation_id UUID NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    seat_id UUID NOT NULL REFERENCES seats(id) ON DELETE CASCADE,
    ticket_type_id UUID NOT NULL REFERENCES ticket_types(id),
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    UNIQUE(reservation_id, seat_id)
);

-- Tabla de Categorías de Productos
CREATE TABLE product_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cinema_id UUID NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Productos
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID NOT NULL REFERENCES product_categories(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    stock INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Combos
CREATE TABLE combos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cinema_id UUID NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Items de Combos
CREATE TABLE combo_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    combo_id UUID NOT NULL REFERENCES combos(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Órdenes
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    reservation_id UUID REFERENCES reservations(id),
    operator_id UUID REFERENCES users(id),
    subtotal DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) DEFAULT 0,
    tax DECIMAL(10, 2) DEFAULT 0,
    total DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50),
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_reference VARCHAR(255),
    qr_code VARCHAR(255),
    order_type VARCHAR(20) NOT NULL,
    notes TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Items de Órdenes
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    item_type VARCHAR(20) NOT NULL,
    item_id UUID NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Tabla de Promociones
CREATE TABLE promotions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cinema_id UUID NOT NULL REFERENCES cinemas(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    code VARCHAR(50) UNIQUE,
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    usage_limit INTEGER,
    usage_count INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    applies_to VARCHAR(50),
    min_purchase DECIMAL(10, 2) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- INSERCIÓN DE DATOS DE PRUEBA

-- Insertar usuarios de prueba
INSERT INTO users (id, email, password_hash, first_name, last_name, role, status, created_at, updated_at)
VALUES 
    (uuid_generate_v4(), 'admin@example.com', '$2a$10$yX5CY4xECiy5S4H7iwdRB.9gMVZ6HUWvFzN2jvQJWltF1JpVOe53S', 'Admin', 'User', 'ADMIN', 'ACTIVE', NOW(), NOW()),
    (uuid_generate_v4(), 'staff@example.com', '$2a$10$4QZJfHnlKrjHC9ow.oRhzeD6r/R/w5uXXnA.r2vUizYA3kHUBN4du', 'Staff', 'User', 'STAFF', 'ACTIVE', NOW(), NOW()),
    (uuid_generate_v4(), 'customer@example.com', '$2a$10$3yPH93QYzAWZOnEI7rlkPOaQAvHYAoSdJVDqxCv0iQRUX9j15.S5K', 'Cliente', 'Regular', 'CUSTOMER', 'ACTIVE', NOW(), NOW());

-- Insertar cine
INSERT INTO cinemas (id, name, address, city, state, postal_code, phone, email, website, active, created_at, updated_at)
VALUES 
    (uuid_generate_v4(), 'Cine Annuar Shopping', 'Av. Principal 123', 'San Miguel de Tucumán', 'Tucumán', '4000', '381-1234567', 'info@cineannuar.com', 'https://cineannuar.com', TRUE, NOW(), NOW());

-- Obtener ID del cine para referencias
DO $$
DECLARE
    cinema_id UUID;
BEGIN
    SELECT id INTO cinema_id FROM cinemas LIMIT 1;

    -- Insertar salas
    WITH inserted_rooms AS (
        INSERT INTO rooms (id, cinema_id, name, capacity, room_type, is_accessible, status, created_at, updated_at)
        VALUES 
            (uuid_generate_v4(), cinema_id, 'Sala 1', 100, 'REGULAR', TRUE, 'ACTIVE', NOW(), NOW()),
            (uuid_generate_v4(), cinema_id, 'Sala 2 (3D)', 80, 'THREE_D', TRUE, 'ACTIVE', NOW(), NOW()),
            (uuid_generate_v4(), cinema_id, 'Sala VIP', 50, 'VIP', TRUE, 'ACTIVE', NOW(), NOW())
        RETURNING id, name
    )
    -- Insertar asientos para cada sala
    SELECT id, name INTO TEMP TABLE temp_rooms FROM inserted_rooms;

    -- Crear asientos para Sala 1
    FOR room_rec IN SELECT * FROM temp_rooms WHERE name = 'Sala 1' LOOP
        FOR i IN 1..10 LOOP -- 10 filas
            FOR j IN 1..10 LOOP -- 10 asientos por fila
                INSERT INTO seats (room_id, row, number, seat_type, status, created_at, updated_at)
                VALUES (
                    room_rec.id,
                    CHR(64 + i), -- Convierte número a letra (1=A, 2=B, etc.)
                    j::TEXT,
                    CASE 
                        WHEN i = 1 AND (j = 1 OR j = 10) THEN 'ACCESSIBLE'
                        WHEN i >= 5 AND i <= 6 THEN 'VIP'
                        ELSE 'REGULAR'
                    END,
                    'ACTIVE',
                    NOW(),
                    NOW()
                );
            END LOOP;
        END LOOP;
    END LOOP;

    -- Crear asientos para Sala 2
    FOR room_rec IN SELECT * FROM temp_rooms WHERE name = 'Sala 2 (3D)' LOOP
        FOR i IN 1..8 LOOP -- 8 filas
            FOR j IN 1..10 LOOP -- 10 asientos por fila
                INSERT INTO seats (room_id, row, number, seat_type, status, created_at, updated_at)
                VALUES (
                    room_rec.id,
                    CHR(64 + i),
                    j::TEXT,
                    CASE 
                        WHEN i = 1 AND (j = 1 OR j = 10) THEN 'ACCESSIBLE'
                        WHEN i >= 4 AND i <= 5 THEN 'VIP'
                        ELSE 'REGULAR'
                    END,
                    'ACTIVE',
                    NOW(),
                    NOW()
                );
            END LOOP;
        END LOOP;
    END LOOP;

    -- Crear asientos para Sala VIP
    FOR room_rec IN SELECT * FROM temp_rooms WHERE name = 'Sala VIP' LOOP
        FOR i IN 1..5 LOOP -- 5 filas
            FOR j IN 1..10 LOOP -- 10 asientos por fila
                INSERT INTO seats (room_id, row, number, seat_type, status, created_at, updated_at)
                VALUES (
                    room_rec.id,
                    CHR(64 + i),
                    j::TEXT,
                    CASE 
                        WHEN i = 1 AND (j = 1 OR j = 10) THEN 'ACCESSIBLE'
                        ELSE 'VIP'
                    END,
                    'ACTIVE',
                    NOW(),
                    NOW()
                );
            END LOOP;
        END LOOP;
    END LOOP;

    -- Insertar películas
    INSERT INTO movies (id, title, original_title, synopsis, duration_minutes, release_date, end_date, director, cast, genre, rating, poster_url, trailer_url, language, is_3d, is_subtitled, status, created_at, updated_at)
    VALUES 
        (uuid_generate_v4(), 'Dune: Parte Dos', 'Dune: Part Two', 'Paul Atreides se une a los Fremen y comienza un viaje espiritual y político para vengar a su familia destruida, mientras trata de prevenir un terrible futuro.', 166, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '30 days', 'Denis Villeneuve', 'Timothée Chalamet, Zendaya, Rebecca Ferguson, Javier Bardem', 'Ciencia Ficción, Aventura', '13+', 'https://via.placeholder.com/400x600?text=Dune+2', 'https://youtube.com/watch?v=dummylink', 'Inglés', TRUE, TRUE, 'ACTIVE', NOW(), NOW()),
        (uuid_generate_v4(), 'Deadpool & Wolverine', 'Deadpool & Wolverine', 'Wade Wilson sale del retiro para unir fuerzas con Wolverine para enfrentar una amenaza común.', 127, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '45 days', 'Shawn Levy', 'Ryan Reynolds, Hugh Jackman', 'Acción, Comedia', '17+', 'https://via.placeholder.com/400x600?text=Deadpool+Wolverine', 'https://youtube.com/watch?v=dummylink2', 'Inglés', TRUE, TRUE, 'ACTIVE', NOW(), NOW()),
        (uuid_generate_v4(), 'Joker: Folie à Deux', 'Joker: Folie à Deux', 'Secuela del aclamado film que sigue las aventuras de Arthur Fleck.', 142, CURRENT_DATE + INTERVAL '15 days', CURRENT_DATE + INTERVAL '75 days', 'Todd Phillips', 'Joaquin Phoenix, Lady Gaga', 'Drama, Crimen', '17+', 'https://via.placeholder.com/400x600?text=Joker+2', 'https://youtube.com/watch?v=dummylink3', 'Inglés', FALSE, TRUE, 'UPCOMING', NOW(), NOW());

    -- Insertar tipos de entradas
    INSERT INTO ticket_types (id, cinema_id, name, description, price, is_active, created_at, updated_at)
    VALUES 
        (uuid_generate_v4(), cinema_id, 'General', 'Entrada estándar para todas las funciones', 1200.00, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'Jubilados/Estudiantes', 'Tarifa reducida para jubilados y estudiantes', 900.00, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'VIP', 'Entrada para sala VIP con asientos reclinables', 1800.00, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'General 3D', 'Entrada para funciones en 3D', 1500.00, TRUE, NOW(), NOW());

    -- Obtener IDs de películas y salas para crear funciones
    SELECT array_agg(id) INTO TEMP TABLE movie_ids FROM movies;
    SELECT id, name INTO TEMP TABLE room_data FROM rooms;

    -- Crear funciones para las películas activas
    -- Obtener IDs de películas activas
    WITH active_movies AS (
        SELECT id FROM movies WHERE status = 'ACTIVE'
    )
    -- Insertar funciones para la próxima semana
    INSERT INTO shows (movie_id, room_id, start_time, end_time, is_3d, is_subtitled, language, status, created_at, updated_at)
    SELECT 
        m.id,
        r.id,
        -- Horarios para cada día (hoy y mañana)
        d + h AS start_time,
        -- Calcular end_time basado en la duración de la película
        d + h + ((SELECT duration_minutes FROM movies WHERE id = m.id) * interval '1 minute') AS end_time,
        -- Funciones en 3D solo en Sala 2
        CASE WHEN r.name = 'Sala 2 (3D)' THEN TRUE ELSE FALSE END AS is_3d,
        -- Alterna entre subtitulado y doblado
        CASE WHEN extract(hour from h) % 2 = 0 THEN TRUE ELSE FALSE END AS is_subtitled,
        'Inglés',
        'SCHEDULED',
        NOW(),
        NOW()
    FROM 
        (SELECT id FROM active_movies) m
    CROSS JOIN
        (SELECT id, name FROM room_data) r
    CROSS JOIN
        (SELECT generate_series(0, 1) * interval '1 day' AS d) days
    CROSS JOIN
        (
            SELECT 
                CASE 
                    WHEN name = 'Sala 1' THEN make_time(14, 30, 0)::time
                    WHEN name = 'Sala 2 (3D)' THEN make_time(16, 45, 0)::time
                    ELSE make_time(15, 0, 0)::time
                END AS h
            FROM room_data
            UNION
            SELECT 
                CASE 
                    WHEN name = 'Sala 1' THEN make_time(19, 0, 0)::time
                    WHEN name = 'Sala 2 (3D)' THEN make_time(21, 15, 0)::time
                    ELSE make_time(20, 0, 0)::time
                END AS h
            FROM room_data
        ) hours
    WHERE 
        -- Evitar conflictos de programación (muy simplificado)
        (r.name = 'Sala 1' AND m.id = (SELECT id FROM movies WHERE title = 'Dune: Parte Dos')) OR
        (r.name = 'Sala 2 (3D)' AND extract(hour from h) < 18 AND m.id = (SELECT id FROM movies WHERE title = 'Deadpool & Wolverine')) OR
        (r.name = 'Sala 2 (3D)' AND extract(hour from h) >= 18 AND m.id = (SELECT id FROM movies WHERE title = 'Dune: Parte Dos')) OR
        (r.name = 'Sala VIP' AND m.id = (SELECT id FROM movies WHERE title = 'Deadpool & Wolverine'));

    -- Insertar categorías de productos
    INSERT INTO product_categories (id, cinema_id, name, description, image_url, display_order, is_active, created_at, updated_at)
    VALUES 
        (uuid_generate_v4(), cinema_id, 'Pochoclos', 'Pochoclos frescos en diferentes tamaños', 'https://via.placeholder.com/200x200?text=Pochoclos', 1, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'Bebidas', 'Refrescos, agua y otras bebidas', 'https://via.placeholder.com/200x200?text=Bebidas', 2, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'Dulces', 'Caramelos, chocolates y golosinas', 'https://via.placeholder.com/200x200?text=Dulces', 3, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'Snacks', 'Nachos, papas fritas y otros snacks salados', 'https://via.placeholder.com/200x200?text=Snacks', 4, TRUE, NOW(), NOW());

    -- Obtener IDs de categorías
    WITH category_ids AS (
        SELECT id, name FROM product_categories
    )
    -- Insertar productos
    INSERT INTO products (id, category_id, name, description, price, image_url, stock, is_active, created_at, updated_at)
    VALUES 
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Pochoclos'), 'Pochoclo Chico', 'Pochoclo en balde pequeño', 400.00, 'https://via.placeholder.com/200x200?text=Pochoclo+Chico', 100, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Pochoclos'), 'Pochoclo Mediano', 'Pochoclo en balde mediano', 500.00, 'https://via.placeholder.com/200x200?text=Pochoclo+Mediano', 100, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Pochoclos'), 'Pochoclo Grande', 'Pochoclo en balde grande', 600.00, 'https://via.placeholder.com/200x200?text=Pochoclo+Grande', 100, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Bebidas'), 'Gaseosa Chica', 'Vaso de 350ml', 350.00, 'https://via.placeholder.com/200x200?text=Gaseosa+Chica', 200, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Bebidas'), 'Gaseosa Mediana', 'Vaso de 500ml', 400.00, 'https://via.placeholder.com/200x200?text=Gaseosa+Mediana', 200, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Bebidas'), 'Gaseosa Grande', 'Vaso de 750ml', 500.00, 'https://via.placeholder.com/200x200?text=Gaseosa+Grande', 200, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Bebidas'), 'Agua Mineral', 'Botella de 500ml', 300.00, 'https://via.placeholder.com/200x200?text=Agua+Mineral', 200, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Dulces'), 'Barra de Chocolate', 'Chocolate con leche', 350.00, 'https://via.placeholder.com/200x200?text=Chocolate', 150, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Dulces'), 'Bolsa de Caramelos', 'Caramelos surtidos', 300.00, 'https://via.placeholder.com/200x200?text=Caramelos', 150, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Snacks'), 'Nachos con Queso', 'Totopos con salsa de queso', 550.00, 'https://via.placeholder.com/200x200?text=Nachos', 80, TRUE, NOW(), NOW()),
        (uuid_generate_v4(), (SELECT id FROM category_ids WHERE name = 'Snacks'), 'Papas Fritas', 'Bolsa de papas fritas', 400.00, 'https://via.placeholder.com/200x200?text=Papas+Fritas', 100, TRUE, NOW(), NOW());

    -- Insertar combos
    -- Combo Individual
    WITH combo_insert AS (
        INSERT INTO combos (id, cinema_id, name, description, price, image_url, is_active, created_at, updated_at)
        VALUES (
            uuid_generate_v4(), 
            cinema_id, 
            'Combo Individual', 
            'Pochoclo mediano y gaseosa mediana', 
            850.00, 
            'https://via.placeholder.com/300x200?text=Combo+Individual', 
            TRUE, 
            NOW(), 
            NOW()
        )
        RETURNING id
    )
    -- Agregar items al combo individual
    INSERT INTO combo_items (combo_id, product_id, quantity, created_at, updated_at)
    SELECT 
        (SELECT id FROM combo_insert),
        products.id,
        1,
        NOW(),
        NOW()
    FROM products
    WHERE name IN ('Pochoclo Mediano', 'Gaseosa Mediana');

    -- Combo Pareja
    WITH combo_insert AS (
        INSERT INTO combos (id, cinema_id, name, description, price, image_url, is_active, created_at, updated_at)
        VALUES (
            uuid_generate_v4(), 
            cinema_id, 
            'Combo Pareja', 
            'Pochoclo grande, 2 gaseosas medianas y chocolate', 
            1400.00, 
            'https://via.placeholder.com/300x200?text=Combo+Pareja', 
            TRUE, 
            NOW(), 
            NOW()
        )
        RETURNING id
    )
    -- Agregar items al combo pareja
    INSERT INTO combo_items (combo_id, product_id, quantity, created_at, updated_at)
    SELECT 
        (SELECT id FROM combo_insert),
        products.id,
        CASE 
            WHEN products.name = 'Gaseosa Mediana' THEN 2
            ELSE 1
        END,
        NOW(),
        NOW()
    FROM products
    WHERE name IN ('Pochoclo Grande', 'Gaseosa Mediana', 'Barra de Chocolate');

    -- Combo Familiar
    WITH combo_insert AS (
        INSERT INTO combos (id, cinema_id, name, description, price, image_url, is_active, created_at, updated_at)
        VALUES (
            uuid_generate_v4(), 
            cinema_id, 
            'Combo Familiar', 
            '2 pochoclos grandes, 4 gaseosas medianas, nachos', 
            2200.00, 
            'https://via.placeholder.com/300x200?text=Combo+Familiar', 
            TRUE, 
            NOW(), 
            NOW()
        )
        RETURNING id
    )
    -- Agregar items al combo familiar
    INSERT INTO combo_items (combo_id, product_id, quantity, created_at, updated_at)
    SELECT 
        (SELECT id FROM combo_insert),
        products.id,
        CASE 
            WHEN products.name = 'Pochoclo Grande' THEN 2
            WHEN products.name = 'Gaseosa Mediana' THEN 4
            ELSE 1
        END,
        NOW(),
        NOW()
    FROM products
    WHERE name IN ('Pochoclo Grande', 'Gaseosa Mediana', 'Nachos con Queso');

    -- Insertar promociones
    INSERT INTO promotions (id, cinema_id, name, description, discount_type, discount_value, code, start_date, end_date, usage_limit, usage_count, is_active, applies_to, min_purchase, created_at, updated_at)
    VALUES 
        (uuid_generate_v4(), cinema_id, '2x1 Martes', 'Compra una entrada y lleva otra gratis todos los martes', 'PERCENTAGE', 50.00, 'MARTES2X1', NOW() - INTERVAL '30 days', NOW() + INTERVAL '180 days', NULL, 0, TRUE, 'TICKETS', 0.00, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'Descuento Estudiantes', '20% de descuento presentando credencial de estudiante', 'PERCENTAGE', 20.00, 'ESTUDIANTE20', NOW() - INTERVAL '60 days', NOW() + INTERVAL '365 days', NULL, 0, TRUE, 'TICKETS', 0.00, NOW(), NOW()),
        (uuid_generate_v4(), cinema_id, 'Descuento en Combos', '$500 de descuento en combos', 'FIXED_AMOUNT', 500.00, 'COMBO500', NOW(), NOW() + INTERVAL '30 days', 100, 0, TRUE, 'COMBOS', 1000.00, NOW(), NOW());

END $$;

-- Mensaje final
SELECT 'Database initialized successfully' as message;