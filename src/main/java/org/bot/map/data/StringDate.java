package org.bot.map.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StringDate implements Comparable<StringDate> {
   //dd.MM.yyyy
   private String date;

   //date = yyyyMMdd
   public StringDate(String date) {
      if (date == null) {
         return;
      } else if (date.contains(".") || date.length() <= 2) {
         this.date = date;
      } else {
         String year = date.substring(0, 4);
         String month = date.substring(4, 6);
         String day = date.substring(6);
         this.date = day + "." + month + "." + year;
      }
   }

   public void setDate(long d) {
      DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
      Date date = new Date(d * 1000);

      String dateMessage = this.date;
      if (dateMessage == null || dateMessage.isEmpty()) {
         dateMessage = dateFormat.format(date);
      } else {
         String[] arr = dateFormat.format(date).split("\\.");
         if (dateMessage.length() < 3) {
            dateMessage += "." + arr[1];
         }
         if (dateMessage.length() < 6) {
            dateMessage += "." + arr[2];
         }
      }
      this.date = dateMessage;
   }

   @Override
   public int compareTo(@NotNull StringDate o) {
      String d1 = mapped();
      String d2 = o.mapped();
      return d1.compareTo(d2);
   }

   //yyyyMMdd
   public String mapped() {
      String[] dates = date.split("\\.");
      return dates[2] + dates[1] + dates[0];
   }

   @Override
   public String toString() {
      return date;
   }
}


