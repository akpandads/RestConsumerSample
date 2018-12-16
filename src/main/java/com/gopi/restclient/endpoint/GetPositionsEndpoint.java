package com.gopi.restclient.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.gopi.restclient.domain.interviewdetails.GetInterviewRequestProperites;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetPositionsEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(CreateSessionEndpoint.class);
    public JsonArray getPositionDetails(GetInterviewRequestProperites getInterviewRequestProperites, String positionUrl){

        //String url = "https://stgprudential.stghv.com/api/v1/positions/?qf=[externalId:tee:common req]&include=integrationPositions";
        JsonArray jsonObj=null;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(positionUrl);

        get.addHeader("X-CSRFToken", getInterviewRequestProperites.getXCSRFToken());
        get.addHeader("Cookie", getInterviewRequestProperites.getCookie());
        get.addHeader("Content-Type", "application/json");
        try {
            logger.info("Hitting Position details Url------------->"+positionUrl);
            HttpResponse response = client.execute(get);
            JsonParser jsonParser = new JsonParser();
            jsonObj=(JsonArray)jsonParser.parse(new BasicResponseHandler().handleResponse(response));
        }
        catch (Exception e){
            logger.info("Exception while getting positions call",e);
            System.exit(0);
        }
        return jsonObj;
    }
}
