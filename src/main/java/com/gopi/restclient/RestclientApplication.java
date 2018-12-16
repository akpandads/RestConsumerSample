package com.gopi.restclient;


import com.google.gson.JsonArray;
import com.gopi.restclient.domain.interviewdetails.GetInterviewRequestProperites;
import com.gopi.restclient.domain.interviewdetails.InterviewResponseDomain;
import com.gopi.restclient.domain.postions.PositionResponse;
import com.gopi.restclient.endpoint.CreateSessionEndpoint;
import com.gopi.restclient.endpoint.GetInterviewDetailsEndpoint;
import com.gopi.restclient.endpoint.GetPositionsEndpoint;
import com.gopi.restclient.writer.JsonToCSVWriter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootApplication
public class RestclientApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RestclientApplication.class);

    @Autowired
    CreateSessionEndpoint createSessionEndpoint;

    @Autowired
    GetInterviewDetailsEndpoint getInterviewDetailsEndpoint;

    @Autowired
    GetPositionsEndpoint getPositionsEndpoint;

    GetInterviewRequestProperites getInterviewRequestProperites;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RestclientApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Inside command line run method ");
        Map <String,String> properties= readPropertiesFile();
        String loginUrl=properties.get("loginUrl");
        String interviewUrl = properties.get("interviewUrl");
        String positionUrl = properties.get("positionUrl");
        String startDate=properties.get("startDate");
        String endDate=getSystemDate();
        getInterviewRequestProperites =createSessionEndpoint.getSession(loginUrl);
        logger.info("Getting Session End with "+getInterviewRequestProperites);
        if(getInterviewRequestProperites !=null){
            JsonArray positionJson = getPositionsEndpoint.getPositionDetails(getInterviewRequestProperites,positionUrl);
            logger.info("Getting Position Details End with "+positionJson);
            List<PositionResponse> positionResponses= JsonToCSVWriter.convertToPositionDomain(positionJson);
            logger.info("Converted to Position DOmain Object of size "+positionResponses.size());
            List<JsonArray> interviewJson = getInterviewDetailsEndpoint.getInterviewDetails(getInterviewRequestProperites,interviewUrl,startDate,endDate,positionResponses);
            logger.info("Getting Interview Details End with "+interviewJson);
            List<InterviewResponseDomain> interviewResponseDomainList= JsonToCSVWriter.convertToInterviewDomain(interviewJson);
            logger.info("Array write to file start");
            boolean writeSuccess=JsonToCSVWriter.writeToFile(interviewResponseDomainList);
            logger.info("Write to file succesful :"+writeSuccess);
            if(writeSuccess){
                logger.info("Batch run successfule. Written contents to file");
                 boolean propertyUpdateSuccesfull=writeToPropertiesFile(endDate);
                 if(propertyUpdateSuccesfull)
                    logger.info("******************* START DATE and END DATE UPDATED FOR NEXT RUn *************************");
                 else{
                     logger.error("***************** ATTENTION!! ERROR WHILE UPDATING PROPERTIES FILE. PLEASE UPDATE MANUALLY BEFORE NEXT RUN IF REQUIRED *************************");
                 }
            }
            else{
                logger.error("******************* ATTENTION!! PROPERTIES FILE NOT UPDATED FOR NEXT RUN BECAUSE OF WRITE ERROR TO CSV FILE *************************");
                logger.info("Exiting System");
                System.exit(1);
            }
            logger.info("JAR execution complete for this batch");
        }
    }

    private Map<String,String> readPropertiesFile() {
        logger.info(" Properties Read from file--------start" );
        Properties prop = new Properties();
        InputStream input = null;
        Map<String,String> propertyValues = new HashMap<>();
        try {

            input = new FileInputStream("config.properties");
            prop.load(input);
            // get the property value and print it out
            String loginUrl=prop.getProperty("loginUrl");
            String positionUrl=prop.getProperty("positionUrl");
            String interviewUrl = prop.getProperty("interviewUrl");
            String startDate=prop.getProperty("startDate");
            if(loginUrl==null || loginUrl =="" || positionUrl==null || positionUrl=="" || interviewUrl==null || interviewUrl=="" || startDate==null || startDate=="")
            {
                throw new Exception("One or more required property file entry missing in properties File");
            }
            logger.info("Found All the properties. Loading inro Map");
            logger.info("Login URL :"+prop.getProperty("loginUrl"));
            propertyValues.put("loginUrl",loginUrl);

            logger.info("Position URL :"+prop.getProperty("positionUrl"));
            propertyValues.put("positionUrl",positionUrl);

            logger.info("Interview URL :"+prop.getProperty("interviewUrl"));
            propertyValues.put("interviewUrl",interviewUrl);

            logger.info("Start Date:"+prop.getProperty("startDate"));
            propertyValues.put("startDate",startDate);


        } catch (Exception ex) {
            logger.error("**********************************************************************************************************************************");
            logger.error("**********************************************************************************************************************************");
            logger.error("*******************ERROR Reading Properties File. Property file entry is essential for this jar to run. *************************");
            logger.error("**********************************************************************************************************************************");
            logger.error("**********************************************************************************************************************************");
            logger.error("Exception is",ex);
            logger.error("Exiting System. Please use a valid properties file and retry. If issue persist contact Developer");
            System.exit(1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info(" Properties Read from file--------end" );
        return propertyValues;
    }

    private String getSystemDate() {
        logger.info("get System Date -------------------------start");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        String endDate=dateFormat.format(date);
        logger.info("System date is :"+endDate);
        logger.info("get System Date -------------------------end");
        return endDate;
    }

    private boolean writeToPropertiesFile(String newStartDate){
        boolean updateSuccesful=true;
        logger.info("Update properties file start and end date ----------------------- start");

        try{
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration("config.properties");
            propertiesConfiguration.setProperty("startDate",newStartDate);
            propertiesConfiguration.save();
        }catch (Exception e){
            updateSuccesful=false;
            logger.error("Error while updating properties file",e);

        }
        return updateSuccesful;
    }
}
