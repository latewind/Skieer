package cn.net.ecode.skieer.constant;

/**
 * 
 * @author Li Shang Qing
 *
 */
public enum Constant {
	PAGE_INIT_INDEX(1),
	PAGE_SIZE(1000),
	DATA_DETECT_PAGE_SIZE(10),
    MAX_QUEUE_SIZE(1000);
	private Integer value;

	private Constant(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public Integer getValue() {
		return value;
	}

}
