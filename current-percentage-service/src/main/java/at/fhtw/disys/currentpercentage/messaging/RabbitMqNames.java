package at.fhtw.disys.currentpercentage.messaging;

public final class RabbitMqNames {

    public static final String ENERGY_EXCHANGE = "energy.exchange";
    public static final String USAGE_UPDATES_QUEUE = "usage.updates";
    public static final String USAGE_UPDATED_ROUTING_KEY = "usage.updated";

    private RabbitMqNames() {
    }
}