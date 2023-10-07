package Part2;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable{
    private final BlockingQueue<ArrayList<String>> resultsBuffer;
    private final ArrayList<String> groupResults;

    public Producer(ArrayList<String> groupResults, BlockingQueue<ArrayList<String>> resultsBuffer) {
        this.groupResults = groupResults;
        this.resultsBuffer = resultsBuffer;
    }

    @Override
    public void run() {
        try {
            resultsBuffer.put(groupResults);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
