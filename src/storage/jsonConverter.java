package storage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tkLibrary.Task;
import tkLibrary.Constants;

import org.json.simple.JSONObject;

public class jsonConverter {
	
	@SuppressWarnings("unchecked")
	public static JSONObject taskToJSON(Task task)
	{
		JSONObject jTask=new JSONObject();
		jTask.put(Constants.DESCRIPTION, task.getDescription());
		jTask.put(Constants.LOCATION, task.getLocation());
		jTask.put(Constants.STARTTIME, convertCalendarToString(task.getStartTime()));
		jTask.put(Constants.ENDTIME, convertCalendarToString(task.getEndTime()));
		jTask.put(Constants.FREQUENCY, new Integer(task.getFrequency()));
		if(task.getFrequencyType() != null){
			jTask.put(Constants.FREQUENCY_TYPE, task.getFrequencyType().toString());
		}
		if(task.getPriorityLevel() != null){
			jTask.put(Constants.PRIORITY_TYPE, task.getPriorityLevel().toString());
		}
		if(task.getState() != null){
			jTask.put(Constants.STATE_TYPE, task.getState().toString());
		}
		if(!task.isSync()){
			jTask.put(Constants.SYNC_STATUS,"true");
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
			temp.setFrequency(Integer.parseInt(obj.get(Constants.FREQUENCY).toString()));
			temp.setFrequencyType((String) obj.get(Constants.FREQUENCY_TYPE));
			temp.setPriority((String) obj.get(Constants.PRIORITY_TYPE));
			temp.setState((String) obj.get(Constants.STATE_TYPE));
			if(obj.get(Constants.SYNC_STATUS) != null){
				temp.setSync(true);
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