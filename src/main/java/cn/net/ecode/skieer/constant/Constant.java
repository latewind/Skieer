package cn.net.ecode.skieer.constant;

/**
 * 
 * @author Li Shang Qing
 *
 */
public enum Constant {
	PAGE_INIT_INDEX(1),
    PAGE_INIT_INDEX_ODD(1),
	PAGE_INIT_INDEX_EVEN(2),
	PAGE_MAX_SIZE(1000),
	DATA_DETECT_PAGE_SIZE(100),
    MAX_QUEUE_SIZE(1000),
	MAX_FETCHER_NUM(2),
	MAX_COLUMN_LEN(512);
	private Integer value;

	 Constant(Integer value) {
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
