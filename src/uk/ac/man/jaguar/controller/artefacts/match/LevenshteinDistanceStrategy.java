/*
 * Copyright (C) 2014 Fernando Osorno-Gutierrez <osornogf-at-cs.man.ac.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.man.jaguar.controller.artefacts.match;

/**
 *
 * @author osornogf
 */
public class LevenshteinDistanceStrategy {
        public static void main(String args[])
        {
            System.out.println(LevenshteinDistanceStrategy.computeLevenshteinDistance("deat", "dead"));
            System.out.println(LevenshteinDistanceStrategy.computeLevenshteinDistance("draq", "draw"));
            System.out.println(LevenshteinDistanceStrategy.computeLevenshteinDistance("paink", "paint"));
            
            System.out.println(LevenshteinDistanceStrategy.computeLevenshteinDistance("futury", "future"));
            System.out.println(LevenshteinDistanceStrategy.computeLevenshteinDistance("brokez", "broken"));
            
        }    
        private static int minimum(int a, int b, int c) {
                return Math.min(Math.min(a, b), c);
        }
        /**
         * 
         * Ref. http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
         * @param str1
         * @param str2
         * @return 
         */
        public static double computeLevenshteinDistance(String str1,String str2) {
            int lengthStr1 = str1.length();
            int lengthStr2 = str2.length();
                int[][] distance = new int[str1.length() + 1][str2.length() + 1];
 
                for (int i = 0; i <= str1.length(); i++)
                        distance[i][0] = i;
                for (int j = 1; j <= str2.length(); j++)
                        distance[0][j] = j;
 
                for (int i = 1; i <= str1.length(); i++)
                        for (int j = 1; j <= str2.length(); j++)
                                distance[i][j] = minimum(
                                                distance[i - 1][j] + 1,
                                                distance[i][j - 1] + 1,
                                                distance[i - 1][j - 1]+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

                
                
                return 1-((double)distance[str1.length()][str2.length()]/(lengthStr1*lengthStr2));    
        }    
        

}
