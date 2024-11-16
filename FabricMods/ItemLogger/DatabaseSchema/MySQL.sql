CREATE TABLE `namespaces` (
  `identifier` smallint PRIMARY KEY,
  `name` varchar(64)
);

CREATE TABLE `items` (
  `identifier` int PRIMARY KEY,
  `namespace` smallint,
  `name` varchar(128),
  `max_stack_size` tinyint DEFAULT 64
);

CREATE TABLE `components` (
  `identifier` int PRIMARY KEY,
  `namespace` smallint,
  `name` varchar(32),
  `type` varchar(16)
);

CREATE TABLE `items_components` (
  `identifier` bigint PRIMARY KEY,
  `item` int,
  `component` bigint,
  `order` int,
  `value` varchar(512)
);

CREATE UNIQUE INDEX `namespaces_index_0` ON `namespaces` (`name`);

CREATE UNIQUE INDEX `items_index_1` ON `items` (`namespace`, `name`);

CREATE UNIQUE INDEX `components_index_2` ON `components` (`namespace`, `name`);

CREATE UNIQUE INDEX `components_index_3` ON `components` (`type`);

CREATE UNIQUE INDEX `items_components_index_4` ON `items_components` (`item`, `component`, `order`);

ALTER TABLE `items` ADD FOREIGN KEY (`namespace`) REFERENCES `namespaces` (`identifier`);

ALTER TABLE `components` ADD FOREIGN KEY (`namespace`) REFERENCES `namespaces` (`identifier`);

ALTER TABLE `items_components` ADD FOREIGN KEY (`item`) REFERENCES `items` (`identifier`);

ALTER TABLE `items_components` ADD FOREIGN KEY (`component`) REFERENCES `components` (`identifier`);
