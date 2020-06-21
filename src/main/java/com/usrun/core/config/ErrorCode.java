package com.usrun.core.config;

public interface ErrorCode {


  int LOGOUT_CODE = 100;
  int MAINTENANCE = 101;
  int FORCE_UPDATE = 102;

  int SYSTEM_ERROR = 500;

  int FIELD_REQUIRED = 1000;
  int ACCESS_DENT = 1001;
  int UPDATE_FAIL = 1002;
  int IMAGE_INVALID = 1003;
  int OTP_INVALID = 1004;
  int OTP_EXPIRED = 1005;
  int OTP_SENT = 1006;

  int USER_NOT_FOUND = 2000;
  int USER_CAN_NOT_JOIN = 2001;
  int USER_JOINED = 2002;
  int USER_QUIT_FAIL = 2003;
  int USER_CREATE_FAIL = 2004;
  int USER_NOT_IN = 2005;
  int USER_IS_OWNER = 2006;
  int USER_DOES_NOT_PERMISSION = 2007;
  int USER_FOLLOW_FAIL = 2009;
  int FOLLOWER_NOT_FOUND = 2010;
  int USER_EXISTED = 2011;
  int USER_IS_CONNECTED = 2012;
  int USER_CAN_NOT_CONNECT = 2013;
  int USER_EMAIL_IS_USED = 2014;
  int OPEN_ID_IS_USED = 2015;
  int USER_CAN_NOT_DISCONNECT = 2016;
  int USER_LOGIN_FAIL = 2017;
  int USER_IS_IN_EVENT = 2018;
  int USER_RESET_PASSWORD_FAIL = 2022;
  int USER_EMAIL_NOT_FOUND = 2023;
  int USER_EMAIL_IS_SOCIAL = 2024;
  int USER_EMAIL_VERIFIED = 2025; // new
  int USER_EMAIL_IS_NOT_STUDENT_EMAIL = 2026; // new


  int TEAM_NOT_FOUND = 3000;
  int TEAM_USER_NOT_FOUND = 3001;
  int TEAM_EXISTED = 3002;
  int TEAM_UPDATE_ROLE_FAILED = 3003;

  int EVENT_NOT_FOUND = 4000;
  int EVENT_EVENT_EXIT = 4001;
  int EVENT_UN_ACTIVE = 4002;
  int EVENT_CAN_NOT_CREATE = 4003;


  int ACTIVITY_NOT_FOUND = 5000;
  int ACTIVITY_ADD_FAIL = 5001;
  int ACTIVITY_PROCESSING_OR_DUPLICATED = 5002;
  int ACTIVITY_REQUEST_TIME_INVALID = 5003;

  int FUND_NOT_FOUND = 6000;

  int LEADER_BOARD_NOT_FOUND = 7000;
  int LEADER_BOARD_CREATE_FAIL = 7001;
  int LEADER_BOARD_NOT_SUPPORT = 7002;
  int LEADER_BOARD_ACCESS_DENIED = 7003;
  int LEADER_BOARD_DELETE_FAIL = 7004;

  int TRACK_NOT_FOUND = 8000;
  int TRACK_NOT_BELONG_TO_USER = 8001;
  int TRACK_TIMEOUT = 8002;
  int TRACK_SIG_INVALID = 8003;
}
