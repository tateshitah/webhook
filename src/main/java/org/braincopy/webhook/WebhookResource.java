package org.braincopy.webhook;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Hiroaki Tateshita
 * @version 0.2.0
 * 
 */
@Path("wh")
public class WebhookResource{

    @Context
    ServletContext context;

    Properties envProperties;

    @GET
    public Response getIt(@QueryParam("crc_token") String crc_token){
        ResponseBuilder builder=Response.ok();
        try {
            loadProperties();
            String algo = "HmacSHA256";
            builder.type(MediaType.APPLICATION_JSON_TYPE);
            try {
                Mac mac = Mac.getInstance(algo);
                String consumer_secret = envProperties.getProperty("consumer_secret");
                final SecretKeySpec keySpec = new SecretKeySpec(consumer_secret.getBytes(), algo);
                mac.init(keySpec);
                
                String hash = Base64.encodeBase64String(mac.doFinal(crc_token.getBytes("UTF-8")));


                builder.entity("{\"response_token\":\"sha256=" + hash + "\"}");
            
            } catch (NoSuchAlgorithmException e) {
                builder.entity("{\"mes\": \"contact admin.\"}");
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                builder.entity("{\"message\": \"contact admin.\"}");
                e.printStackTrace();
			}
        } catch (IOException e1) {
            builder.entity("{contact admin}");
            System.err.println("fail to load properties file");
            e1.printStackTrace();
        }
        return builder.build();
    }

    private void loadProperties() throws IOException {
        if (envProperties == null) {
			envProperties = new Properties();
			try {
				if (context != null) {
					envProperties.load(context.getResourceAsStream("WEB-INF/conf/env.properties"));
				} else {
					envProperties.load(new FileInputStream("WEB-INF/conf/env.properties"));
				}
			} catch (IOException e) {
				throw new IOException("during loading properties file: " + e.getMessage());
            }
        }
    }
}