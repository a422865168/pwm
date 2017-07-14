 

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
  `modify_opr` varchar(20) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `modify_time` datetime NOT NULL,
  `tm_smp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
