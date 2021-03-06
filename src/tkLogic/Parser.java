package tkLogic;

import java.util.ArrayList;
import java.util.Calendar;

import tkLibrary.CommandType;
import tkLibrary.Constants;
import tkLibrary.Task;
import tkLibrary.UserInput;
import tkLibrary.CommandKey;

//@author A0110493N
/**
 * Parser is a singleton class
 * 
 * It formats a string of command and convert it into a UserInput object
 * containing the CommandType and the Task(s) details
 */
public class Parser {
    private static Parser theOneParser;
    private UserInput userInput;
    private Task task;

    // Variables for the Task
    private String completeDescription;
    private String completeLocation;
    // private String frequencyType;
    // private int frequencyValue;
    private String startTimeAndDate;
    private String endTimeAndDate;
    private String priority;
    private String state;

    // Temporary time and date variables
    private String[] startTime;
    private String[] endTime;
    private String[] startDate;
    private String[] endDate;

    private boolean isEdit;

    private Parser() {
    }

    /**
     * getInstance
     * 
     * @return a Parser object
     */
    public static Parser getInstance() {
        if (theOneParser == null) {
            theOneParser = new Parser();
        }
        return theOneParser;
    }

    /**
     * format
     * 
     * @param userCommand string of input from user
     * 
     * @return user instruction and task(s) details
     * 
     * @throws Exception invalid formats
     */
    public UserInput format(String userCommand) throws Exception {
        resetParser();
        String[] userInputArray = splitUserInput(userCommand);
        userInput = new UserInput(determineCommandType(userInputArray[0]), task);
        if (userInputArray.length > 1) {
            parseAll(userInputArray);
            if (isEdit) {
                userInput.setEditedTask(task);
            }
        }
        return userInput;
    }

    private void resetParser() {
        task = new Task();
        completeDescription = null;
        completeLocation = null;
        // frequencyType = null;
        // frequencyValue = 0;
        startTime = new String[0];
        endTime = new String[0];
        startDate = new String[0];
        endDate = new String[0];
        startTimeAndDate = null;
        endTimeAndDate = null;
        priority = Constants.PRIORITY_NULL;
        state = Constants.STATE_NULL;
        isEdit = false;
    }

    private String[] splitUserInput(String userCommand) {
        return userCommand.trim().split("\\s+");
    }

    private CommandType determineCommandType(String commandTypeString)
            throws Exception {
        if (commandTypeString.equalsIgnoreCase("add")) {
            return CommandType.ADD;
        } else if (commandTypeString.equalsIgnoreCase("delete")) {
            return CommandType.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("undo")) {
            return CommandType.UNDO;
        } else if (commandTypeString.equalsIgnoreCase("edit")) {
            return CommandType.EDIT;
        } else if (commandTypeString.equalsIgnoreCase("clear")) {
            return CommandType.CLEAR;
        } else if (commandTypeString.equalsIgnoreCase("search")) {
            return CommandType.SEARCH;
        } else if (commandTypeString.equalsIgnoreCase("list")) {
            return CommandType.LIST;
        } else if (commandTypeString.equalsIgnoreCase("set")) {
            return CommandType.SET;
        } else if (commandTypeString.equalsIgnoreCase("redo")) {
            return CommandType.REDO;
        } else if (commandTypeString.equalsIgnoreCase("sync")) {
            return CommandType.SYNC;
        } else if (commandTypeString.equalsIgnoreCase("exit")) {
            return CommandType.EXIT;
        } else if (commandTypeString.equalsIgnoreCase("help")) {
            return CommandType.HELP;
        } else {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_COMMAND,
                    commandTypeString));
        }
    }

    private void parseAll(String[] userInputArray) throws Exception {
        parseInput(userInputArray);
        parseTime();
        setTaskFields();
    }

    private void parseInput(String[] userInputArray) throws Exception {
        ArrayList<String> word = new ArrayList<String>();
        String newWord;
        CommandKey newCommandKey;
        CommandKey commandKey = determineCommandKey("description");
        for (int i = 1; i < userInputArray.length; i++) {
            newWord = userInputArray[i];
            newCommandKey = determineCommandKey(newWord);
            if (newCommandKey != null) {
                executeCmdKey(word, commandKey);
                commandKey = determineCommandKey(newWord);
                word = new ArrayList<String>();
            } else {
                word.add(newWord);
            }
        }
        executeCmdKey(word, commandKey);
    }

    private void parseTime() throws Exception {
        if (startTime.length == 0 && startDate.length != 0) {
            setStartTime0000();
        }
        if (endTime.length == 0 && endDate.length != 0) {
            setEndTime2359();
        }
        if (startDate.length == 0 && endDate.length == 0) {
            setDateToToday();
        }
        if (endTime.length != 0 && endTimeAndDate == null) {
            setEndTimeAndDate();
        }
        if (startTime.length != 0 && startTimeAndDate == null) {
            setStartTimeAndDate();
        }
    }

    private void setTaskFields() throws Exception {
        task.setDescription(completeDescription);
        task.setLocation(completeLocation);
        // task.setFrequency(frequencyValue);
        // task.setFrequencyType(frequencyType);
        task.setStartTime(startTimeAndDate);
        task.setEndTime(endTimeAndDate);
        task.setState(state);
        task.setPriority(priority);
    }

    private CommandKey determineCommandKey(String commandKeyString) {
        if (commandKeyString.equalsIgnoreCase("description")) {
            return CommandKey.DESCRIPTION;
        } else if (commandKeyString.equalsIgnoreCase("from")) {
            return CommandKey.FROM;
        } else if (commandKeyString.equalsIgnoreCase("by")) {
            return CommandKey.BY;
        } else if (commandKeyString.equalsIgnoreCase("to")) {
            return CommandKey.TO;
        } else if (commandKeyString.equalsIgnoreCase("at")) {
            return CommandKey.AT;
        } else if (commandKeyString.equalsIgnoreCase("on")) {
            return CommandKey.ON;
        } else if (commandKeyString.equalsIgnoreCase("correct")) {
            return CommandKey.EDIT;
            // }
            // else if (commandKeyString.equalsIgnoreCase("every")) {
            // return CommandKey.EVERY;
        } else if (commandKeyString.equalsIgnoreCase("priority")) {
            return CommandKey.PRIORITY;
        } else if (commandKeyString.equalsIgnoreCase("status")) {
            return CommandKey.STATE;
        } else {
            return null;
        }
    }

    private void executeCmdKey(ArrayList<String> word, CommandKey commandKey)
            throws Exception {
        switch (commandKey) {
            case DESCRIPTION:
                parseDescription(word);
                break;
            case FROM:
                parseStartTime(word);
                break;
            case TO:
                parseEndTime(word);
                break;
            case ON:
                parseDate(word);
                break;
            case BY:
                parseDeadline(word);
                break;
            case AT:
                parseLocation(word);
                break;
            // case EVERY:
            // parseFrequency(word);
            // break;
            case EDIT:
                changeTaskObject(word);
                break;
            case PRIORITY:
                parsePriority(word);
                break;
            case STATE:
                parseState(word);
                break;
        }
    }

    private void setStartTime0000() {
        Calendar.getInstance();
        String[] currentTime = new String[2];
        currentTime[0] = "00";
        currentTime[1] = "00";
        startTime = currentTime;
    }

    private void setEndTime2359() {
        Calendar.getInstance();
        String[] currentTime = new String[2];
        currentTime[0] = "23";
        currentTime[1] = "59";
        endTime = currentTime;
    }

    private void setDateToToday() throws Exception {
        Calendar calendar = Calendar.getInstance();
        String[] date = new String[3];
        date[0] = "" + calendar.get(5);
        date[1] = determineMonth("" + (calendar.get(2) + 1));
        date[2] = "" + calendar.get(1);
        startDate = date;
        endDate = date;
    }

    private void setEndTimeAndDate() {
        if (endDate.length == 0) {
            endTimeAndDate = getTime(endTime, startDate);
        } else {
            endTimeAndDate = getTime(endTime, endDate);
        }
    }

    private void setStartTimeAndDate() {
        startTimeAndDate = getTime(startTime, startDate);
    }

    private void parseDescription(ArrayList<String> description) {
        if (description.size() != 0) {
            completeDescription = description.get(0);
            for (int i = 1; i < description.size(); i++) {
                completeDescription += " " + description.get(i);
            }
            completeDescription = completeDescription.replaceAll("/", "");
        }
    }

    private void parseStartTime(ArrayList<String> time) throws Exception {
        String[] startingTime = new String[2];
        try {
            startTime = updateTime(time, startingTime);
        } catch (Exception e) {
            try {
                String[] date = getDateValue(time);
                startDate = date;
                startingTime[0] = "00";
                startingTime[1] = "00";
                startTime = startingTime;
            } catch (Exception f) {
                throw e;
            }
        }
    }

    private void parseEndTime(ArrayList<String> time) throws Exception {
        String[] endingTime = new String[2];
        try {
            endTime = updateTime(time, endingTime);
        } catch (Exception e) {
            try {
                String[] date = getDateValue(time);
                endDate = date;
                endingTime[0] = "23";
                endingTime[1] = "59";
                endTime = endingTime;
            } catch (Exception f) {
                throw e;
            }
        }
    }

    private void parseDate(ArrayList<String> day) throws Exception {
        String[] date = getDateValue(day);
        determineDate(date);
    }

    private void parseDeadline(ArrayList<String> time) throws Exception {
        parseStartTime(time);
        if (startTime[0] == "00" && startTime[1] == "00") {
            startTime[0] = "23";
            startTime[1] = "59";
        }
        endTime = new String[0];
        endDate = new String[0];
    }

    private void parseLocation(ArrayList<String> location) {
        if (location.size() != 0) {
            completeLocation = location.get(0);
            for (int i = 1; i < location.size(); i++) {
                completeLocation += " " + location.get(i);
            }
            completeLocation = completeLocation.replaceAll("/", "");
        }
    }

    // private void parseFrequency(ArrayList<String> frequency) {
    // frequencyType = frequency.get(1);
    // frequencyValue = Integer.valueOf(frequency.get(0));
    // }

    private void changeTaskObject(ArrayList<String> word) throws Exception {
        parseTime();
        setTaskFields();
        resetParser();
        executeCmdKey(word, determineCommandKey("description"));
        isEdit = true;
    }

    private void parsePriority(ArrayList<String> priorityLevel) throws Exception {
        if (priorityLevel.size() == 1) {
            priority = priorityLevel.get(0);
        } else {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_PRIORITY,
                    priorityLevel));
        }
    }

    private void parseState(ArrayList<String> status) throws Exception {
        if (status.size() == 1) {
            state = status.get(0);
        } else {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_STATE,
                    status));
        }
    }

    private String determineMonth(String month) throws Exception {
        if (month.length() == 3) {
            return check3LettersMonth(month);
        } else {
            return checkNumericalMonth(month);
        }
    }

    private String getTime(String[] time, String[] date) {
        return date[1] + " " + date[0] + " " + date[2] + " " + time[0] + ":"
                + time[1] + ":00";
    }

    private String[] updateTime(ArrayList<String> time, String[] requiredTime)
            throws Exception {
        String newTime = "";
        newTime = getInputTime(time, newTime);
        convertTimeValue(requiredTime, newTime);
        checkTimeFormat(requiredTime, newTime);
        return requiredTime;
    }

    private String[] getDateValue(ArrayList<String> day) throws Exception {
        String[] date = new String[3];
        if (day.size() == 1) {
            convertSingleLineDate(day, date);
        } else if (day.size() == 3) {
            convert3WordsDate(day, date);
        } else {
            String originalDate = getOriginalDate(day);
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_DATE,
                    originalDate));
        }
        checkDay(date[0]);
        checkYear(date[2]);
        return date;
    }

    private void determineDate(String[] date) {
        if (startTime.length != 0) {
            if (endTime.length != 0) {
                if (startDate.length == 0) {
                    startDate = date;
                }
                if (endDate.length == 0) {
                    endDate = date;
                }
            } else {
                startDate = date;
            }
        } else if (endTime.length != 0) {
            endDate = date;
        } else {
            startDate = date;
            endDate = date;
        }
    }

    private String check3LettersMonth(String month) throws Exception {
        if (month.equalsIgnoreCase("jan") || month.equalsIgnoreCase("feb")
                || month.equalsIgnoreCase("mar") || month.equalsIgnoreCase("apr")
                || month.equalsIgnoreCase("may") || month.equalsIgnoreCase("jun")
                || month.equalsIgnoreCase("jul") || month.equalsIgnoreCase("aug")
                || month.equalsIgnoreCase("sep") || month.equalsIgnoreCase("oct")
                || month.equalsIgnoreCase("nov") || month.equalsIgnoreCase("dec")) {
            return month;
        } else {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_MONTH,
                    month));
        }
    }

    private String checkNumericalMonth(String month) throws Exception {
        try {
            int monthValue = Integer.valueOf(month) - 1;
            if (monthValue == 0) {
                return "Jan";
            } else if (monthValue == 1) {
                return "Feb";
            } else if (monthValue == 2) {
                return "Mar";
            } else if (monthValue == 3) {
                return "Apr";
            } else if (monthValue == 4) {
                return "May";
            } else if (monthValue == 5) {
                return "Jun";
            } else if (monthValue == 6) {
                return "Jul";
            } else if (monthValue == 7) {
                return "Aug";
            } else if (monthValue == 8) {
                return "Sep";
            } else if (monthValue == 9) {
                return "Oct";
            } else if (monthValue == 10) {
                return "Nov";
            } else if (monthValue == 11) {
                return "Dec";
            } else {
                throw new Exception(String.format(
                        Constants.EXCEPTIONS_INVALID_MONTH, month));
            }
        } catch (NumberFormatException e) {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_MONTH,
                    month));
        }
    }

    private String getInputTime(ArrayList<String> time, String newTime)
            throws Exception {
        if (time.size() == 0) {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_TIME,
                    time));
        } else {
            newTime = time.get(0);
            for (int i = 1; i < time.size(); i++) {
                newTime += time.get(i);
            }
        }
        return newTime;
    }

    private void convertTimeValue(String[] requiredTime, String newTime)
            throws Exception {
        if (newTime.length() == 5 && newTime.substring(4, 5).equals("h")) {
            // for the time format: hhmmH
            try {
                Integer.valueOf(newTime.substring(0, 4));
            } catch (NumberFormatException e) {
                throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_TIME,
                        newTime));
            }
            requiredTime[0] = newTime.substring(0, 2);
            requiredTime[1] = newTime.substring(2, 4);

        } else if (newTime.contains("m")) {
            // for the time format ending with am/pm
            try {
                Float timeValue =
                        Float.valueOf(newTime.substring(0, newTime.length() - 2));
                Float minuteValue =
                        timeValue - Float.valueOf(timeValue.intValue() + "");
                getMinute(requiredTime, minuteValue);
                getHour(requiredTime, newTime, timeValue);
            } catch (NumberFormatException e) {
                throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_TIME,
                        newTime));
            }
        }
    }

    private void checkTimeFormat(String[] requiredTime, String newTime)
            throws Exception {
        if (requiredTime[0] == null || Integer.valueOf(requiredTime[0]) < 0
                || Integer.valueOf(requiredTime[0]) > 23
                || Integer.valueOf(requiredTime[1]) < 0
                || Integer.valueOf(requiredTime[1]) > 59) {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_TIME,
                    newTime));
        }
    }

    private void convertSingleLineDate(ArrayList<String> day, String[] date)
            throws Exception {
        String enteredDate = day.get(0);
        checkForSlash(enteredDate, day.get(0));
        date[0] = enteredDate.substring(0, enteredDate.indexOf("/"));
        enteredDate = enteredDate.replaceFirst(date[0] + "/", "");
        checkForSlash(enteredDate, day.get(0));
        date[1] = enteredDate.substring(0, enteredDate.indexOf("/"));
        enteredDate = enteredDate.replaceFirst(date[1] + "/", "");
        date[1] = determineMonth(date[1]);
        date[2] = enteredDate;
        if (date[2].length() == 2) {
            if (Integer.valueOf(date[2]) > 50) {
                date[2] = "19" + date[2];
            } else {
                date[2] = "20" + date[2];
            }
        }
    }

    private void convert3WordsDate(ArrayList<String> day, String[] date)
            throws Exception {
        date[0] = day.get(0);
        date[1] = determineMonth(day.get(1));
        date[2] = day.get(2);
    }

    private String getOriginalDate(ArrayList<String> day) {
        String originalDate = "";
        originalDate = day.get(0);
        for (int i = 1; i < day.size(); i++) {
            originalDate += " " + day.get(i);
        }
        return originalDate;
    }

    private void checkDay(String year) throws Exception {
        try {
            Integer.valueOf(year);
        } catch (NumberFormatException e) {
            throw new Exception(
                    String.format(Constants.EXCEPTIONS_INVALID_DAY, year));
        }
    }

    private void checkYear(String day) throws Exception {
        try {
            Integer.valueOf(day);
        } catch (NumberFormatException e) {
            throw new Exception(
                    String.format(Constants.EXCEPTIONS_INVALID_YEAR, day));
        }
    }

    private void getMinute(String[] requiredTime, Float minuteValue) {
        int minute;
        minute = Float.valueOf(minuteValue * 100).intValue();
        if (minute < 10) {
            requiredTime[1] = "0" + minute;
        } else {
            requiredTime[1] = "" + minute;
        }
    }

    private void getHour(String[] requiredTime, String newTime, Float timeValue)
            throws Exception {
        int hour;
        if (newTime.contains("a") && timeValue == 12) {
            hour = (timeValue.intValue() - 12);
        } else if (newTime.contains("a")) {
            if (timeValue.intValue() > 11) {
                throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_TIME,
                        newTime));
            }
            hour = timeValue.intValue();
        } else if (newTime.contains("p") && timeValue != 12) {
            hour = (timeValue.intValue() + 12);
        } else {
            hour = timeValue.intValue();
        }
        if (hour < 10) {
            requiredTime[0] = "0" + hour;
        } else {
            requiredTime[0] = "" + hour;
        }
    }

    private void checkForSlash(String checkingDate, String originalDate)
            throws Exception {
        if (!checkingDate.contains("/")) {
            throw new Exception(String.format(Constants.EXCEPTIONS_INVALID_DATE,
                    originalDate));
        }
    }
}