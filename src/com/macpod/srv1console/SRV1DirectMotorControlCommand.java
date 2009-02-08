package com.macpod.srv1console;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SRV1DirectMotorControlCommand extends SRV1Command {
	byte leftMotor;
	byte rightMotor;
	
	public SRV1DirectMotorControlCommand() {
	}
	
	public boolean process(DataInputStream in, DataOutputStream out)
			throws Exception {
		
		return false;
	}

	public boolean repeat() {
		return false;
	}


	public boolean repeatOnFail() {
		return true;
	}

}
