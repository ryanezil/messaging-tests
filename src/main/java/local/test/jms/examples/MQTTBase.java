package local.test.jms.examples;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public abstract class MQTTBase implements MqttCallbackExtended {
	
	protected static final String USER = "amq-demo-user";
	protected static final String PASSWORD = "password";	

	/*
      At most once (0)
      At least once (1)
      Exactly once (2).
	 */
	protected static final int QOS = 2;
	protected static final boolean CLEAN_SESSION = true;

	
	protected static final String targetDestination = "foo/bar/test";

	protected AtomicInteger counter = new AtomicInteger(0);
	
	protected IMqttClient client = null;	
	
	protected abstract void run() throws MqttException;
	protected abstract String getConnectionURL();
	protected abstract boolean isSubscriber();

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("MQTT - Connection lost: " + cause);

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String textMessage = new String(message.getPayload(), StandardCharsets.UTF_8);

		System.out.format("MQTT DEMO - Received message[%d] with payload: %s%n", counter.getAndIncrement(), textMessage);		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

		System.out.println("MQTT - Message delivery has been completed");
		
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {

		System.out.println("MQTT - Connection to the server has been established...");

		if(isSubscriber()) {
			try {
				client.subscribe(targetDestination, QOS);
				System.out.println("Client Subscribed to topic: " + targetDestination);				
			} catch (MqttException e) {
				e.printStackTrace();
			}			
		}
	}	

	protected static SSLSocketFactory getSSLSocketFactory() {
		// Code from: https://stackoverflow.com/questions/73407169/using-insecure-tls-in-java-version-of-eclipse-paho

		TrustManager [] trustAllCerts = new TrustManager [] {new X509ExtendedTrustManager () {
			@Override
			public void checkClientTrusted (X509Certificate [] chain, String authType, Socket socket) {
		 
			}
		 
			@Override
			public void checkServerTrusted (X509Certificate [] chain, String authType, Socket socket) {
		 
			}
		 
			@Override
			public void checkClientTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {
		 
			}
		 
			@Override
			public void checkServerTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {
		 
			}
		 
			@Override
			public java.security.cert.X509Certificate [] getAcceptedIssuers () {
			   return null;
			}
		 
			@Override
			public void checkClientTrusted (X509Certificate [] certs, String authType) {
			}
		 
			@Override
			public void checkServerTrusted (X509Certificate [] certs, String authType) {
			}
		 
		 }};

		 SSLContext sc = null;
		 try {
			sc = SSLContext.getInstance ("TLS");
			sc.init (null, trustAllCerts, new java.security.SecureRandom ());

			return sc.getSocketFactory();
		 } catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace ();

			return null;
		 }		 
	}
	

	protected void connect() throws MqttException {
		// Create a new MQTT connection to the broker. If the the client ID is not set,
		// the broker will pick one for us.
		
		String role = isSubscriber() ? "CONSUME" : "PRODUCE";
		System.out.println("Connecting to broker using MQTT to " + role);
		
		// Remember to use the same clientId when cleansession = false
		String clientId = MqttClient.generateClientId();
		
		client = new MqttClient(getConnectionURL(), clientId, null);
        			
		MqttConnectOptions options = new MqttConnectOptions();

		options.setPassword(PASSWORD.toCharArray());
		options.setUserName(USER);
		
		options.setSocketFactory(getSSLSocketFactory());
		
		if(isSubscriber()) {
			// Note that a clientId must be specified when setting cleanSession to false
			/*
			 * If true, all state information is discarded when connecting to, or
			 * disconnecting from, the MQTT broker
			 */				
			options.setCleanSession(CLEAN_SESSION);
		}
		
		
		options.setAutomaticReconnect(true);
		options.setConnectionTimeout(30);
		options.setKeepAliveInterval(60); // 60 seconds
		options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

		client.setCallback(this);
		
		client.connect(options);
		
		System.out.println("Connected to broker with clientID=" + clientId);		
		
	}
	
}
