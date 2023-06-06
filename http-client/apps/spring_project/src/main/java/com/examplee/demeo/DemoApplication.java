package com.examplee.demeo;

import com.example.demo.http.HttpClient;
import com.example.logger.*;
import com.example.DatabaseWrapper;

// import com.example.logger.FileLoggingClient;

import com.example.demo.crypto.ChaCha20Poly1305;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws Exception {

		ConsoleLoggingClient consolelog = new ConsoleLoggingClient();

		System.out.println(" ..........Http client library.......");
		System.out.println("..............................................");

		HttpClient client = new HttpClient();
		String url = "https://jsonplaceholder.typicode.com/posts";
		String response = client.withUrl(url).addQueryParam("id", "1").sendRequest();
		// consolelog.log((response));
		System.out.println(response);

		System.out.println(" ...........Database_connector library.......");
		System.out.println("..............................................");

		Properties dbProperties = new Properties();
		dbProperties.setProperty("db.url", "jdbc:mysql://localhost:3306/goss_skill");
		dbProperties.setProperty("db.username", "root");
		dbProperties.setProperty("db.password", "gossadmin");
		dbProperties.setProperty("max.pool.size", "20");

		// Create an instance of DatabaseWrapper using the configured properties
		DatabaseWrapper databaseWrapper = new DatabaseWrapper(dbProperties);

		try {
			// INSERT query
			String insertQuery = "INSERT INTO goss_skill.skiils_table (ID, enterpriseID, skills) VALUES (?, ?, ?)";
			int insertedRows = databaseWrapper.insert(insertQuery, 12321, "tested1", "React");
			System.out.println("Inserted rows: " + insertedRows);

			// UPDATE query
			String updateQuery = "UPDATE goss_skill.skiils_table SET skills = ? WHERE ID = ?";
			int updatedRows = databaseWrapper.update(updateQuery, "Python", 30);
			System.out.println("Updated rows: " + updatedRows);

			// DELETE query
			String deleteQuery = "DELETE FROM goss_skill.skiils_table WHERE ID = ?";
			int deletedRows = databaseWrapper.delete(deleteQuery, 89);
			System.out.println("Deleted rows: " + deletedRows);

			// Execute the SELECT query
			String selectQuery = "SELECT id, enterpriseID, skills FROM goss_skill.skiils_table WHERE enterpriseID = ?";
			Object[] params = { "hirdanshu.vij" };
			List<Map<String, Object>> results = databaseWrapper.executeQuery(selectQuery, params);

			// Process the results
			if (results != null) {
				for (Map<String, Object> row : results) {
					int id = (int) row.get("id");
					String enterpriseID = (String) row.get("enterpriseID");
					String skills = (String) row.get("skills");

					System.out.println("id: " + id + ", enterpriseID: " + enterpriseID + ", skills: " + skills);
				}
			} else {
				System.out.println("Query execution failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(".............. Crypto client .............");
		System.out.println("..........................................");

		String input = "Java & ChaCha20-Poly1305.";

		ChaCha20Poly1305 cipher = new ChaCha20Poly1305();

		SecretKey key = getKey(); // 256-bit secret key (32 bytes)

		System.out.println("Input                  : " + input);
		System.out.println("Input             (hex): " + convertBytesToHex(input.getBytes()));

		System.out.println("\n---Encryption---");
		byte[] cText = cipher.encrypt(input.getBytes(), key); // encrypt

		System.out.println("Key               (hex): " + convertBytesToHex(key.getEncoded()));
		System.out.println("Encrypted         (hex): " + convertBytesToHex(cText));

		System.out.println("\n---Print Mac and Nonce---");

		ByteBuffer bb = ByteBuffer.wrap(cText);
		int NONCE_LEN = 12; // 96 bits, 12 bytes
		int MAC_LEN = 16; // 128 bits, 16 bytes

		// This cText contains chacha20 ciphertext + polu1305 MAC + nonce

		// ChaCha20 encrypted the plaintext into a ciphertext of equal length
		byte[] originalCText = new byte[input.getBytes().length];
		byte[] nonce = new byte[NONCE_LEN]; // 16 bytes , 128 bits
		byte[] mac = new byte[MAC_LEN]; // 12 bytes , 96 bits

		bb.get(originalCText);
		bb.get(mac);
		bb.get(nonce);

		System.out.println("Cipher (original) (hex): " + convertBytesToHex(originalCText));
		System.out.println("MAC               (hex): " + convertBytesToHex(mac));
		System.out.println("Nonce             (hex): " + convertBytesToHex(nonce));

		System.out.println("\n---Decryption---");
		System.out.println("Input             (hex): " + convertBytesToHex(cText));

		byte[] pText = cipher.decrypt(cText, key); // decrypt

		System.out.println("Key               (hex): " + convertBytesToHex(key.getEncoded()));
		System.out.println("Decrypted         (hex): " + convertBytesToHex(pText));
		System.out.println("Decrypted              : " + new String(pText));

	}

	// https://mkyong.com/java/java-how-to-convert-bytes-to-hex/
	private static String convertBytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte temp : bytes) {
			result.append(String.format("%02x", temp));
		}
		return result.toString();
	}

	// A 256-bit secret key (32 bytes)
	private static SecretKey getKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("ChaCha20");
		keyGen.init(256, SecureRandom.getInstanceStrong());
		return keyGen.generateKey();
	}

}
