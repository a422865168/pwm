 

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

DROP TABLE IF EXISTS `pwm_withdraw_order`;
CREATE TABLE `pwm_withdraw_order` (
  `order_no` varchar(24) NOT NULL COMMENT '订单号',
  `order_tm` datetime NOT NULL COMMENT '订单时间',
  `order_exp_tm` datetime NOT NULL COMMENT '订单失效时间',
  `ac_tm` date NOT NULL COMMENT '记账时间',
  `order_ccy` char(3) NOT NULL DEFAULT 'RMB' COMMENT '币种',
  `order_succ_tm` datetime DEFAULT NULL COMMENT '订单成功时间',
  `wc_type` char(2) NOT NULL DEFAULT '21' COMMENT '提现类型 11:自主提现 21:自动结算',
  `tx_type` varchar(2) NOT NULL DEFAULT '04' COMMENT '交易类型 01.充值 02.消费 03.转账 04.提现 05.充海币',
  `bus_type` varchar(4) NOT NULL DEFAULT '0401' COMMENT '业务类型 04:提现 0401:个人提现 0402:商户提现',
  `wc_apply_amt` decimal(13,2) NOT NULL COMMENT '申请提现金额',
  `wc_act_amt` decimal(13,2) NOT NULL COMMENT '实际提现金额',
  `fee_amt` decimal(9,2) NOT NULL COMMENT '手续费金额',
  `pay_urge_flg` char(1) NOT NULL DEFAULT '0' COMMENT '付款加急标识 1.是 0.否',
  `user_id` varchar(20) NOT NULL COMMENT '内部用户编号',
  `user_name` varchar(60) NOT NULL COMMENT '用户/商户名称',
  `agr_no` varchar(20) DEFAULT NULL COMMENT '签约协议号',
  `cap_corg_no` varchar(16) NOT NULL COMMENT '资金合作机构号',
  `cap_card_no` varchar(30) NOT NULL COMMENT '资金卡号',
  `cap_card_type` char(1) DEFAULT NULL COMMENT '资金卡账户类型 0:借记卡 1:信用卡 2:准贷记卡 3:储蓄账户',
  `cap_card_name` varchar(60) DEFAULT NULL COMMENT '资金卡账户姓名',
  `wc_remark` varchar(100) DEFAULT NULL COMMENT '提现备注',
  `ntf_mbl` varchar(20) NOT NULL COMMENT '通知的手机号',
  `order_status` char(2) NOT NULL DEFAULT 'W1' COMMENT '订单状态 W1:系统受理中 W2:资金流出已受理 S1:付款成功 F1:付款失败 F2:付款核销 R9:审批拒绝',
  `rsp_order_no` varchar(20) DEFAULT NULL COMMENT '资金流出模块订单号',
  `rsp_succ_tm` datetime DEFAULT NULL COMMENT '资金流出模块成功时间',
  `bus_cnl` varchar(5) NOT NULL COMMENT '业务受理渠道',
  `user_ip_adr` varchar(15) DEFAULT NULL COMMENT '用户操作IP地址',
  `user_opr_sys` varchar(30) DEFAULT NULL COMMENT '用户操作系统信息',
  `user_brow_info` varchar(30) DEFAULT NULL COMMENT '用户浏览器信息',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DELAY_KEY_WRITE=1

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
values ('PWM20001','zh','业务类型与交易类型不匹配!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20002','zh','原充值订单不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20003','zh','充值金额不一致!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20004','zh','生成订单不唯一!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20005','zh','更新订单失败!',now(),now());