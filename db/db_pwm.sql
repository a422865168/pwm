 

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

DROP TABLE IF EXISTS `pwm_withdraw_order`;
CREATE TABLE `pwm_withdraw_order` (
  `order_no` varchar(24) NOT NULL COMMENT '订单号',
  `order_tm` datetime NOT NULL COMMENT '订单时间',
  `order_exp_tm` datetime NOT NULL COMMENT '订单失效时间',
  `ac_tm` date DEFAULT NULL COMMENT '记账时间',
  `order_ccy` char(3) NOT NULL COMMENT '币种',
  `order_succ_tm` datetime DEFAULT NULL COMMENT '订单成功时间',
  `wc_type` char(2) NOT NULL COMMENT '提现类型 11:自主提现 21:自动结算',
  `tx_type` varchar(2) NOT NULL COMMENT '交易类型 01.充值 02.消费 03.转账 04.提现 05.充海币',
  `bus_type` varchar(4) NOT NULL COMMENT '业务类型 04:提现 0401:个人提现 0402:商户提现',
  `wc_apply_amt` decimal(13,2) NOT NULL COMMENT '申请提现金额',
  `wc_act_amt` decimal(13,2) DEFAULT '0.00' COMMENT '实际提现金额',
  `fee_amt` decimal(9,2) NOT NULL COMMENT '手续费金额',
  `pay_urge_flg` char(1) NOT NULL DEFAULT '0' COMMENT '付款加急标识 1.是 0.否',
  `user_id` varchar(20) NOT NULL COMMENT '内部用户编号',
  `user_name` varchar(60) NOT NULL COMMENT '用户/商户名称',
  `agr_no` varchar(20) DEFAULT '' COMMENT '签约协议号',
  `cap_corg_no` varchar(16) NOT NULL DEFAULT '' COMMENT '资金合作机构号',
  `cap_card_no` varchar(30) NOT NULL COMMENT '资金卡号',
  `cap_card_type` char(1) NOT NULL DEFAULT '' COMMENT '资金卡账户类型 0:借记卡 1:信用卡 2:准贷记卡 3:储蓄账户',
  `cap_card_name` varchar(60) DEFAULT '' COMMENT '资金卡账户姓名',
  `wc_remark` varchar(100) DEFAULT '' COMMENT '提现备注',
  `ntf_mbl` varchar(20) NOT NULL COMMENT '通知的手机号',
  `order_status` char(2) NOT NULL COMMENT '订单状态 W1:系统受理中 W2:资金流出已受理 S1:付款成功 F1:付款失败 F2:付款核销 R9:审批拒绝',
  `rsp_order_no` varchar(20) DEFAULT NULL COMMENT '资金流出模块订单号',
  `rsp_succ_tm` datetime DEFAULT NULL COMMENT '资金流出模块成功时间',
  `bus_cnl` varchar(5) NOT NULL COMMENT '业务受理渠道',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
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
--------------ruan
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
---------------------leon
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10026','zh','实际提现金额不小于0!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10027','zh','用户编号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10028','zh','银行卡号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10029','zh','申请提现金额不小于0!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10030','zh','申请手续费不小于0!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10031','zh','提现类型不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10032','zh','付款加急标识不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10033','zh','资金合作机构不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10034','zh','支付密码不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10035','zh','手机号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10036','zh','营业厅充值请求参数校验失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10037','zh','营业厅充值请求操作状态异常!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10038','zh','业务类型不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10039','zh','汇款单图片地址不能为空!',now(),now());


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
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20006','zh','生成海币充值订单不唯一!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20007','zh','更新海币充值订单失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20008','zh','原海币充值订单不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20009','zh','海币充值金额不一致!',now(),now());
-------------------------
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20010','zh','营业厅充值找不到原订单信息!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20011','zh','营业厅充值原订单状态异常!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20012','zh','更新收银订单失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20013','zh','线下收银台收款失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20014','zh','未查找到任何用户或者商户信息!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20015','zh','未找到任何汇款充值订单信息!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20016','zh','未找到任何汇款银行账号信息!',now(),now());

insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30001','zh','该用户为黑名单!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30002','zh','提现余额加手续费大于用户账户余额!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30003','zh','支付密码错误次数超过5次!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30004','zh','支付密码错误!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30005','zh','提现订单号不存在!',now(),now());

-------------------------
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40001','zh','调用营销接口返回接口异常!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40002','zh','借贷不平衡!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40003','zh','账户信息不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40004','zh','调用营销接口未返回数据!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40005','zh','调用创建收银台接口未返回数据!',now(),now());