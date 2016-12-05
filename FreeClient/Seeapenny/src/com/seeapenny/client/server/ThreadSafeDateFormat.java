package com.seeapenny.client.server;

import java.text.*;
import java.util.Date;

public class ThreadSafeDateFormat extends DateFormat {
   
   private final SimpleDateFormat formatter;
   
   public ThreadSafeDateFormat(String pattern) {
      formatter = new SimpleDateFormat(pattern);
   }
   
   @Override
   public synchronized Date parse(String string) throws ParseException {
      return formatter.parse(string);
   }
   
   @Override
   public Date parse(String string, ParsePosition position) {
      return formatter.parse(string, position);
   }
   
   @Override
   public StringBuffer format(Date date, StringBuffer buffer, FieldPosition field) {
      return formatter.format(date, buffer, field);
   }
}
