package deri.sensor.utils;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class PasswordConstraint implements Constraint {
	private String target;
	private String message;
	
	public PasswordConstraint(String target, String message) {
		super();
		this.target = target;
		this.message = message;
	}

	@Override
	public void validate(Component comp, Object obj)
			throws WrongValueException {
		if(obj != null && !obj.toString().equals("")){
			String value = obj.toString();
			if(!value.equals(this.target)){
				throw new WrongValueException(comp,this.message);
			}
		}
	}

}
