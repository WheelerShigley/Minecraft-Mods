CREATE TABLE [namespaces] (
  [identifier] smallint PRIMARY KEY,
  [name] varchar(64)
)
GO

CREATE TABLE [items] (
  [identifier] int PRIMARY KEY,
  [namespace] smallint,
  [name] varchar(128),
  [max_stack_size] tinyint DEFAULT (64)
)
GO

CREATE TABLE [components] (
  [identifier] int PRIMARY KEY,
  [namespace] smallint,
  [name] varchar(32),
  [type] varchar(16)
)
GO

CREATE TABLE [items_components] (
  [identifier] bigint PRIMARY KEY,
  [item] int,
  [component] bigint,
  [order] int,
  [value] varchar(512)
)
GO

CREATE UNIQUE INDEX [namespaces_index_0] ON [namespaces] ("name")
GO

CREATE UNIQUE INDEX [items_index_1] ON [items] ("namespace", "name")
GO

CREATE UNIQUE INDEX [components_index_2] ON [components] ("namespace", "name")
GO

CREATE UNIQUE INDEX [components_index_3] ON [components] ("type")
GO

CREATE UNIQUE INDEX [items_components_index_4] ON [items_components] ("item", "component", "order")
GO

ALTER TABLE [items] ADD FOREIGN KEY ([namespace]) REFERENCES [namespaces] ([identifier])
GO

ALTER TABLE [components] ADD FOREIGN KEY ([namespace]) REFERENCES [namespaces] ([identifier])
GO

ALTER TABLE [items_components] ADD FOREIGN KEY ([item]) REFERENCES [items] ([identifier])
GO

ALTER TABLE [items_components] ADD FOREIGN KEY ([component]) REFERENCES [components] ([identifier])
GO
