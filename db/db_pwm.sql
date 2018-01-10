CREATE TABLE `pwm_rechange_hcoupon` (
  `order_no` varchar(28) NOT NULL COMMENT '海币充值订单编号',
  `user_id` varchar(20) NOT NULL COMMENT '内部用户号',
  `ac_tm` date NOT NULL COMMENT '会计日期',
  `tx_tm` datetime NOT NULL COMMENT '交易时间',
  `order_amt` decimal(13,2) NOT NULL DEFAULT '0.00' COMMENT '充值金额',
  `order_ccy` varchar(4) NOT NULL COMMENT '币种',
  `order_status` varchar(2) NOT NULL COMMENT '订单状态',
  `bus_type` varchar(4) NOT NULL COMMENT '业务类型  0501充海币',
  `tx_type` varchar(2) NOT NULL COMMENT '交易类型  05',
  `h_coupon_amt` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '海币数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pwm_rechange_order` (
  `order_no` varchar(28) NOT NULL COMMENT '充值订单编号',
  `order_tm` datetime NOT NULL COMMENT '订单创建时间',
  `ac_tm` date NOT NULL COMMENT '会计日',
  `tx_type` varchar(2) NOT NULL COMMENT '交易类型',
  `payer_id` varchar(20) DEFAULT NULL COMMENT '充值用户id',
  `bus_type` varchar(4) NOT NULL COMMENT '业务类型',
  `fee` decimal(10,2) DEFAULT NULL COMMENT '手续费',
  `order_ccy` varchar(4) NOT NULL COMMENT '订单币种',
  `order_amt` decimal(13,2) NOT NULL COMMENT '订单金额',
  `order_status` varchar(2) NOT NULL COMMENT '订单状态',
  `order_succ_tm` datetime DEFAULT NULL COMMENT '订单处理成功时间',
  `psn_flag` varchar(1) NOT NULL COMMENT '个企标识',
  `order_exp_tm` datetime NOT NULL COMMENT '订单超时时间',
  `sys_channel` varchar(5) NOT NULL COMMENT '系统渠道',
  `ip_address` varchar(30) DEFAULT NULL ,
  `ext_order_no` varchar(25) DEFAULT NULL COMMENT '外围订单号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `hall_order_no` varchar(28) DEFAULT NULL COMMENT '营业厅充值订单编号',
  `modify_opr` varchar(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `modify_time` datetime NOT NULL,
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `corp_org` varchar(16) DEFAULT '' COMMENT '资金机构',
  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pwm_withdraw_bank` (
  `bin_id` varchar(20) NOT NULL COMMENT '主键',
  `card_bin` varchar(12) NOT NULL DEFAULT '123' COMMENT '卡bin',
  `cap_corg` varchar(16) NOT NULL DEFAULT 'ABC' COMMENT '资金机构',
  `bank_name` varchar(24) NOT NULL DEFAULT '' COMMENT '提现银行',
  `card_ac_type` char(1) NOT NULL DEFAULT 'D' COMMENT '卡类型，D借记卡，C贷记卡',
  `card_length` int(11) NOT NULL DEFAULT '16' COMMENT '卡长度',
  `opr_id` varchar(16) NOT NULL DEFAULT '' COMMENT '操作员',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`bin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pwm_withdraw_card` (
  `card_id` varchar(24) NOT NULL COMMENT '主键',
  `card_no` varchar(64) NOT NULL COMMENT '加密银行卡号',
  `card_no_last` varchar(4) NOT NULL COMMENT '银行卡后四位',
  `branch_name` varchar(24) NOT NULL COMMENT '支行名称',
  `user_id` varchar(20) NOT NULL COMMENT '用户编号',
  `cap_corg` varchar(16) NOT NULL COMMENT '资金机构',
  `card_status` char(1) NOT NULL DEFAULT '1' COMMENT '卡状态 1生效 0失效',
  `eft_tm` datetime NOT NULL COMMENT '生效时间',
  `fail_tm` varchar(20) DEFAULT '' COMMENT '失效时间',
  `remark` varchar(128) DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `pwm_withdraw_order` (
  `order_no` varchar(28) NOT NULL COMMENT '订单号',
  `order_tm` datetime NOT NULL COMMENT '订单时间',
  `order_exp_tm` datetime NOT NULL COMMENT '订单失效时间',
  `ac_tm` date DEFAULT NULL COMMENT '记账时间',
  `order_ccy` char(3) NOT NULL COMMENT '币种',
  `order_succ_tm` datetime DEFAULT NULL COMMENT '订单成功时间',
  `wc_type` char(2) NOT NULL COMMENT '提现类型 11:自主提现 21:自动结算',
  `tx_type` varchar(2) NOT NULL DEFAULT '04' COMMENT '交易类型 01.充值 02.消费 03.转账 04.提现 05.充海币',
  `bus_type` varchar(4) NOT NULL COMMENT '业务类型 04:提现 0401:个人提现 0402:商户提现',
  `wc_apply_amt` decimal(13,2) NOT NULL COMMENT '申请提现金额',
  `wc_act_amt` decimal(13,2) DEFAULT '0.00' COMMENT '实际提现金额',
  `wc_total_amt` decimal(13,2) DEFAULT '0.00' COMMENT '提现总金额',
  `fee_amt` decimal(9,2) NOT NULL COMMENT '手续费金额',
  `pay_urge_flg` char(1) NOT NULL DEFAULT '0' COMMENT '付款加急标识 1.是 0.否',
  `user_id` varchar(20) NOT NULL COMMENT '内部用户编号',
  `user_name` varchar(60) NOT NULL COMMENT '用户/商户名称',
  `agr_no` varchar(20) DEFAULT '' COMMENT '签约协议号',
  `cap_corg_no` varchar(16) NOT NULL DEFAULT '' COMMENT '资金合作机构号',
  `cap_card_no` varchar(64) NOT NULL COMMENT '资金卡号',
  `cap_card_type` char(1) NOT NULL DEFAULT '' COMMENT '资金卡账户类型 0:借记卡 1:信用卡 2:准贷记卡 3:储蓄账户',
  `cap_card_name` varchar(60) DEFAULT '' COMMENT '资金卡账户姓名',
  `wc_remark` varchar(100) DEFAULT '' COMMENT '提现备注',
  `ntf_mbl` varchar(20) NOT NULL COMMENT '通知的手机号',
  `order_status` char(2) NOT NULL COMMENT '订单状态 W1:系统受理中 W2:资金流出已受理 S1:付款成功 F1:付款失败 F2:付款核销 R9:审批拒绝',
  `rsp_order_no` varchar(32) DEFAULT NULL COMMENT '资金流出模块订单号',
  `rsp_succ_tm` datetime DEFAULT NULL COMMENT '资金流出模块成功时间',
  `bus_cnl` varchar(5) NOT NULL COMMENT '业务受理渠道',
  `modify_time` datetime NOT NULL COMMENT '修改时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

--错误码初始化
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
values ('PWM10024','zh','充值海币数量不能为空',now(),now());
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
values ('PWM10030','zh','申请提现手续费不小于0!',now(),now());
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
values ('PWM10040','zh','卡种不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10041','zh','卡种非法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10042','zh','申请提现金额不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10043','zh','银行卡号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10044','zh','支行名称不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10045','zh','资金机构不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10046','zh','提现银行卡ID不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10047','zh','银行卡户名不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10048','zh','卡后四位不能为空!',now(),now());

insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10049','zh','手机号不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10050','zh','对平总金额不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10051','zh','附言摘要不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10052','zh','营业厅充值手续费不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10053','zh','营业厅充值请求数据为空!',now(),now());

insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM10054','zh','支付密码随机数不能为空!',now(),now());

INSERT INTO lemon_msg_info(msg_cd,LANGUAGE,msg_info,create_time,modifyTime)
VALUES ('PWM10055','zh','支付密码不能为空!',NOW(),NOW());
INSERT INTO lemon_msg_info(msg_cd,LANGUAGE,msg_info,create_time,modifyTime)
VALUES ('PWM10056','zh','营业厅对账类型不能为空!',NOW(),NOW());
INSERT INTO lemon_msg_info(msg_cd,LANGUAGE,msg_info,create_time,modifyTime)
VALUES ('PWM10057','zh','营业厅对账日期不能为空!',NOW(),NOW());
INSERT INTO lemon_msg_info(msg_cd,LANGUAGE,msg_info,create_time,modifyTime)
VALUES ('PWM10058','zh','营业厅指定对账文件名不能为空!',NOW(),NOW());

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
values ('PWM20017','zh','不能重复提交待审核订单!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20018','zh','汇款金额与订单金额不一致!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20019','zh','更新提现银行卡状态失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20020','zh','添加提现银行卡失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20021','zh','不能重复下单!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20022','zh','账户不能为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20023','zh','该充值订单的收银订单号为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20024','zh','找不到原充值订单信息!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20025','zh','账务差错处理类型校验失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20026','zh','订单账务借贷不平衡!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20027','zh','营业厅长款补单失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20028','zh','营业厅短款撤单失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20029','zh','不能对已成功订单进行补单!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20030','zh','不能对失败订单进行撤单!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20031','zh','长短款差错处理业务类型非法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20032','zh','不支持的差错处理操作!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20033','zh','对平金额处理请求数据为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20034','zh','对平金额处理对账类型不匹配!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20035','zh','充值短款撤单请求数据为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20036','zh','对账差错子类型错误!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20037','zh','充值长款补单请求数据为空!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20038','zh','营业厅提现撤单失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM20039','zh','余额不足!',now(),now());

insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30001','zh','该用户为黑名单!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30002','zh','提现金额加手续费大于用户账户余额!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30003','zh','支付密码错误次数超过5次!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30004','zh','支付密码错误!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30005','zh','提现订单号不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30006','zh','传入手续费与算出手续费不一致!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30007','zh','调用rsm风控接口失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30008','zh','调用tfm计费接口失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30009','zh','调用acm账户接口失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30010','zh','调用urm用户接口失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30011','zh','调用tfm计费接口失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30012','zh','该提现卡号已存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30013','zh','该提现银行卡不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30014','zh','该提现银行卡已失效!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30015','zh','查询汇款银行账户未找到结果!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30016','zh','获取营业厅对账文件失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM30017','zh','支付密码错误超过5次!',now(),now());
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
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40006','zh','账号不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40007','zh','资金能力受理失败!',now(),now());

insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40701','zh','生成对账文件失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40702','zh','上传对账文件到文件服务器失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40703','zh','生成对账标志文件失败!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime)
values ('PWM40704','zh','获取本地路径失败!!',now(),now());

