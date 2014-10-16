package tkLogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import storage.Storage;
import tkLibrary.Constants;
import tkLibrary.Task;

/*
 * Basically the logic functions should be very clear and simple.
 * So that UserInterface have to parse the command and call the logic.
 * This maybe be a little bit more complicated so I can help Ben.
 */
public class Logic {
	private Storage storage;

	public Logic(String fileName) {
		storage = new Storage(fileName);
	}
	
	public String add(Task task) {
		if (isFreeTimeslots(task)) {
			storage.add(task);
		} else {
			return Constants.MESSAGE_CLASHING_TIMESLOTS;
		}
		return Constants.MESSAGE_TASK_ADDED;
	}
	
	public String delete(Task task) throws Exception{
		if(isExistingTask(task)) {
			storage.delete(task);
		} else {
			return Constants.MESSAGE_TASK_DOES_NOT_EXIST;
		}
		return Constants.MESSAGE_TASK_DELETED;
	}

	private boolean isFreeTimeslots(Task task){
		return (storage.queryFreeSlot(task.getStartTime()) || storage.queryFreeSlot(task.getEndTime()));
	}
	
	private boolean isExistingTask(Task task){
		return (storage.queryTask(task));
	}

	public String edit(Task taskToBeEdited, Task editedTask) throws Exception {
		if (delete(taskToBeEdited).equals(Constants.MESSAGE_TASK_DOES_NOT_EXIST)) {
			return Constants.MESSAGE_EDIT_TASK_DOES_NOT_EXIST;
		}
		if (add(editedTask).equals(Constants.MESSAGE_CLASHING_TIMESLOTS)) {
			return Constants.MESSAGE_EDIT_TASK_CLASHES;
		}
		return Constants.MESSAGE_TASK_EDITED;
	}
	
	/*
	 * for listing, only mention the time, the frequency and the state.
	 */
	public ArrayList<Task> list(Task task) {
		ArrayList<Task> res = new ArrayList<Task>();
		ArrayList<Task> list = storage.load();
		
		boolean isDeadline = false, isEvent = false, isFloating = false;
		if (task.getDescription()!=null) {
			isDeadline = task.getDescription().toLowerCase().contains("deadline");
			isEvent = task.getDescription().toLowerCase().contains("event");
			isFloating = task.getDescription().toLowerCase().contains("floating");
		}
		if (!isDeadline && !isEvent && !isFloating) {
			isDeadline = isEvent = isFloating = true;
		}
		
		for (Task item : list) {
			if (isIncluded(task, item)) {
				if (isDeadline && item.getStartTime() != null && item.getEndTime() == null) {
					res.add(item);
				} else if (isEvent && item.getStartTime() != null && item.getEndTime() != null) {
					res.add(item);
				} else if (isFloating && item.getStartTime() == null && item.getEndTime() == null) {
					res.add(item);
				} 
			}
		}
		res = sort(res);
		return res;
	}

	public String undo() {
		storage.undo();
		return Constants.MESSAGE_UNDO_DONE;
	}
	
	public String clear() {
		storage.clear();
		return Constants.MESSAGE_TASK_CLEARED;
	}

	/*
	 * searching a task by keyword
	 * mention the description and the location
	 */
	public ArrayList<Task> search(String keyword) {
		ArrayList<Task> list = storage.load();
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for(Task item : list){
			if (item.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
			   (item.getLocation() != null && item.getLocation().toLowerCase().contains(keyword.toLowerCase()))){
				searchResults.add(item);
			}
		}
		searchResults = sort(searchResults);
		return searchResults;
	}

	private ArrayList<Task> sort(ArrayList<Task> list) {
		Collections.sort(list, new Comparator<Task>() {
	        @Override
	        public int compare(Task  task1, Task  task2) {
	        	String t1 = convertCalendarToString(task1.getStartTime(), Constants.FORMAT_DATE_CMP);
	        	String t2 = convertCalendarToString(task2.getStartTime(), Constants.FORMAT_DATE_CMP);
	        	
	            if (t1 == null && t2 != null ) {
	            	return 1;
	            } else if (t1 != null && t2 == null ) {
	            	return -1;
	            } else if (t1 == null && t2 == null ) {
	            	return 0;
	            } 
	            return t1.compareTo(t2);
	        }
	    });
		return list;
	}
	
	private boolean isIncluded(Task feature, Task task) {
		if (feature.getStartTime() != null) {
			String featureTime = convertCalendarToString(feature.getStartTime(), Constants.FORMAT_DATE);
			String startTime = convertCalendarToString(task.getStartTime(), Constants.FORMAT_DATE);
			String endTime = convertCalendarToString(task.getEndTime(), Constants.FORMAT_DATE);
			if (startTime == null && endTime == null) {
				return false;
			} else if (startTime != null && endTime !=null) {
				if (startTime.compareTo(featureTime) > 0 
						|| featureTime.compareTo(endTime) > 0) {
					return false;
				}
			} else if (!featureTime.equals(startTime) && !featureTime.equals(endTime)) {
				return false;
			}
		}
		
		if (feature.getFrequencyType() != null
				&& feature.getFrequencyType()!=task.getFrequencyType()) {
			return false;
		}
		
		if (feature.getState() != null
				&& feature.getState()!=task.getState()) {
			return false;
		}
		
		return true;
	}
	
	private String convertCalendarToString(Calendar time, String FORMAT) {
		if (time == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);     
		return formatter.format(time.getTime());
	}
}
