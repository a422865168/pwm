 

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pwm_rechange_order
-- ----------------------------
DROP TABLE IF EXISTS `pwm_rechange_order`;
CREATE TABLE `pwm_rechange_order` (
  `order_no` varchar(24) NOT NULL,
  `order_tm` datetime NOT NULL,
  `ac_tm` date NOT NULL,
  `tx_type` varchar(2) NOT NULL,
  `bus_type` varchar(4) NOT NULL,
  `order_ccy` varchar(4) NOT NULL,
  `order_amt` decimal(13,2) NOT NULL,
  `order_status` varchar(2) NOT NULL,
  `order_succ_tm` datetime DEFAULT NULL,
  `psn_flag` varchar(1) NOT NULL,
  `order_exp_tm` datetime NOT NULL,
  `sys_channel` varchar(5) NOT NULL,
  `ip_address` varchar(30) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `ext_order_no` varchar(24) NOT NULL DEFAULT  '',
  `modify_opr` varchar(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `modify_time` datetime NOT NULL,
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pwm_rechange_sea` (
  `order_no` varchar(24) NOT NULL COMMENT '海币充值订单编号',
  `user_id` varchar(24) NOT NULL COMMENT '内部用户号',
  `order_amt` decimal(13,2) NOT NULL DEFAULT '0.00' COMMENT '充值金额',
  `order_ccy` varchar(4) NOT NULL COMMENT '币种',
  `order_status` varchar(2) NOT NULL COMMENT '订单状态',
  `h_coupon_amt` decimal(13,2) NOT NULL DEFAULT '0.00' COMMENT '海币数量',
  `ac_tm` date NOT NULL COMMENT '会计日期',
  `tx_tm` datetime NOT NULL COMMENT '交易时间',
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10001','zh','充值金额非法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10002','zh','订单渠道不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10003','zh','对公对私标志不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10004','zh','业务类型不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10005','zh','订单号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10006','zh','订单金额不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10007','zh','业务订单号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10008','zh','订单状态不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10009','zh','订单状态值非法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10010','zh','商户号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10011','zh','营业厅充值订单号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10012','zh','充值金额非法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10013','zh','充值用户id不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10014','zh','充值操作状态不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10015','zh','充值操作状态非法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10016','zh','充值手续费非法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10017','zh','币种不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10018','zh','查询关键字不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10019','zh','查询用户类型不能为空!',now(),now());


insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20001','zh','业务类型与交易类型不匹配!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20002','zh','原充值订单不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20003','zh','充值金额不一致!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20004','zh','生成订单不唯一!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20005','zh','更新订单失败!',now(),now());

--------------ruan
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10020','zh','币种不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10021','zh','海币充值订单号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10022','zh','海币充值订状态不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10023','zh','内部用户号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10024','zh','充值金额不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10025','zh','充海币操作状态非法!',now(),now());


insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20006','zh','生成海币充值订单不唯一!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20007','zh','更新海币充值订单失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20008','zh','原海币充值订单不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20009','zh','海币充值金额不一致!',now(),now());
-------------------------
