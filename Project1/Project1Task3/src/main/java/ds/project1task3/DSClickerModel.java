package ds.project1task3;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Feb 10, 2023
 *
 * This model records the answer history.
 * The history will be refreshed once /getResults being accessed.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DSClickerModel {
    /*
    k - v -> option - total counts
     */
    private Map<String, Integer> answerMap;
    /**
     * Constructor: initialize the answerMap with A-D and 0.
     * @throws IOException
     */
    DSClickerModel() {
        this.answerMap = new HashMap<>();
        this.answerMap.put("A", 0);
        this.answerMap.put("B", 0);
        this.answerMap.put("C", 0);
        this.answerMap.put("D", 0);
    }

    /**
     * Add the counts of the option by 1.
     * @param option A/B/C/D
     */
    public void addResult(String option) {
        if (option != null) {
            this.answerMap.put(option, answerMap.get(option) + 1);
        }
    }

    /**
     * Get the total counts of the option.
     * @param option A/B/C/D
     * @return total counts of option
     */
    public Integer getTotal(String option) {
        return this.answerMap.get(option);
    }


}
