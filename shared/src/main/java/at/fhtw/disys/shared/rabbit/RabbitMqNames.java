package at.fhtw.disys.shared.rabbit;

public final class RabbitMqNames {

    public static final String ENERGY_EXCHANGE = "energy.exchange";
    public static final String ENERGY_MESSAGES_QUEUE = "energy.messages";
    public static final String USAGE_UPDATES_QUEUE = "usage.updates";
    public static final String ENERGY_MESSAGE_ROUTING_KEY = "energy.message";
    public static final String USAGE_UPDATED_ROUTING_KEY = "usage.updated";

    private RabbitMqNames() {
    }
}
