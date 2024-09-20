package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    private final Integer CSV_CRN_INDEX = 0;
    private final Integer CSV_SUBJECT_INDEX = 1;
    private final Integer CSV_NUM_INDEX = 2;
    private final Integer CSV_DESCRIPTION_INDEX = 3;
    private final Integer CSV_SECTION_INDEX = 4;
    private final Integer CSV_TYPE_INDEX = 5;
    private final Integer CSV_CREDITS_INDEX = 6;
    private final Integer CSV_START_INDEX = 7;
    private final Integer CSV_END_INDEX = 8;
    private final Integer CSV_DAYS_INDEX = 9;
    private final Integer CSV_WHERE_INDEX = 10;
    private final Integer CSV_SCHEDULE_INDEX = 11;
    private final Integer CSV_INSTRUCTOR_INDEX = 12;
    
    private final String JSON_SCHEDULETYPE = "scheduletype";
    private final String JSON_SUBJECT = "subject";
    private final String JSON_COURSE = "course";
    private final String JSON_SECTION = "section";
    
    
    public String convertCsvToJsonString(List<String[]> csv) {
        
        Iterator <String[]> iterator = csv.iterator();
        
        JsonObject jsonData = new JsonObject();
        
        JsonObject scheduleType = new JsonObject();
        JsonObject subject = new JsonObject();
        JsonObject course = new JsonObject();
        JsonArray section = new JsonArray();
        
        String[] headerRow = iterator.next();
        
        while (iterator.hasNext()) {
            
            /* "scheduletype" */
            String[] csvData = iterator.next();
            
            scheduleType.put(csvData[CSV_TYPE_INDEX], csvData[CSV_SCHEDULE_INDEX]);
            
            /* "subject" */
           String num = csvData[CSV_NUM_INDEX];
           String[] parts = num.split(" ");
           String subjectID = parts[0];
           String subjectNum = parts[1];
           
           subject.put(subjectID, csvData[CSV_SUBJECT_INDEX]);
           
           /* "course" */
           JsonObject subjectInfo = new JsonObject();
           
           int credits = Integer.parseInt(csvData[CSV_CREDITS_INDEX]);
           
           subjectInfo.put(SUBJECTID_COL_HEADER, subjectID);
           subjectInfo.put(NUM_COL_HEADER, subjectNum);
           subjectInfo.put(DESCRIPTION_COL_HEADER, csvData[CSV_DESCRIPTION_INDEX]);
           subjectInfo.put(CREDITS_COL_HEADER, credits);
           
           course.put(csvData[CSV_NUM_INDEX], subjectInfo);
           
           /* "section" */
           JsonObject secObject = new JsonObject();
           JsonArray instructor = new JsonArray();
           
           int crn = Integer.parseInt(csvData[CSV_CRN_INDEX]);
           
           secObject.put(CRN_COL_HEADER, crn);
           secObject.put(SUBJECTID_COL_HEADER, subjectID);
           secObject.put(NUM_COL_HEADER, subjectNum);
           secObject.put(SECTION_COL_HEADER, csvData[CSV_SECTION_INDEX]);
           secObject.put(TYPE_COL_HEADER, csvData[CSV_TYPE_INDEX]);
           secObject.put(START_COL_HEADER, csvData[CSV_START_INDEX]);
           secObject.put(END_COL_HEADER, csvData[CSV_END_INDEX]);
           secObject.put(DAYS_COL_HEADER, csvData[CSV_DAYS_INDEX]);
           secObject.put(WHERE_COL_HEADER, csvData[CSV_WHERE_INDEX]);
           
           String[] instructors = csvData[CSV_INSTRUCTOR_INDEX].split(",");
           for (String instructorName : instructors) {
               instructor.add(instructorName.trim());
           }
           secObject.put(INSTRUCTOR_COL_HEADER, instructor);
           section.add(secObject);
        }
        jsonData.put(JSON_SCHEDULETYPE, scheduleType);
        jsonData.put(JSON_SUBJECT, subject);
        jsonData.put(JSON_COURSE, course);
        jsonData.put(JSON_SECTION, section);
        
        return Jsoner.serialize(jsonData);
        
    }
    
    public String convertJsonToCsvString(JsonObject json) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        /* headers */
        csvWriter.writeNext(new String[]{CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER,
           SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER,
           WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER});
        
        JsonObject scheduleType = (JsonObject) json.get(JSON_SCHEDULETYPE);
        JsonObject subject = (JsonObject) json.get(JSON_SUBJECT);
        JsonObject course = (JsonObject) json.get(JSON_COURSE);
        JsonArray sections = (JsonArray) json.get(JSON_SECTION);
        
        /*get Strings from Json*/
        for(Object sectionObject : sections) {
            JsonObject classSection = (JsonObject) sectionObject;
            
            String crn = classSection.get(CRN_COL_HEADER).toString();
            String subjectID = (String) classSection.get(SUBJECTID_COL_HEADER);
            String subjects = (String) subject.get(subjectID);
            String num = (String) classSection.get(NUM_COL_HEADER);
            
            JsonObject courseDetails =(JsonObject) course.get(subjectID + " " + num);
            String description = (String) courseDetails.get(DESCRIPTION_COL_HEADER);
            String section = (String) classSection.get(SECTION_COL_HEADER);
            String type = (String) classSection.get(TYPE_COL_HEADER);
            String credits = (String) courseDetails.get(CREDITS_COL_HEADER).toString();
            String start = (String) classSection.get(START_COL_HEADER);
            String end = (String) classSection.get(END_COL_HEADER);
            String days = (String) classSection.get(DAYS_COL_HEADER);
            String where = (String) classSection.get(WHERE_COL_HEADER);
            String schedule = (String) scheduleType.get(type);
            
            JsonArray instructors = (JsonArray) classSection.get(INSTRUCTOR_COL_HEADER);
            StringBuilder instructorBuilder = new StringBuilder();
            for (int i = 0; i < instructors.size(); i++){
                if (i > 0) {
                    instructorBuilder.append(", ");
                }
                instructorBuilder.append((String) instructors.get(i));
            }
            String instructorsString = instructorBuilder.toString();
            String subjectNum = subjectID + " " + num;
            
            ArrayList<String> csvRow = new ArrayList<>();
            
            csvRow.add(crn);
            csvRow.add(subjects);
            csvRow.add(subjectNum);
            csvRow.add(description);
            csvRow.add(section);
            csvRow.add(type);
            csvRow.add(credits);
            csvRow.add(start);
            csvRow.add(end);
            csvRow.add(days);
            csvRow.add(where);
            csvRow.add(schedule);
            csvRow.add(instructorsString);
            
            csvWriter.writeNext(csvRow.toArray(new String[]{}));
            
    }

         
    return writer.toString();
        
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}