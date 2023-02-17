package local.test.jms.examples;

import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MQTTProducer extends MQTTBase {

	private static boolean RETAIN = false;
	
	@Override
	public String getConnectionURL() {

		return new String("ssl://amq-dc0-tls-mqtt-1-svc-rte-dc0.apps-crc.testing:443");
	}


	public static void main(final String[] args) throws Exception {

		MQTTProducer MQTTProducer = new MQTTProducer();
		MQTTProducer.run();
	}
	
	@Override
	public void run() throws MqttException {

		try {

			this.connect();
			
			int i = 0;
			while (true) {
	
				String payload0 = "Generated message ID #[" + i + "] with retain=[" + RETAIN + "] published into [" + targetDestination + "]";

				client.publish(targetDestination, payload0.getBytes(), QOS, RETAIN);
	
				System.out.println("Message #" + i + " sent wiht RETAIN=" + RETAIN);

				i++;

				TimeUnit.SECONDS.sleep(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.disconnect();
			}
		}

	}


	@Override
	protected boolean isSubscriber() {
		return false;
	}	
	
}
