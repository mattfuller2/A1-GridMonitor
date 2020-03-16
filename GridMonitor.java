import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * GridMonitor builds a system to monitor array levels.
 *
 * @author Matt Fuller
 */
public class GridMonitor implements GridMonitorInterface {

	private double[][] baseGridArray, surrSumGridArray, surrAveGridArray, deltaGridArray, deltaMinGridArray;
	private boolean[][] dangerGridArray;
	private boolean dangerStatus;
	private String testFile;
	private double a1, a2, num, COUNT = 0, aboveNum, leftNum, rightNum, belowNum, averageNum, deltaMin;
	private int sum = 0;

	/*
	 * Constructor: attempts to open and read the specified plain-text file
	 * containing current grid levels using one or more Scanners. The Scanner class
	 * will throw a FileNotFoundException if the given file name is not valid or the
	 * file cannot be read.
	 */
	public GridMonitor(String filename) throws FileNotFoundException {
		testFile = filename;
		File file = new File(testFile);
		Scanner scan = new Scanner(file);

		if (file.exists()) {

			// Reads the first line of plain-text file to determine the size of grid
			while (scan.hasNextLine()) {
				if (COUNT < 1) {
					a1 = scan.nextDouble();
					a2 = scan.nextDouble();
					COUNT++;
					baseGridArray = new double[(int) a1][(int) a2];
				}

				// Reads the file and puts the levels in the 2D array
				for (int row = 0; row < baseGridArray.length; row++) {
					for (int col = 0; col < baseGridArray[row].length; col++) {
						try {
							num = scan.nextDouble();
							if (num != 0) {
								baseGridArray[row][col] = num;
							}
						} catch (NoSuchElementException e) {

						}
					}
				}
			}
			getBaseGrid();
			scan.close();
			getSurroundingSumGrid();
			getSurroundingAvgGrid();
			getDeltaGrid();
		}
	}

	/*
	 * Returns the base grid.
	 * 
	 * @return baseGridArray
	 */
	@Override
	public double[][] getBaseGrid() {
		return baseGridArray;
	}

	/*
	 * Returns the surrounding sum grid.
	 * 
	 * @return getSurroundingSumGrid
	 */
	@Override
	public double[][] getSurroundingSumGrid() {
		surrSumGridArray = new double[(int) a1][(int) a2];
		for (int row = 0; row < baseGridArray.length; row++) {
			for (int col = 0; col < baseGridArray[row].length; col++) {
				sum = 0;
				COUNT = 0;
				try {
					if ((row - 1) >= 0) {
						aboveNum = baseGridArray[row - 1][col];
					} else
						aboveNum = baseGridArray[row][col];
					if ((row + 1) < a1) {
						belowNum = baseGridArray[row + 1][col];
					} else
						belowNum = baseGridArray[row][col];

					if ((col - 1) >= 0) {
						leftNum = baseGridArray[row][col - 1];
					} else
						leftNum = baseGridArray[row][col];

					if ((col + 1) < a2) {
						rightNum = baseGridArray[row][col + 1];
					} else
						rightNum = baseGridArray[row][col];

					sum = (int) (aboveNum + belowNum + leftNum + rightNum);
					surrSumGridArray[row][col] = sum;
				} catch (ArrayIndexOutOfBoundsException e) {

				}
			}
		}
		return surrSumGridArray;
	}

	/*
	 * Returns the surrounding average grid.
	 * 
	 * @return getSurroundingAvgGrid
	 */
	@SuppressWarnings("unused")
	@Override
	public double[][] getSurroundingAvgGrid() {
		DecimalFormat fmt = new DecimalFormat("0.000");
		surrAveGridArray = new double[(int) a1][(int) a2];
		for (int row = 0; row < a1; row++) {
			for (int col = 0; col < a2; col++) {
				averageNum = surrSumGridArray[row][col] / 4;
				surrAveGridArray[row][col] = averageNum;
			}
		}
		return surrAveGridArray;
	}

	/*
	 * Returns the delta grid.
	 * 
	 * @return getDeltaGrid
	 */
	@Override
	public double[][] getDeltaGrid() {
		deltaGridArray = new double[(int) a1][(int) a2];
		deltaMinGridArray = new double[(int) a1][(int) a2];
		for (int row = 0; row < a1; row++) {
			for (int col = 0; col < a2; col++) {
				deltaMin = surrAveGridArray[row][col] / 2;
				deltaGridArray[row][col] = Math.abs(deltaMin);
				deltaMinGridArray[row][col] = surrAveGridArray[row][col] + deltaMin;
			}
		}
		return deltaGridArray;
	}

	/*
	 * Returns the danger grid.
	 * 
	 * @return getDangerGrid
	 */
	@Override
	public boolean[][] getDangerGrid() {
		dangerGridArray = new boolean[(int) a1][(int) a2];
		for (int row = 0; row < a1; row++) {
			for (int col = 0; col < a2; col++) {
				dangerStatus = true;
				if (deltaMin < 0) {
					if ((baseGridArray[row][col] <= (surrAveGridArray[row][col] + deltaGridArray[row][col]) && (baseGridArray[row][col] >= deltaMinGridArray[row][col]))) {
						dangerStatus = false;
						dangerGridArray[row][col] = dangerStatus;
					} else {
						dangerStatus = true;
						dangerGridArray[row][col] = dangerStatus;
					}
				} else {
					if (baseGridArray[row][col] >= (surrAveGridArray[row][col] / 2) && baseGridArray[row][col] <= surrAveGridArray[row][col] + (surrAveGridArray[row][col] / 2)) {
						dangerStatus = false;
						dangerGridArray[row][col] = dangerStatus;
					} else {
						dangerStatus = true;
						dangerGridArray[row][col] = dangerStatus;
					}
				}
			}
		}
		return dangerGridArray;
	}

}
