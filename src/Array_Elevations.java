/*
    I, Nick Milanovic, 000292701 certify that this material is my original work.
    No other person's work has been used without due acknowledgement
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Array_Elevations {
    public static void main(String[] args) {

        String fileName = "src/ELEVATIONS.txt";

        int numOfRows = 0;
        int numOfCols = 0;
        int minPeak = 0;
        int radius = 0;
        int highest = 0;

        int[][] elevationValues;

        try
        {
            Scanner input = new Scanner(new File(fileName));
            numOfRows = input.nextInt();
            numOfCols = input.nextInt();
            minPeak = input.nextInt();
            radius = input.nextInt();

            elevationValues = new int[numOfRows][numOfCols];
            // Immediately extracting the highest value once the file is read, first iterating through rows
            for(int row=0; row<numOfRows; row++)
            {
                // Next we iterate through every column of each row
                for(int col=0; col<numOfCols; col++)
                {
                    // Assigning the next number to elevationValues and checking if it is higher than the last
                    elevationValues[row][col] = input.nextInt();
                    if(elevationValues[row][col] >= highest)
                    {
                        highest = elevationValues[row][col];
                    }
                }
            }
            input.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        // Q1 - Find frequency of lowest elevation
        long startTime = System.nanoTime();
        int[] lowest = findLowest(elevationValues);
        System.out.println("The lowest elevation is " + lowest[0] + " and it appears " + lowest[1] + " times.");

        // Q2 & 3 - Finding peaks and the min distance between them
        int[][] peaks = findPeaks(elevationValues, minPeak, radius, numOfRows, numOfCols);
        localPeakDistance(peaks);

        // Q4 - Find most frequent elevation
        startTime = System.nanoTime();
        String mostFrequent = getMostFrequent(elevationValues, highest);
        System.out.println(mostFrequent);
        long stopTime = System.nanoTime();
        System.out.printf(" [Time = %d us]\n", (stopTime - startTime) / 1000);

    }

    /*
        Function required for q1 -> finding frequency of lowest elevation
        Input will be the 2D elevation array
        Output will be a regular integer array of lowest elevation and amount of appearances
     */
    public static int[] findLowest(int[][] elevation)
    {
        int lowestElevation = elevation[0][0];
        int lowCount = 0;

        // Iterate through the rows
        for(int row=0; row<elevation.length; row++)
        {
            // Iterate through the columns of each row
            for(int col=0; col<elevation[row].length; col++)
            {
                // Check if the current elevation is lower than the lowest elevation to find our
                // lowest number. Everytime a lower number is found, reset the counter
                if(elevation[row][col] < lowestElevation)
                {
                    lowCount = 0;
                    lowestElevation = elevation[row][col];
                }
                // Everytime the lowest number is found will increase the counter
                if(elevation[row][col] == lowestElevation)
                {
                    lowCount++;
                    break;
                }
            }
        }
        return new int[] {lowestElevation, lowCount};
    }

    /*
        Function required for q2 -> finding local peaks
        Inputs will be the 2D elevation int array, the minPeak, radius, number of rows and number of columns
        Output will be a 2D array of the peaks and their coordinates
     */
    public static int[][] findPeaks(int[][] elevation, int minPeak, int radius, int numOfRows, int numOfCol)
    {
        int counter = 0;
        // To make the algorithm more efficient, I want to exclude the outer radius and only iterate through the
        // inner elevations
        int[][] findPeaks = new int[(numOfRows * numOfRows) - (((radius*2) * numOfRows) + ((radius*2) * numOfCol))][2];
        // Iterate through the rows
        for(int row=radius; row<elevation.length - radius; row++)
        {
            // Iterate through the columns of the rows
            for (int col = radius; col < elevation[row].length - radius; col++)
            {
                // Check to see if the current number is greater than the minPeak
                if (elevation[row][col] >= minPeak)
                {
                    // If so, add a boolean tag that is set to true
                    boolean tag = true;
                    // Iterating through the bottom 13 and top 13 array rows
                    for (int i = row - radius; i <= (row + radius); i++)
                    {
                        // Iterating through the left 13 and right 13 columns of each row
                        for (int j = col - radius; j <= (col + radius); j++)
                        {
                            // Check to see if the current values is less than or equal to the values within the radius
                            // and if the current row/column is within the radius.
                            if ((row != i) && (col != j) && (elevation[row][col] <= elevation[i][j]))
                            {
                                tag = false;
                            }
                        }
                    }
                    // Counter goes up to keep track of all the peaks, which also get stored in the 2D array findPeaks
                    // along with their coordinates that are required for q3
                    if (tag)
                    {
                        counter++;
                        System.out.println("Local peak is: " + elevation[row][col]);
                        findPeaks[counter][0] = row;
                        findPeaks[counter][1] = col;
                    }
                }
            }
        }
        return findPeaks;
    }

    /*
        Function required for q3 that will take the results of q2 as the input
        Outputting a string with the minimum distance between peaks
     */
    public static void localPeakDistance(int[][] peaks)
    {
        int distanceRows=0;
        int distanceCol=0;
        double realDistance=1000;
        double tempRealDistance;
        // Iterate through the rows
        for(int localPeak=0; localPeak< peaks.length; localPeak++)
        {
            // Ensuring that none of the coordinates are 0 to cut processing time
            if(peaks[localPeak][0] != 0 && peaks[localPeak][1] != 0)
            {
                // Comparing all the integers to determine the ones with the least distance between them
                for(int nextPeak=localPeak+1; nextPeak<peaks.length; nextPeak++)
                {
                    // Ensuring neither x nor y are 0 to iterate through less numbers and accuracy, because 0 values will
                    // always have a minimum distance of 0
                    if(peaks[localPeak][0] != 0 && peaks[localPeak][1] != 0 && peaks[nextPeak][0] != 0 && peaks[nextPeak][1] != 0)
                    {
                        // Comparing the x coordinates of the two peaks
                        distanceRows = peaks[localPeak][0] - peaks[nextPeak][0];
                        // Comparing the y coordinates of the two peaks
                        distanceCol = peaks[localPeak][1] - peaks[nextPeak][1];
                        // Using the formula to determine the real distance between them
                        tempRealDistance = Math.sqrt((distanceRows * distanceRows) + (distanceCol * distanceCol));
                        // Check to see if it is smaller than the previous distance recorded
                        if(tempRealDistance <= realDistance)
                        {
                            realDistance = tempRealDistance;
                        }
                    }
                }
            }
        }
        System.out.println("The minimum distance between two peaks is: " + Math.round(realDistance * 100.0)/100.0);
    }



    /*
        Function required for q4 to find the most frequent elevation
        Inputting the 2D elevation array as well as the highest number that was determined when reading the file
        Outputting a string containing the most frequent numbers and how often it appears
     */
    public static String getMostFrequent(int[][] elevation, int highest)
    {
        int count = 1;
        int tempCount = 1;
        int temp = 0;
        int[][] mostFrequent = new int[highest + 1][1]; // creating a new 2D array

        // Iterate through all the rows
        for(int row=0; row< elevation.length; row++)
        {
            // Iterate through all the columns of each row
            for(int col=0; col< elevation[row].length; col++)
            {
                // Counting how many times the most frequent int appears
                int currentElevation = elevation[row][col];
                mostFrequent[currentElevation][0]+=1;
            }
        }
        // Iterating over the mostFrequent 2D array to get the most common number
        for(int i=0; i< mostFrequent.length; i++)
        {
            if(mostFrequent[i][0] >= tempCount)
            {
                tempCount = mostFrequent[i][0];
                count = tempCount;
                temp = i;
            }
        }
        return "The number " + temp + " occurs the most. A total of " + count + " times";
    }

}
