package com.xixi.rocketmq.springboot.starter.test;

import com.xixi.rocketmq.springboot.starter.BaseRocketMqProducer;

/**
 * @author Chris.Li
 * @desc
 * @date 2019/11/26
 */
//@RocketMqProducer(group = "txGroup1",
//        txMessage = true, listener = "txListener")
public interface TxProducer1 extends BaseRocketMqProducer {
}
