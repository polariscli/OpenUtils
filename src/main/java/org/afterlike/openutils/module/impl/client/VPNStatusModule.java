package org.afterlike.openutils.module.impl.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.util.client.ClientUtil;
import org.apache.logging.log4j.LogManager;

public class VPNStatusModule extends Module {
	public VPNStatusModule() {
		super("VPN Status", ModuleCategory.CLIENT);
	}
	private static final String API_URL = "http://ip-api.com/json?fields=status,message,query,city,country,proxy,hosting";
	private static String maskIp(String ip) {
		if (ip == null)
			return "Unknown";
		// IPv4
		String[] parts = ip.split("\\.");
		if (parts.length == 4) {
			return parts[0] + ".***.***." + parts[3];
		}
		// IPv6
		if (ip.contains(":")) {
			int idx = ip.indexOf(':');
			return ip.substring(0, Math.min(6, idx)) + ":****";
		}
		return "Unknown";
	}

	private void checkStatus() {
		ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "VPN-Status-Thread");
			t.setDaemon(true);
			return t;
		});
		executor.execute(() -> {
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);
				int code = conn.getResponseCode();
				if (code != 200) {
					throw new RuntimeException("HTTP error: " + code);
				}
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
				if (!"success".equalsIgnoreCase(json.get("status").getAsString())) {
					ClientUtil.sendMessage("Failed success...");
					throw new RuntimeException(json.get("message").getAsString());
				}
				String ip = json.get("query").getAsString();
				String maskedIp = maskIp(ip);
				String city = json.has("city") ? json.get("city").getAsString() : "Unknown";
				String country = json.has("country")
						? json.get("country").getAsString()
						: "Unknown";
				boolean proxy = json.has("proxy") && json.get("proxy").getAsBoolean();
				boolean hosting = json.has("hosting") && json.get("hosting").getAsBoolean();
				boolean vpnDetected = proxy || hosting;
				ClientUtil.sendMessage("&bIP: &7" + maskedIp);
				ClientUtil.sendMessage("&bLocation: &7" + city + ", " + country);
				ClientUtil.sendMessage(
						"&bStatus: &7" + (vpnDetected ? "&aConnected" : "&cNot Connected"));
			} catch (Exception e) {
				ClientUtil.sendMessage("&cFailed to check VPN status.");
				LogManager.getLogger().warn(e.getMessage(), e);
			} finally {
				executor.shutdown();
			}
		});
	}

	@Override
	protected void onEnable() {
		checkStatus();
		setEnabled(false);
	}
}
