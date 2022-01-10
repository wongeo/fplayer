package com.feng.media;

public class PlayerError {
	public static final int MEDIA_ERROR_BASE = -1000;

	public static final int ERROR_ALREADY_CONNECTED = MEDIA_ERROR_BASE;
	public static final int ERROR_NOT_CONNECTED = MEDIA_ERROR_BASE - 1;
	public static final int ERROR_UNKNOWN_HOST = MEDIA_ERROR_BASE - 2;
	public static final int ERROR_CANNOT_CONNECT = MEDIA_ERROR_BASE - 3;
	public static final int ERROR_IO = MEDIA_ERROR_BASE - 4;
	public static final int ERROR_CONNECTION_LOST = MEDIA_ERROR_BASE - 5;
	public static final int ERROR_MALFORMED = MEDIA_ERROR_BASE - 7;
	public static final int ERROR_OUT_OF_RANGE = MEDIA_ERROR_BASE - 8;
	public static final int ERROR_BUFFER_TOO_SMALL = MEDIA_ERROR_BASE - 9;
	public static final int ERROR_UNSUPPORTED = MEDIA_ERROR_BASE - 10;
	public static final int ERROR_END_OF_STREAM = MEDIA_ERROR_BASE - 11;

	// Not technically an error.
	public static final int INFO_FORMAT_CHANGED = MEDIA_ERROR_BASE - 12;
	public static final int INFO_DISCONTINUITY = MEDIA_ERROR_BASE - 13;

	// Custom ERROR for corrupt NAL
	public static final int ERROR_CompletionButNotEnd = MEDIA_ERROR_BASE - 98;
	public static final int ERROR_CORRUPT_NAL = MEDIA_ERROR_BASE - 99;

	public static final int MEDIA_ENDPOINT_ERROR = -2147483648;
	public static final int MEDIA_BADSOURCE = -2147483600;
	public static final int MEDIA_NOT_EXIST = -2147483601;

	public static boolean isNetworkError(int errCode) {
		return errCode == ERROR_NOT_CONNECTED || errCode == ERROR_UNKNOWN_HOST || errCode == ERROR_MALFORMED || errCode == ERROR_OUT_OF_RANGE || errCode == ERROR_END_OF_STREAM || errCode == ERROR_NOT_CONNECTED || errCode == ERROR_CANNOT_CONNECT || errCode == MEDIA_ENDPOINT_ERROR || errCode == MEDIA_ERROR_BASE || errCode == ERROR_IO || errCode == ERROR_CompletionButNotEnd;
	}

	public static boolean isMediaError(int errCode) {
		return errCode == ERROR_NOT_CONNECTED || errCode == ERROR_UNKNOWN_HOST || errCode == ERROR_MALFORMED || errCode == ERROR_OUT_OF_RANGE || errCode == ERROR_END_OF_STREAM || errCode == ERROR_NOT_CONNECTED || errCode == MEDIA_ENDPOINT_ERROR || errCode == MEDIA_ERROR_BASE || errCode == ERROR_CANNOT_CONNECT || errCode == ERROR_IO || errCode == MEDIA_BADSOURCE || errCode == ERROR_CompletionButNotEnd;
	}

	public static boolean isConnectionError(int errCode) {
		return errCode == ERROR_NOT_CONNECTED || errCode == ERROR_MALFORMED || errCode == ERROR_END_OF_STREAM || errCode == ERROR_CANNOT_CONNECT || errCode == MEDIA_ENDPOINT_ERROR || errCode == ERROR_IO || errCode == ERROR_CompletionButNotEnd;
	}
}
