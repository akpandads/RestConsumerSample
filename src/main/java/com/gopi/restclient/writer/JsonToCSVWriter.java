package com.gopi.restclient.writer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.gopi.restclient.domain.interviewdetails.InterviewResponseDomain;
import com.gopi.restclient.domain.postions.PositionResponse;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonToCSVWriter {
    private static final Logger logger = LoggerFactory.getLogger(JsonToCSVWriter.class);

    public static List<InterviewResponseDomain> convertToInterviewDomain(List<JsonArray> jsonInterviewList){
        logger.info("Converted to json object start");
        List<InterviewResponseDomain> interviewResponseDomainsList = new ArrayList<>();
        Type listType = new TypeToken<ArrayList<InterviewResponseDomain>>(){}.getType();
        Gson gson = new Gson();
        for(JsonArray jsonInterview:jsonInterviewList){
            List<InterviewResponseDomain> tempList = gson.fromJson(jsonInterview,listType);
            for(InterviewResponseDomain interviewResponseDomain:tempList){
                interviewResponseDomainsList.add(interviewResponseDomain);
            }
        }
        logger.info("Converted to json array with size"+interviewResponseDomainsList.size());
        return interviewResponseDomainsList;
    }

    public static boolean writeToFile(List<InterviewResponseDomain> interviewResponseDomainList){
        String filePath="InterviewDetails.csv";
        File file = new File(filePath);
        //using custom delimiter and quote character
        boolean succesFulRun=true;
        try{
            FileWriter outputfile = new FileWriter(file);
            CSVWriter csvWriter = new CSVWriter(outputfile);
            String[] header = { "Identifier", "Email", "Score","InterviewDate" };
            csvWriter.writeNext(header);
            int counter = 0;
            for(InterviewResponseDomain interviewResponseDomain:interviewResponseDomainList){
                String id="",email="",score="",interviewdate="";
                List<String[]> line = new ArrayList<String[]>();
                if(interviewResponseDomain.getEmail()!=null){
                    id=interviewResponseDomain.getEmail();
                    email=interviewResponseDomain.getEmail();
                }
                if(interviewResponseDomain.getSummary()!=null && interviewResponseDomain.getSummary().getInsightsRankingScaled() !=null){
                    score=interviewResponseDomain.getSummary().getInsightsRankingScaled().toString();
                }
                if(interviewResponseDomain.getLastStatusChangeDate()!=null){
                    interviewdate=interviewResponseDomain.getLastStatusChangeDate().substring(0,19);
                }
                logger.info("adding line "+id+","+email+","+score+","+interviewdate);
                line.add(new String[] {id,email,score,interviewdate});
                csvWriter.writeAll(line);
            }
            csvWriter.close();
        }
        catch (Exception e){
            succesFulRun=false;
            logger.error("Exception while writing to csv",e);
        }
        return succesFulRun;
    }

    public static List<PositionResponse> convertToPositionDomain(JsonArray jsonObject){
        logger.info("Converted to json object start");
        Type listType = new TypeToken<ArrayList<PositionResponse>>(){}.getType();
        Gson gson = new Gson();
        List<PositionResponse> positionResponseDomainsList = gson.fromJson(jsonObject,listType);
        logger.info("Converted to json array with size"+positionResponseDomainsList.size());
        return positionResponseDomainsList;
    }
}
