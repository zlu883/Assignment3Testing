package AvConvObjects;

import java.util.ArrayList;

import Commands.BashCommand;

public interface AvConvObject {
	public BashCommand createBashCommand(String fileName);
	public ArrayList<String> getFormatDataList();
}
