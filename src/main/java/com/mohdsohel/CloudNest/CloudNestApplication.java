package com.mohdsohel.CloudNest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudNestApplication {

	public static void main(String[] args) {

        System.out.println("========== PROXY SETTINGS ==========");

        System.out.println("http.proxyHost = "
                + System.getProperty("http.proxyHost"));

        System.out.println("http.proxyPort = "
                + System.getProperty("http.proxyPort"));

        System.out.println("https.proxyHost = "
                + System.getProperty("https.proxyHost"));

        System.out.println("https.proxyPort = "
                + System.getProperty("https.proxyPort"));

        System.out.println("socksProxyHost = "
                + System.getProperty("socksProxyHost"));

        System.out.println("socksProxyPort = "
                + System.getProperty("socksProxyPort"));

        System.out.println("===================================");

        SpringApplication.run(CloudNestApplication.class, args);
	}

}
