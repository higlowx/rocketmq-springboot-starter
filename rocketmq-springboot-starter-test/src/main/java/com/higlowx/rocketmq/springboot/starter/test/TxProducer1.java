package com.higlowx.rocketmq.springboot.starter.test;

import com.higlowx.rocketmq.springboot.starter.BaseRocketMqProducer;

/**
 * @author Dylan.Li
 * @desc
 * @date 2019/11/26
 */
//@RocketMqProducer(group = "txGroup1",
//        txMessage = true, listener = "txListener")
public interface TxProducer1 extends BaseRocketMqProducer {
}
