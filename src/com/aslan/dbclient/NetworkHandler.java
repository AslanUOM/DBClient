package com.aslan.dbclient;

import com.aslan.dbclient.model.SensorResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class NetworkHandler {

	public void getRequest() {
		try {
			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8080/ConTra/sensordatareceiver/save");
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			String output = response.getEntity(String.class);
			System.out.println("Output from Server .... \n");
			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postRequest(SensorResponse sensorResponse) {
		try {

			Client client = Client.create();
			WebResource webResource = client.resource("http://localhost:8080/ConTra/sensordatareceiver/save");
			String input = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, sensorResponse);
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
