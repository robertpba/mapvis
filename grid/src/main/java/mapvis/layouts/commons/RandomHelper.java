package mapvis.layouts.commons;

import mapvis.common.datatype.Tuple2;

import java.util.Collection;
import java.util.Random;

public class RandomHelper {
    public static<T> T weightedRandom (Collection<Tuple2<Integer, T>> list, Random random){
        int sum_of_weight = 0;
        for (Tuple2<Integer, T> tuple2 : list) {
            sum_of_weight += tuple2.first;
        }
        int rnd = random.nextInt(sum_of_weight);
        for (Tuple2<Integer, T> tuple2 : list) {
            if(rnd < tuple2.first)
                return tuple2.second;
            rnd -= tuple2.first;
        }
        return null;
    }
}
