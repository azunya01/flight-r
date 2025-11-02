create table employee
(
    EmployeeID char(10)                     not null
        primary key,
    Name       varchar(20)                  not null,
    Phone      char(11)                     null,
    Role       varchar(10) default '查询员' null,
    check (`Role` in (_gbk\'²éÑ¯Ô±\',_gbk\'¹ÜÀíÔ±\'))
)
comment '员工信息表（无外键）';

create table flight
(
    FlightID      char(10)       not null
        primary key,
    DepartureCity varchar(20)    not null,
    ArrivalCity   varchar(20)    not null,
    DepartureTime datetime       not null,
    ArrivalTime   datetime       not null,
    BasePrice     decimal(10, 2) null,
    check (`DepartureCity` <> `ArrivalCity`)
)
    comment '航班信息（无外键）';

create index idx_flight_city
    on flight (DepartureCity, ArrivalCity);

create index idx_flight_time
    on flight (DepartureTime);

create table flight_seat
(
    FlightID        char(10)      not null,
    SeatTypeID      int           not null,
    PriceMultiplier decimal(8, 2) null,
    AvailableSeats  int           null,
    TotalSeats      int           null,
    SeatName        varchar(100)  not null,
    primary key (FlightID, SeatTypeID)
)
    comment '航班舱位价格余票（无外键）';

create table flight_seat_inventory
(
    ID          bigint auto_increment
        primary key,
    FlightID    char(10)                                                                  not null,
    SeatTypeID  char(5)                                                                   not null,
    seatNo      varchar(6)                                                                not null,
    Status      enum ('AVAILABLE', 'HELD', 'BOOKED', 'BLOCKED') default 'AVAILABLE'       not null,
    OrderID     bigint                                                                    null,
    PassengerID bigint                                                                    null,
    CreatedAT   datetime                                        default CURRENT_TIMESTAMP not null,
    UpdatedAT   datetime                                        default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint uk_flight_seat
        unique (FlightID, seatNo)
)
    comment '逐个座位清单（表示 seat_no）';

create index idx_fsi_flight_type_status
    on flight_seat_inventory (FlightID, SeatTypeID, Status);

create table `order`
(
    OrderID      int auto_increment
        primary key,
    FlightID     char(10)                     null,
    Status       varchar(20)                  not null,
    CreateAt     datetime                     null,
    UpdateAt     datetime                     null,
    UserID       int                          not null,
    TotalPrice   decimal(10, 2)               null,
    PayStatus    varchar(20) default 'UNPAID' not null,
    PayAmount    decimal(10, 2)               null comment '实付金额',
    PayChannel   varchar(20)                  null comment '支付渠道 ALIPAY/WECHAT/CARD',
    PaidAt       datetime                     null comment '支付时间',
    CancelReason varchar(100)                 null comment '取消原因',
    RebookFrom   int                          null comment '改签来源订单',
    RebookTo     int                          null comment '改签到的新订单',
    check (`PayStatus` in (_utf8mb4\'UNPAID\',_utf8mb4\'PAID\',_utf8mb4\'REFUNDING\',_utf8mb4\'REFUNDED\',_utf8mb4\'PARTIAL_REFUNDED\',_utf8mb4\'FAILED\')),
	check (`Status` in (_utf8mb4\'HOLD\',_utf8mb4\'PAID\',_utf8mb4\'CANCELED\',_utf8mb4\'REBOOKED\'))
)
comment '订单信息（无外键）';

create index idx_order_fid
    on `order` (FlightID);

create index idx_order_flight
    on `order` (FlightID);

create index idx_order_id
    on `order` (OrderID);

create index idx_order_status
    on `order` (Status);

create table passenger
(
    PassengerID bigint auto_increment comment '乘客主键'
        primary key,
    UserID      bigint                                   not null comment '归属用户ID（登录用户）',
    OrderID     int                                      not null comment '订单ID',
    Name        varchar(20)                              not null,
    IDNumber    char(18)                                 not null,
    Gender      char                                     not null,
    phone       varchar(20)                              not null,
    SeatTypeID  int                                      not null,
    price       decimal(10, 2) default 0.00              null,
    CreatedAt   datetime       default CURRENT_TIMESTAMP not null,
    UpdatedAt   datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint uk_passenger_order_idno
        unique (OrderID, IDNumber),
    check (`Gender` in (_utf8mb4\'ç·\',_utf8mb4\'å¥³\'))
)
comment '乘客信息（无外键）';

create index idx_passenger_order
    on passenger (OrderID);

create index idx_passenger_user
    on passenger (UserID);

create definer = root@localhost trigger trg_passenger_ai_sum_order
    after insert
    on passenger
    for each row
BEGIN
    IF NEW.OrderID IS NOT NULL THEN
        UPDATE `order` o
        SET o.TotalPrice = (
            SELECT ROUND(COALESCE(SUM(p.price),0), 2)
            FROM passenger p
            WHERE p.OrderID = o.OrderID
        )
        WHERE o.OrderID = NEW.OrderID;
    END IF;
END;

create definer = root@localhost trigger trg_passenger_au_sum_order
    after update
    on passenger
    for each row
BEGIN
    IF NEW.OrderID IS NOT NULL THEN
        UPDATE `order` o
        SET o.TotalPrice = (
            SELECT ROUND(COALESCE(SUM(p.price),0), 2)
            FROM passenger p
            WHERE CAST(p.OrderID AS UNSIGNED) = o.OrderID
        )
        WHERE o.OrderID = CAST(NEW.OrderID AS UNSIGNED);
    END IF;

    IF OLD.OrderID IS NOT NULL AND (NEW.OrderID <> OLD.OrderID) THEN
        UPDATE `order` o
        SET o.TotalPrice = (
            SELECT ROUND(COALESCE(SUM(p.price),0), 2)
            FROM passenger p
            WHERE CAST(p.OrderID AS UNSIGNED) = o.OrderID
        )
        WHERE o.OrderID = CAST(OLD.OrderID AS UNSIGNED);
    END IF;
END;

create definer = root@localhost trigger trg_passenger_bi_calc_price
    before insert
    on passenger
    for each row
BEGIN
    IF NEW.OrderID IS NOT NULL AND NEW.SeatTypeID IS NOT NULL THEN
        SET NEW.price = (
            SELECT ROUND(COALESCE(f.BasePrice,0) * COALESCE(fs.priceMultiplier,1), 2)
            FROM `order` o
                     JOIN flight f       ON f.FlightID = o.FlightID
                     JOIN flight_seat fs ON fs.FlightID = o.FlightID
                AND fs.SeatTypeID = CAST(NEW.SeatTypeID AS CHAR(5))
            WHERE o.OrderID = CAST(NEW.OrderID AS UNSIGNED)
            LIMIT 1
        );
    ELSE
        SET NEW.price = 0.00;
    END IF;
END;

create definer = root@localhost trigger trg_passenger_bu_calc_price
    before update
    on passenger
    for each row
BEGIN
    IF (NEW.OrderID <> OLD.OrderID) OR (NEW.SeatTypeID <> OLD.SeatTypeID) THEN
        IF NEW.OrderID IS NOT NULL AND NEW.SeatTypeID IS NOT NULL THEN
            SET NEW.price = (
                SELECT ROUND(COALESCE(f.BasePrice,0) * COALESCE(fs.priceMultiplier,1), 2)
                FROM `order` o
                         JOIN flight f       ON f.FlightID = o.FlightID
                         JOIN flight_seat fs ON fs.FlightID = o.FlightID
                    AND fs.SeatTypeID = CAST(NEW.SeatTypeID AS CHAR(5))
                WHERE o.OrderID = CAST(NEW.OrderID AS UNSIGNED)
                LIMIT 1
            );
        ELSE
            SET NEW.price = 0.00;
        END IF;
    END IF;
END;

create definer = root@localhost trigger trg_pax_ad_seat_plus1
    after delete
    on passenger
    for each row
BEGIN
    IF OLD.OrderID IS NOT NULL AND OLD.SeatTypeID IS NOT NULL THEN
        UPDATE flight_seat fs
            JOIN `order` o ON o.OrderID = CAST(OLD.OrderID AS UNSIGNED)
                AND o.FlightID = fs.FlightID
        SET fs.AvailableSeats = LEAST(fs.AvailableSeats + 1, fs.TotalSeats)
        WHERE fs.SeatTypeID = CAST(OLD.SeatTypeID AS CHAR(5));
    END IF;
END;

create definer = root@localhost trigger trg_pax_ai_seat_minus1
    after insert
    on passenger
    for each row
BEGIN
    IF NEW.OrderID IS NOT NULL AND NEW.SeatTypeID IS NOT NULL THEN
        UPDATE flight_seat fs
            JOIN `order` o ON o.OrderID = CAST(NEW.OrderID AS UNSIGNED)
                AND o.FlightID = fs.FlightID
        SET fs.AvailableSeats = GREATEST(fs.AvailableSeats - 1, 0)
        WHERE fs.SeatTypeID = CAST(NEW.SeatTypeID AS CHAR(5));
    END IF;
END;

create definer = root@localhost trigger trg_pax_au_seat_adjust
    after update
    on passenger
    for each row
BEGIN
    IF (NEW.OrderID <> OLD.OrderID) OR (NEW.SeatTypeID <> OLD.SeatTypeID) THEN
        IF OLD.OrderID IS NOT NULL AND OLD.SeatTypeID IS NOT NULL THEN
            UPDATE flight_seat fs
                JOIN `order` o ON o.OrderID = CAST(OLD.OrderID AS UNSIGNED)
                    AND o.FlightID = fs.FlightID
            SET fs.AvailableSeats = LEAST(fs.AvailableSeats + 1, fs.TotalSeats)
            WHERE fs.SeatTypeID = CAST(OLD.SeatTypeID AS CHAR(5));
        END IF;

        IF NEW.OrderID IS NOT NULL AND NEW.SeatTypeID IS NOT NULL THEN
            UPDATE flight_seat fs
                JOIN `order` o ON o.OrderID = CAST(NEW.OrderID AS UNSIGNED)
                    AND o.FlightID = fs.FlightID
            SET fs.AvailableSeats = GREATEST(fs.AvailableSeats - 1, 0)
            WHERE fs.SeatTypeID = CAST(NEW.SeatTypeID AS CHAR(5));
        END IF;
    END IF;
END;

create table rebook
(
    RebookID    char(10) not null
        primary key,
    OrderID     char(10) null,
    OperatorID  char(10) null,
    OldFlightID char(10) null,
    NewFlightID char(10) null,
    RebookTime  datetime null
)
    comment '改签记录（无外键）';

create table seattype
(
    SeatTypeID    char(5)       not null
        primary key,
    SeatTypeName  varchar(20)   null,
    PriceModifier decimal(3, 2) null
)
    comment '舱位类型（无外键）';

create table user
(
    id       int auto_increment comment '主键ID'
        primary key,
    username varchar(255) null comment '用户名',
    password varchar(255) null comment '密码',
    name     varchar(255) null comment '姓名',
    role     varchar(255) null comment '角色',
    phone    varchar(255) null comment '电话',
    sex      varchar(20)  null,
    idNumber varchar(20)  null
)
    comment '用户信息表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

