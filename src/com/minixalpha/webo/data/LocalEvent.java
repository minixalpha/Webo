package com.minixalpha.webo.data;

/**
 * 各种事件代码
 * 
 * 例如使用 intent 传递数据时，数据的名字，使用广播时事件的名字
 * 
 * @author minixalpha
 * 
 */
public class LocalEvent {
	public static final String SEND_WEIBO_SUCCEED = "com.minixalpha.webo.SEND_WEIBO_SUCCEED";
	public static final String REPOST = "com.minixalpha.webo.RETWEET";
	public static final String COMMENT = "com.minixalpha.webo.COMMENT";
	public static final String REPLY_EVENT = "com.minixalpha.webo.REPLY_EVENT";
	public static final String REPLY_SOURCE = "com.minixalpha.webo.REPLY_SOURCE";
	public static final String REPLY_ID = "com.minixalpha.webo.REPLY_ID";
	public static final String STATUS = "com.minixalpha.webo.STATUS";
	public static final String IMAGES_URL = "com.minixalpha.webo.IMAGES_URL";
	public static final String IMAGE_POS = "com.minixalpha.webo.IMAGE_POS";
}
