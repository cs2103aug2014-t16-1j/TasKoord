package storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tkLibrary.Task;
import tkLibrary.Constants;

import org.json.simple.JSONObject;

/**
 * This Converter facilitates a two way conversion between <Task> and jsonObject
 * task can be converted to jsonObject for storage to file
 * Alternatively the retrieved jsonObject from file can be converted to <Task>       
 */
public class jsonConverter {
	
	//@author A0118919U
	@SuppressWarnings("unchecked")
	public static JSONObject taskToJSON(Task task)
	{
		JSONObject jTask=new JSONObject();
		jTask.put(Constants.DESCRIPTION, task.getDescription());
		jTask.put(Constants.LOCATION, task.getLocation());
		jTask.put(Constants.STARTTIME, convertCalendarToString(task.getStartTime()));
		jTask.put(Constants.ENDTIME, convertCalendarToString(task.getEndTime()));
		jTask.put(Constants.FREQUENCY, new Integer(task.getFrequency()));
		jTask.put(Constants.SYNC_STATUS, new Integer(task.getSyncedValue()));
		
		if(task.getFrequencyType() != null){
			jTask.put(Constants.FREQUENCY_TYPE, task.getFrequencyType().toString());
		}else{
			jTask.put(Constants.FREQUENCY_TYPE, null);
		}
		
		if(task.getPriorityLevel() != null){
			jTask.put(Constants.PRIORITY_TYPE, task.getPriorityLevel().toString());
		}else{
			jTask.put(Constants.PRIORITY_TYPE,"NULL");
		}
		
		if(task.getState() != null){
			jTask.put(Constants.STATE_TYPE, task.getState().toString());
		}else{
			jTask.put(Constants.STATE_TYPE, "NULL");
		}
		
		return jTask;
	}		
	
	public static Task jsonToTask(JSONObject obj){
		Task temp = null;
		try{
			temp = new Task();
			temp.setDescription((String) obj.get(Constants.DESCRIPTION));
			temp.setLocation((String) obj.get(Constants.LOCATION));
			temp.setStartTime((String) obj.get(Constants.STARTTIME));
			temp.setEndTime((String) obj.get(Constants.ENDTIME));
			temp.setSyncedValue(Integer.parseInt(obj.get(Constants.SYNC_STATUS).toString()));
			temp.setFrequency(Integer.parseInt(obj.get(Constants.FREQUENCY).toString()));
			
			if(obj.get(Constants.FREQUENCY_TYPE) != null){
				temp.setFrequencyType((String) obj.get(Constants.FREQUENCY_TYPE));
			}else{
				temp.setFrequencyType(null);
			}
			
			if(obj.get(Constants.PRIORITY_TYPE) != null){
				temp.setPriority((String) obj.get(Constants.PRIORITY_TYPE));
			}else{
				temp.setPriority("NULL");
			}
			
			if(obj.get(Constants.STATE_TYPE) != null){
				temp.setState((String) obj.get(Constants.STATE_TYPE));
			}else{
				temp.setState("NULL");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return temp;	
	}
	
	private static String convertCalendarToString(Calendar time) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATE_HOUR);     
		return formatter.format(time.getTime());
	}
}