
-- Create table
create table SECOND_HOUSE
(
  id                 VARCHAR2(64),
  title              NVARCHAR2(64),
  source_website     NVARCHAR2(64),
  residential        NVARCHAR2(64),
  floor              NVARCHAR2(64),
  address            NVARCHAR2(64),
  space              NVARCHAR2(64),
  orientation        NVARCHAR2(64),
  house_type         NVARCHAR2(64),
  listed_date        NVARCHAR2(64),
  listed_unit_price  NVARCHAR2(64),
  listed_total       NVARCHAR2(64),
  volume_ratio       NVARCHAR2(64),
  green_rate         NVARCHAR2(64),
  property_name      NVARCHAR2(64),
  property_rights    NVARCHAR2(64),
  property_fee       NVARCHAR2(64),
  traffic            NVARCHAR2(512),
  surround_condition NVARCHAR2(512),
  decorator          NVARCHAR2(64),
  buying_date        NVARCHAR2(64),
  grep_date          NVARCHAR2(64)
)
tablespace SKIEER_DATA
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns
comment on column SECOND_HOUSE.id
  is '主键';
comment on column SECOND_HOUSE.title
  is '标题';
comment on column SECOND_HOUSE.source_website
  is '来自哪个网站';
comment on column SECOND_HOUSE.residential
  is '小区';
comment on column SECOND_HOUSE.floor
  is '楼层';
comment on column SECOND_HOUSE.address
  is '地址';
comment on column SECOND_HOUSE.space
  is '房屋房间';
comment on column SECOND_HOUSE.orientation
  is '朝向';
comment on column SECOND_HOUSE.house_type
  is '户型';
comment on column SECOND_HOUSE.listed_date
  is '挂牌时间';
comment on column SECOND_HOUSE.listed_unit_price
  is '挂牌单价';
comment on column SECOND_HOUSE.listed_total
  is '挂牌总价';
comment on column SECOND_HOUSE.volume_ratio
  is '容积率';
comment on column SECOND_HOUSE.green_rate
  is '绿化率';
comment on column SECOND_HOUSE.property_rights
  is '产权性质';
comment on column SECOND_HOUSE.property_name
  is '物业名称';
comment on column SECOND_HOUSE.property_fee
  is '物业费';
comment on column SECOND_HOUSE.traffic
  is '交通情况';
comment on column SECOND_HOUSE.surround_condition
  is '周边情况';
comment on column SECOND_HOUSE.decorator
  is '装修情况';
comment on column SECOND_HOUSE.buying_date
  is '购买时间';
comment on column SECOND_HOUSE.grep_date
  is '采集时间';
