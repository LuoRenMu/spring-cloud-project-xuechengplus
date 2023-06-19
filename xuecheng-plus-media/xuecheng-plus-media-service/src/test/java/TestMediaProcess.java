
import cn.lomu.media.service.MediaProcessService;
import cn.lomu.media.task.VideoTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @author LoMu
 * Date  2023-05-29 3:02
 */
public class TestMediaProcess {


    public static void main(String[] args) {
        int[] ints = twoSum(new int[]{3, 2, 4}, 6);
        System.out.println(Arrays.toString(ints));
    }

    public static int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
