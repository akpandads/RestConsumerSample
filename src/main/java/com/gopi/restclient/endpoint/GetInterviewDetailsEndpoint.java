package com.gopi.restclient.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.gopi.restclient.domain.interviewdetails.GetInterviewRequestProperites;
import com.gopi.restclient.domain.postions.PositionResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetInterviewDetailsEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(CreateSessionEndpoint.class);
    public List<JsonArray> getInterviewDetails(GetInterviewRequestProperites getInterviewRequestProperites, String endPoint, String fromDate, String toDate, List<PositionResponse> positionResponseList){
        List<JsonArray> jsonInterviewResponses=new ArrayList<>();
        if(positionResponseList==null){
            logger.info("position Response Llist is null. Retruning from getInterview details Call by returning null ");
            return  null;
        }
        for(PositionResponse positionResponse : positionResponseList){
            String urlEnd="]&include=summary";
            endPoint = endPoint.replace("positionIDParam",positionResponse.getId());
            logger.info("Replaced positionParam in the end point url :: "+endPoint);
            String interviewUrl = endPoint+fromDate+","+toDate+urlEnd;
            JsonArray jsonObj=null;
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(interviewUrl);

            get.addHeader("X-CSRFToken", getInterviewRequestProperites.getXCSRFToken());
            get.addHeader("Cookie", getInterviewRequestProperites.getCookie());
            get.addHeader("Content-Type", "application/json"); //x-www-form-urlencoded");
            try {
                logger.info("Hitting Interview Details Url------------->"+interviewUrl);
                HttpResponse response = client.execute(get);
                JsonParser jsonParser = new JsonParser();
                jsonObj=(JsonArray)jsonParser.parse(new BasicResponseHandler().handleResponse(response));
            }
            catch (Exception e){
                logger.info("Exception while getting interview for position "+positionResponse.getId());
                logger.error("Exception while getting interview call",e);
                System.exit(0);
            }
            jsonInterviewResponses.add(jsonObj);
        }

        return jsonInterviewResponses;
    }
}
