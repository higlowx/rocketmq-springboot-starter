package com.higlowx.rocketmq.springboot.starter;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;

/**
 * @author Dylan.Li
 * @since 1.0
 * @date 2019/11/11
 */
public class ProducerFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private ApplicationContext context;
    private Class<?> type;
    private String group;
    private Integer sendMsgTimeout;
    private String namesrvAddr;
    private boolean vipChannelEnabled;
    private boolean txMessage;
    private String accessKey;
    private String accessSecret;
    private String executor;
    private String listener;
    private ExecutorService executorService;
    private TransactionListener transactionListener;
    private AccessChannel accessChannel;

    @Override
    public Object getObject() throws Exception {

        loadingUnifiedConfig();

        Producer.Builder producer = Producer.Builder.builder()
                .basic(this);

        if (!txMessage) {
            return Proxy.newProxyInstance(type.getClassLoader(),
                    new Class[]{type}, producer.build());
        }

        loadingExecutorService(executor);
        loadingTransactionListener(listener);

        return Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                producer.executor(executorService).transactionListener(transactionListener).build());
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(group, "group must be set");
        Assert.notNull(type, "type must be set");
        Assert.notNull(accessChannel, "accessChannel must be set");
        Assert.isTrue(!txMessage || StringUtils.hasText(listener),
                "transaction listener must be set when txMessage set");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * loading unified config
     *
     * @throws BeanNotFoundException
     */
    private void loadingUnifiedConfig() throws BeanNotFoundException {
        RocketMqUnifiedProperties properties = (RocketMqUnifiedProperties) getBean(RocketMqConst.UNIFIED_CONFIG_BEANNAME);
        Assert.hasText(properties.getNamesrvAddr(), "namesrvAddr must be set in config files  for this producer");
        namesrvAddr = properties.getNamesrvAddr();
        accessKey = properties.getAccessKey();
        accessSecret = properties.getAccessSecret();
    }

    /**
     * loading executor for local transaction executing and checking
     *
     * @param executorName bean name of defined executor
     * @throws BeanNotFoundException
     */
    private void loadingExecutorService(String executorName) throws BeanNotFoundException {

        //use default executor
        if (StringUtils.isEmpty(executorName)) {
            executorService = RocketMqExecutorService.INSTANCE.getInstance();
            return;
        }
        //use defined executor
        executorService = (ExecutorService) getBean(executorName);
    }

    /**
     * loading local transaction listener
     *
     * @param listenerName bean name of defined listener
     * @throws BeanNotFoundException
     */
    private void loadingTransactionListener(String listenerName) throws BeanNotFoundException {
        transactionListener = (TransactionListener) getBean(listenerName);
    }

    /**
     * checkout the registered bean by beanName
     *
     * @param beanName bean name
     * @return specified bean
     * @throws BeanNotFoundException
     */
    private Object getBean(String beanName) throws BeanNotFoundException {
        Object bean = context.getBean(beanName);
        if (ObjectUtils.isEmpty(bean)) {
            throw new BeanNotFoundException("class instance not found by bean named " + beanName);
        }
        return bean;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    Integer getSendMsgTimeout() {
        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(Integer sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    String getNamesrvAddr() {
        return namesrvAddr;
    }

    boolean isVipChannelEnabled() {
        return vipChannelEnabled;
    }

    public void setVipChannelEnabled(boolean vipChannelEnabled) {
        this.vipChannelEnabled = vipChannelEnabled;
    }

    boolean isTxMessage() {
        return txMessage;
    }

    public void setTxMessage(boolean txMessage) {
        this.txMessage = txMessage;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public AccessChannel getAccessChannel() {
        return accessChannel;
    }

    public void setAccessChannel(AccessChannel accessChannel) {
        this.accessChannel = accessChannel;
    }
}
