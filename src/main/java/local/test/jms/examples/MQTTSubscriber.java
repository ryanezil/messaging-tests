package local.test.jms.examples;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MQTTSubscriber extends MQTTBase {
	
	@Override
	public String getConnectionURL() {

		return new String("ssl://amq-dc0-tls-mqtt-1-svc-rte-dc0.apps-crc.testing:443");
	}


	public static void main(final String[] args) throws Exception {

		MQTTSubscriber MQTTSubscriber = new MQTTSubscriber();
		MQTTSubscriber.run();

	}

	@Override
	public void run() throws MqttException {

		try {
			
			this.connect();

			while(true) {
				//
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.unsubscribe(targetDestination);
				client.disconnect();
			}
		}		
	}


	@Override
	protected boolean isSubscriber() {
		return true;
	}
}
