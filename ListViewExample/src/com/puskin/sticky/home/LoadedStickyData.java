package com.puskin.sticky.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoadedStickyData implements Serializable {

	private static final long serialVersionUID = 1224183699688588953L;
	private List<StickyData> stickyDataList = new ArrayList<StickyData>();

	public List<StickyData> getStickyDataList() {
		return stickyDataList;
	}

	public void setStickyDataList(List<StickyData> stickyDataList) {
		this.stickyDataList = stickyDataList;
	}


}
