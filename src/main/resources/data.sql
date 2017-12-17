insert into customer (id, firstname, lastname, email, phone) values (default, 'Duglas', 'Costa', 'duglas-costa@gmail.com', '+380-637-5413');
insert into customer (id, firstname, lastname, email, phone) values (default, 'Jerar', 'Pike', 'jerar.pike@gmail.com', '+380-512-1718');
insert into customer (id, firstname, lastname, email, phone) values (default, 'Andrey', 'Pyatov', 'andrey_pyatov@gmail.com', '+380-724-8251');
insert into customer (id, firstname, lastname, email, phone) values (default, 'David', 'Vilia', 'david.vilia@yahoo.com', '+380-624-9369');
insert into customer (id, firstname, lastname, email, phone) values (default, 'David', 'Alama', 'david.alama@gmail.com', '+926-168-8471');
insert into customer (id, firstname, lastname, email, phone) values (default, 'Tomas', 'Muller', 'tomas.muller@yandex.com', '+836-873-9573');
insert into customer (id, firstname, lastname, email, phone) values (default, 'Robert', 'Levandovski', 'robert.levandovski@gmail.com', '+343-888-1718');

insert into employee (id, firstname, lastname, email, phone) values (default, 'Sesk', 'Fabrigas', 'sesk_fabrigas@gmail.com', '+380-333-2013');
insert into employee (id, firstname, lastname, email, phone) values (default, 'Samuel', 'Untity', 'samuel.untity@gmail.com', '+380-624-9363');
insert into employee (id, firstname, lastname, email, phone) values (default, 'Frank', 'Ribery', 'frank.ribary@yahoo.com', '+234-873-9992');
insert into employee (id, firstname, lastname, email, phone) values (default, 'Alexandr', 'Kokorin', 'alexands.kokorin@yandex.com', '+563-314-9269');
insert into employee (id, firstname, lastname, email, phone) values (default, 'Andrey', 'Shevchenko', 'andrey.shevchenko@gmail.com', '+834-835-7242');
insert into employee (id, firstname, lastname, email, phone) values (default, 'Andrey', 'Arshavin', 'andrey.arshavin@yandex.com', '+324-723-1523');
insert into employee (id, firstname, lastname, email, phone) values (default, 'Bast', 'Dost', 'bast.dost@yahoo.com', '+678-936-9080');

insert into lead (id, customer_id, start, end, type, status, origin_address, destination_address) values (default, 1, '2017-12-15T13:30:00', '2017-12-15T13:30:00', 'LOCAL', 'ASSIGNED', '123, Brick st., LA', '123, Mac st., LA');
insert into lead (id, customer_id, start, end, type, status, origin_address, destination_address) values (default, 2, '2017-12-15T13:30:00', '2017-12-15T13:30:00', 'DISTANCE', 'CONVERTED', '27, Tree st., LA', '413, Apple st., LA');
insert into lead (id, customer_id, start, end, type, status, origin_address, destination_address) values (default, 3, '2017-12-15T13:30:00', '2017-12-15T13:30:00', 'DISTANCE', 'CLOSED', '27, Cherry st., LA', '413, Villon st., LA');
insert into lead (id, customer_id, start, end, type, status, origin_address, destination_address) values (default, 4, '2017-12-15T13:30:00', '2017-12-15T13:30:00', 'DISTANCE', 'CONVERTED', '42, Marshmallow st., LA', '15, Cherry st., LA');
insert into lead (id, customer_id, start, end, type, status, origin_address, destination_address) values (default, 5, '2017-12-15T13:30:00', '2017-12-15T13:30:00', 'DISTANCE', 'CONVERTED', '27, Pine st., LA', '413, Orange st., LA');
insert into lead (id, customer_id, start, end, type, status, origin_address, destination_address) values (default, 6, '2017-12-15T13:30:00', '2017-12-15T13:30:00', 'LOCAL', 'ASSIGNED', '15, Glass st., LA', '73, Sand st., LA');
insert into lead (id, customer_id, start, end, type, status, origin_address, destination_address) values (default, 7, '2017-12-15T13:30:00', '2017-12-15T13:30:00', 'DISTANCE', 'CONVERTED', '27, Tree st., LA', '413, Oak st., LA');

insert into lead_employee (lead_id, employee_id) values (1, 1);
insert into lead_employee (lead_id, employee_id) values (2, 2);
insert into lead_employee (lead_id, employee_id) values (3, 3);
insert into lead_employee (lead_id, employee_id) values (4, 4);
insert into lead_employee (lead_id, employee_id) values (5, 5);
insert into lead_employee (lead_id, employee_id) values (6, 6);
insert into lead_employee (lead_id, employee_id) values (7, 7);

insert into lead_estimates (lead_id, name, quantity, price) values (1, 'Packing Paper', 1, 120);
insert into lead_estimates (lead_id, name, quantity, price) values (1, 'Big Box', 3, 175);
insert into lead_estimates (lead_id, name, quantity, price) values (2, 'Packing Stripe', 1, 100);
insert into lead_estimates (lead_id, name, quantity, price) values (2, 'Small Box', 1, 125);
insert into lead_estimates (lead_id, name, quantity, price) values (3, 'Velcro', 1, 10);
insert into lead_estimates (lead_id, name, quantity, price) values (3, 'Big Box', 1, 200);
insert into lead_estimates (lead_id, name, quantity, price) values (4, 'Small Box', 3, 125);
insert into lead_estimates (lead_id, name, quantity, price) values (5, 'Packing Stripe', 3, 100);
insert into lead_estimates (lead_id, name, quantity, price) values (6, 'Packing Stripe', 3, 100);
insert into lead_estimates (lead_id, name, quantity, price) values (6, 'Small Box', 1, 125);

insert into lead_inventories (lead_id, category, name, quantity, weight, volume) values (1, 'DINNING', 'Table', 1, 20, 15);
insert into lead_inventories (lead_id, category, name, quantity, weight, volume) values (3, 'KITCHEN', 'Fridge', 1, 100.50, 20);
insert into lead_inventories (lead_id, category, name, quantity, weight, volume) values (4, 'DINNING', 'Table', 1, 20, 15);
insert into lead_inventories (lead_id, category, name, quantity, weight, volume) values (5, 'KITCHEN', 'Fridge', 1, 100.50, 20);
insert into lead_inventories (lead_id, category, name, quantity, weight, volume) values (5, 'LIVING', 'Audio system',  4, 5, 5);
insert into lead_inventories (lead_id, category, name, quantity, weight, volume) values (6, 'LIVING', 'Shelves', 4, 75, 25);
insert into lead_inventories (lead_id, category, name, quantity, weight, volume) values (7, 'OFFICE', 'Lamp', 1, 20, 15);