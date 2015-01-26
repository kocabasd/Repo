import java.util.Hashtable;

public class BSConstant {
	BSInstance instance;
	Integer n, sdd, slotLength, maxBreakTime, maxLunchBreakTime, minWorkingPeriod, maxWorkingPeriod, workLimit, minBreakExceedsWorkLimit, minBreakLength, maxBreakLength;
	Hashtable<Integer, Shift> shiftSDD    = new Hashtable<Integer, Shift>();
	Hashtable<Integer, Integer> daySDD    = new Hashtable<Integer, Integer>();
	Integer[] shiftMinusRequirement, weight = new Integer[7];
	
	public void modelConstraints(BSInstance instance){		
		this.instance = instance;
		n = instance.getN();
		sdd = instance.getSdd();
		shiftMinusRequirement = new Integer[n];
		shiftMinusRequirement = instance.getShiftMinusRequirement();
		slotLength = instance.getSlotLength();
		weight = instance.getWeight();
		shiftSDD = instance.getShiftSDD();
		daySDD = instance.getDaySDD();
		maxBreakTime = instance.getMaxBreakTime();
		maxLunchBreakTime = instance.getMaxLunchBreakTime();
		minWorkingPeriod = instance.getMinWorkingPeriod();
		maxWorkingPeriod = instance.getMaxWorkingPeriod();
		workLimit = instance.getWorkLimit();
		minBreakExceedsWorkLimit = instance.getMinBreakExceedsWorkLimit();
		minBreakLength = instance.getMinBreakLength();
		maxBreakLength = instance.getMaxBreakLength();
	}
}

