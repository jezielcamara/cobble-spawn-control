package com.cobbleworld.cobblespawncontrol.util;

import java.util.Locale;

public final class Glob {
   private Glob() {
   }

   public static boolean matches(String var0, String var1) {
      if (var0 != null && var1 != null) {
         String var2 = var0.toLowerCase(Locale.ROOT);
         String var3 = var1.toLowerCase(Locale.ROOT);
         int var4 = 0;
         int var5 = 0;
         int var6 = -1;
         int var7 = -1;

         while (var5 < var3.length()) {
            if (var4 >= var2.length() || var2.charAt(var4) != '?' && var2.charAt(var4) != var3.charAt(var5)) {
               if (var4 < var2.length() && var2.charAt(var4) == '*') {
                  var6 = var4++;
                  var7 = var5;
               } else {
                  if (var6 == -1) {
                     return false;
                  }

                  var4 = var6 + 1;
                  var5 = ++var7;
               }
            } else {
               var4++;
               var5++;
            }
         }

         while (var4 < var2.length() && var2.charAt(var4) == '*') {
            var4++;
         }

         return var4 == var2.length();
      } else {
         return false;
      }
   }
}
