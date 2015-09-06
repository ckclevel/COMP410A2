package testing;
/**
 * Represents a machine with limited memory that can sort tape drives.
 */
import java.lang.Math;

public class TapeSorter {

    private int memorySize;
    private int tapeSize;
    public int[] memory;

    public TapeSorter(int memorySize, int tapeSize) {
        this.memorySize = memorySize;
        this.tapeSize = tapeSize;
        this.memory = new int[memorySize];
    }

    /**
     * Sorts the first `size` items in memory via quicksort
     */
    public void quicksort(int size) {
        // TODO: Implement me for 10 points [DONE]
    	quicksort(0,size-1);
    }
    
    public void quicksort(int left, int right){
    	int lo = left;
    	int hi = right;
    	int pivot = memory[lo+(hi-lo)/2];
    	while(lo <= hi) {
    		while(memory[lo] < pivot) { lo++;}
    		while(memory[hi] > pivot) { hi--;}
    		if(lo<=hi) {
    			int temp = memory[lo];
    			memory[lo] = memory[hi];
    			memory[hi] = temp;
    			lo++;
    			hi--;
    		}
    	}
    	if(left < hi) { quicksort(left, hi);}
    	if(lo < right) { quicksort(lo, right);}
    }

    /**
     * Reads in numbers from drive `in` into memory (a chunk), sorts it, then writes it out to a different drive.
     * It writes chunks alternatively to drives `out1` and `out2`.
     * 
     * If there are not enough numbers left on drive `in` to fill memory, then it should read numbers until the end of
     * the drive is reached.
     *
     * Example 1: Tape size = 8, memory size = 2
     * ------------------------------------------
     *   BEFORE:
     * in: 4 7 8 6 1 3 5 7
     *
     *   AFTER:
     * out1: 4 7 1 3 _ _ _ _
     * out2: 6 8 5 7 _ _ _ _
     *
     *
     * Example 2: Tape size = 10, memory size = 3
     * ------------------------------------------
     *   BEFORE:
     * in: 6 3 8 9 3 1 0 7 3 5
     *
     *   AFTER:
     * out1: 3 6 8 0 3 7 _ _ _ _
     * out2: 1 3 9 5 _ _ _ _ _ _
     *
     *
     * Example 3: Tape size = 13, memory size = 4
     * ------------------------------------------
     *   BEFORE:
     * in: 6 3 8 9 3 1 0 7 3 5 9 2 4
     *
     *   AFTER:
     * out1: 3 6 8 9 2 3 5 9 _ _ _ _ _
     * out2: 0 1 3 7 4 _ _ _ _ _ _ _ _
     */
    public void initialPass(TapeDrive in, TapeDrive out1, TapeDrive out2) {
        // TODO: Implement me for 15 points! [DONE]
    	int i,j,k;
    	int leftover = tapeSize % memorySize;
    	in.reset(); out1.reset(); out2.reset(); //reset the counters for each drive
    	
    	for(i=0;i<(tapeSize/memorySize);i++) {
    		for(j=0;j<memorySize;j++) {
	    			memory[j] = in.read();
    		}
    		quicksort(memorySize);
    		if(i%2 == 0) {
    	    	for(k=0;k<memorySize;k++) {
    	    		out1.write(memory[k]);
    	    	}
    		} else {
    	    	for(k=0;k<memorySize;k++) {
    	    		out2.write(memory[k]);
    	    	}
    		}
    	}
    	if(leftover > 0) {
    		for(j=0; j<leftover;j++) {
    			memory[j] = in.read();
    		}
    		quicksort(leftover);
    		if(i%2 == 0) {
    	    	for(k=0;k<leftover;k++) {
    	    		out2.write(memory[k]);
    	    	}
    		} else {
    	    	for(k=0;k<leftover;k++) {
    	    		out1.write(memory[k]);
    	    	}
    		}
    		
    	}
    	
    	in.reset();
    	out1.reset();
    	out2.reset();
   
    }

    /**
     * Merges the first chunk on drives `in1` and `in2` and writes the sorted, merged data to drive `out`.
     * The size of the chunk on drive `in1` is `size1`.
     * The size of the chunk on drive `in2` is `size2`.
     *
     *          Example
     *       =============
     *
     *  (BEFORE)
     * in1:  [ ... 1 3 6 8 9 ... ]
     *             ^
     * in2:  [ ... 2 4 5 7 8 ... ]
     *             ^
     * out:  [ ... _ _ _ _ _ ... ]
     *             ^
     * size1: 4, size2: 4
     *
     *   (AFTER)
     * in1:  [ ... 1 3 6 8 9 ... ]
     *                     ^
     * in2:  [ ... 2 4 5 7 8 ... ]
     *                     ^
     * out:  [ ... 1 2 3 4 5 6 7 8 _ _ _ ... ]
     *                             ^
     */
    public void mergeChunks(TapeDrive in1, TapeDrive in2, TapeDrive out, int size1, int size2) {
        // TODO: Implement me for 10 points [DONE]
    	int i=0;
    	int total = size1+size2;
    	int c1 = in1.read(),cc1=0;
    	int c2 = in2.read(),cc2=0;
    	
    	while(i<total) {
    		if(c1<=c2 && cc1<size1) {
    			out.write(c1);
    			c1 = in1.read();
    			cc1++;
    		} else if(c2<c1 && cc2<size2) {
    			out.write(c2);
    			c2 = in2.read();
    			cc2++;
    		} else if(cc1 < size1) {
    			out.write(c1);
    			c1 = in1.read();
    			cc1++;
    		} else if(cc2 < size2) {
    			out.write(c2);
    			c2 = in2.read();
    			cc2++;
    		}
	    	i++;
    	}
    }
 

    /**
     * Merges chunks from drives `in1` and `in2` and writes the resulting merged chunks alternatively to drives `out1`
     * and `out2`.
     *
     * The `runNumber` argument denotes which run this is, where 0 is the first run.
     *
     * -- Math Help --
     * The chunk size on each drive prior to merging will be: memorySize * (2 ^ runNumber)
     * The number of full chunks on each drive is: floor(tapeSize / (chunk size * 2))
     *   Note: If the number of full chunks is 0, that means that there is a full chunk on drive `in1` and a partial
     *   chunk on drive `in2`.
     * The number of leftovers is: tapeSize - 2 * chunk size * number of full chunks
     *
     * To help you better understand what should be happening, here are some examples of corner cases (chunks are
     * denoted within curly braces {}):
     *
     * -- Even number of chunks --
     * in1 ->   { 1 3 5 6 } { 5 7 8 9 }
     * in2 ->   { 2 3 4 7 } { 3 5 6 9 }
     * out1 ->  { 1 2 3 3 4 5 6 7 }
     * out2 ->  { 3 5 5 6 7 8 9 9 }
     *
     * -- Odd number of chunks --
     * in1 ->   { 1 3 5 } { 6 7 9 } { 3 4 8 }
     * in2 ->   { 2 4 6 } { 2 7 8 } { 0 3 9 }
     * out1 ->  { 1 2 3 4 5 6 } { 0 3 3 4 8 9 }
     * out2 ->  { 2 6 7 7 8 9 }
     *
     * -- Number of leftovers <= the chunk size --
     * in1 ->   { 1 3 5 6 } { 5 7 8 9 }
     * in2 ->   { 2 3 4 7 }
     * out1 ->  { 1 2 3 3 4 5 6 7 }
     * out2 ->  { 5 7 8 9 }
     *
     * -- Number of leftovers > the chunk size --
     * in1 ->   { 1 3 5 6 } { 5 7 8 9 }
     * in2 ->   { 2 3 4 7 } { 3 5 }
     * out1 ->  { 1 2 3 3 4 5 6 7 }
     * out2 ->  { 3 5 5 7 8 9 }
     *
     * -- Number of chunks is 0 --
     * in1 ->   { 2 4 5 8 9 }
     * in2 ->   { 1 5 7 }
     * out1 ->  { 1 2 4 5 5 7 8 9 }
     * out2 ->
     */
    public void doRun(TapeDrive in1, TapeDrive in2, TapeDrive out1, TapeDrive out2, int runNumber) {
        // TODO: Implement me for 15 points
    	int chunkSize = memorySize * (int)(Math.pow(2,runNumber));
    	int numFullChunks = tapeSize/(chunkSize*2);
    	int leftovers = tapeSize - 2 * chunkSize * numFullChunks;
    	int i;
    	
    	for(i=0; i<numFullChunks;i++) {
    		if(i%2 == 0) {
	    		mergeChunks(in1,in2,out1,chunkSize,chunkSize);
    		} else {
	    		mergeChunks(in1,in2,out2,chunkSize,chunkSize);
    		}
    	}

		if(leftovers > 0) {
			if(numFullChunks%2==0) {
				mergeChunks(in1,in2,out1,chunkSize,(leftovers-chunkSize));
			} else {
				mergeChunks(in1,in2,out2,chunkSize,(leftovers-chunkSize));
			}
		} else {
			if(numFullChunks%2==0) {
				mergeChunks(in1,in2,out1,leftovers,0);
			} else {
				mergeChunks(in1,in2,out2,leftovers,0);
			}
		}
    	
    }

    /**
     * Sorts the data on drive `t1` using the external sort algorithm. The sorted data should end up on drive `t1`.
     *
     * Initially, drive `t1` is filled to capacity with unsorted numbers.
     * Drives `t2`, `t3`, and `t4` are empty and are to be used in the sorting process.
     */
    public void sort(TapeDrive t1, TapeDrive t2, TapeDrive t3, TapeDrive t4) {
        // TODO: Implement me for 15 points
    	int i;
    	int passes = (int)Math.ceil(Math.log((double)tapeSize/(double)memorySize)/Math.log(2)); 
    	initialPass(t1,t3,t4);
    	t1.reset();
    	t2.reset();
    	t3.reset();
    	t4.reset();
    	
    	for(i=0;i<passes;i++) {
    		if(i==0 || i%2 == 0) {
    			doRun(t3,t4,t1,t2,i);
    		} else {
    			doRun(t1,t2,t3,t4,i);
    		}
    		t1.reset();
        	t2.reset();
        	t3.reset();
        	t4.reset();
    	}
    	if(i%2==0) {
    		for(i=0;i<tapeSize;i++) {
    			t1.write(t3.read());
    		}
    	}
		t1.reset();
    	t2.reset();
    	t3.reset();
    	t4.reset();

    }

    public static void main(String[] args) {


        TapeSorter tapeSorter = new TapeSorter(5, 10);
        TapeDrive t1 = TapeDrive.generateRandomTape(10);
        TapeDrive t2 = new TapeDrive(10);
        TapeDrive t3 = new TapeDrive(10);
        TapeDrive t4 = new TapeDrive(10);

       // tapeSorter.sort(t1,t2,t3,t4);
        
       tapeSorter.sort(t1, t2, t3, t4);
        int last = Integer.MIN_VALUE;
        boolean sorted = true;
        for (int i = 0; i < 10; i++) {
            int val = t1.read();
            sorted &= last <= val; // <=> sorted = sorted && (last <= val);
            last = val;
        }
        if (sorted)
            System.out.println("Random input sorted correctly\n");
        else
            System.out.println("Not sorted!"); 
        	t1.printTape(); 


	
		// Example from textbook //

      	TapeDrive sortedDrive = new TapeDrive(13);

        tapeSorter = new TapeSorter(3, 13);
		t1 = new TapeDrive(13);
		t2 = new TapeDrive(13);
		t3 = new TapeDrive(13);
		t4 = new TapeDrive(13);
        writeToDrive(t1, new int[] {81, 94, 11, 96, 12, 35, 17, 99, 28, 58, 41, 75, 15 });
	tapeSorter.sort(t1,t2,t3,t4);

	writeToDrive(sortedDrive, new int[] {11, 12, 15, 17, 28, 35, 41, 58, 75, 81, 94, 96, 99 });
	System.out.println("Book example sorted correctly:  " 
			   + tapeSorter.isEqual(t1,sortedDrive) + "\n");
		t1.printTape();
		



		// Sorting with repeats //

	int[] lotsOfRepeats = { 0,  0,  0,  3, -1,  0,  0,  1, -2,  3,  2, -1,  4,  1,  1,  4,
			       -2,  2,  1,  2,  2,  3, -1,  1, -2, -2,  1,  0,  1,  0,  4, -2 };

	int[] sortedReps = { -2, -2, -2, -2, -2, -1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  1,  
			     1,  1,  1,  1,  1,  1,  2,  2,  2,  2,  3, 3,  3,  4,  4,  4 };
	
	tapeSorter = new TapeSorter(9, 32);
	t1 = new TapeDrive(32);
	t2 = new TapeDrive(32);
	t3 = new TapeDrive(32);
	t4 = new TapeDrive(32);
	sortedDrive = new TapeDrive(32);

	writeToDrive(t1, lotsOfRepeats);
	writeToDrive(sortedDrive, sortedReps);
	tapeSorter.sort(t1,t2,t3,t4);

	System.out.println("Sorting repetitious numbers performed correctly:  " 
			   + tapeSorter.isEqual(t1,sortedDrive) + "\n");



		// All numbers the same //

	tapeSorter = new TapeSorter(4, 11);
	t1 = new TapeDrive(11);
	t2 = new TapeDrive(11);
	t3 = new TapeDrive(11);
	t4 = new TapeDrive(11);
	sortedDrive = new TapeDrive(11);

	writeToDrive(t1, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
	writeToDrive(sortedDrive, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
	tapeSorter.sort(t1,t2,t3,t4);

	System.out.println("Sorting a drive of all 0's performed correctly:  " 
			   + tapeSorter.isEqual(t1,sortedDrive) + "\n");



		// Sorting with tapeSize=1, memSize=1 //

	tapeSorter = new TapeSorter(1,1);
	t1 = new TapeDrive(1);
	t2 = new TapeDrive(1);
	t3 = new TapeDrive(1);
	t4 = new TapeDrive(1);
	sortedDrive = new TapeDrive(1);

	writeToDrive(t1, new int[] {7});
	writeToDrive(sortedDrive, new int[] {7});
	tapeSorter.sort(t1,t2,t3,t4);

	System.out.println("Sorting tapeSize=1, memSize=1 performed correctly:  " 
			   + tapeSorter.isEqual(t1,sortedDrive) + "\n");
	

	
		// Sorting with tapeSize=1, memSize=100 //

	tapeSorter = new TapeSorter(100,1);
	tapeSorter.sort(t1,t2,t3,t4);

	System.out.println("Sorting tapeSize=1, memSize=100 performed correctly:  " 
			   + tapeSorter.isEqual(t1,sortedDrive) + "\n");



	
		//  Sorting with tapeSize=100, memSize=1 //

	int[] first100 = { 27, 17, 60,  2, 51, 11, 98, 87,  8, 10, 70, 41, 34, 
				     68, 54,  4, 72,  0, 74, 39, 59, 42, 58, 19, 22, 36, 
				     79, 48, 61, 45, 57, 47, 89,  6, 77, 28, 14, 91, 25,  
				      7, 52, 38, 62, 31, 65, 16, 33, 81, 64, 15, 49, 84,  
				      3, 88, 46,  5, 56, 24, 76,  1, 93, 35, 94, 26, 20, 
				     92, 96, 21, 43, 12, 53, 13, 86, 29, 40, 85, 18, 83, 
				     90, 95, 97, 23,  9, 78, 71, 63, 73, 82, 69, 99, 55, 
				     75, 32, 37, 50, 44, 66, 67, 30, 80 };

	int[] first100sort = { 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 
				       13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 
				       26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
				       39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 
				       52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 
				       65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 
				       78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 
				       91, 92, 93, 94, 95, 96, 97, 98, 99 };
       
	tapeSorter = new TapeSorter(1,100);
	t1 = new TapeDrive(100);
	t2 = new TapeDrive(100);
	t3 = new TapeDrive(100);
	t4 = new TapeDrive(100);
	sortedDrive = new TapeDrive(100);

	writeToDrive(t1, first100);
	writeToDrive(sortedDrive, first100sort);
	tapeSorter.sort(t1,t2,t3,t4);

	System.out.println("Sorting tapeSize=100, memSize=1 performed correctly:  " 
			   + tapeSorter.isEqual(t1,sortedDrive) + "\n");
	



		// Sorting with tapeSize=100, memSize=1000 //

	tapeSorter = new TapeSorter(1000, 100);
	writeToDrive(t1, first100);

	tapeSorter.sort(t1,t2,t3,t4);
	System.out.println("Sorting tapeSize=100, memSize=1000 performed correctly:  " 
			   + tapeSorter.isEqual(t1,sortedDrive) + "\n");




		// Sorting with tapeSize much (moderately?) bigger than memSize //

	tapeSorter = new TapeSorter(11,100000);
	t1 = new TapeDrive(100000);
	t2 = new TapeDrive(100000);
	t3 = new TapeDrive(100000);
	t4 = new TapeDrive(100000);	

        t1 = TapeDrive.generateRandomTape(100000);
	tapeSorter.sort(t1, t2, t3, t4);

        sorted = true;
	last = Integer.MIN_VALUE;
        for (int i=0; ((i < 100000) && sorted); i++) {
            int val = t1.read();
            sorted &= last <= val; // <=> sorted = sorted && (last <= val);
            last = val;
        }
        if (sorted)
            System.out.println("Moderate tape size random input sorted correctly\n");
        else
            System.out.println("Not sorted!");
	
    }


    private static void writeToDrive (TapeDrive t1, int[] intArr) {
	t1.reset();
	for (int i=0; i < intArr.length; i++) t1.write(intArr[i]);
    }


    private boolean isEqual (TapeDrive t1, TapeDrive t2) {
	t1.reset();
	t2.reset();

	for (int i=0; i < this.tapeSize; i++ ) 
	    if (t1.read() != t2.read()) return false;
	return true;
	
    }


}
