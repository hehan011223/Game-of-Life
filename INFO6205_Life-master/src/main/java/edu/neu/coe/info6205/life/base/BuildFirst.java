package edu.neu.coe.info6205.life.base;

import io.jenetics.EnumGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static edu.neu.coe.info6205.life.base.Game.run;

public class BuildFirst implements Problem<ISeq<Integer>, EnumGene<Integer>, Long> {

    private final ISeq<Integer> _points;

    public BuildFirst(ISeq<Integer> points) {
        _points = points;
    }

    @Override
    public Function<ISeq<Integer>, Long> fitness() {
        return p -> {
            String s = "";
            for (int i = 1; i < p.length(); i += 2) {
                int x = p.get(i - 1);
                s += x + " ";
                int y = p.get(i);
                s += y + ",";
            }
            final Game.Behavior generations = run(0L, s);
            return generations.generation;
        };
    }

    @Override
    public Codec<ISeq<Integer>, EnumGene<Integer>> codec() {
        return Codecs.ofPermutation(_points);
    }

    public static BuildFirst of(String firstPattern) {
        List<String> result = new ArrayList<>();
        for (String w : firstPattern.split(", *")) {
            result.add(w);
        }
        List<Integer> pointlist = new ArrayList<>();
        for (String s : result) {
            String[] ans = s.split(" ");
            Integer point1 = Integer.parseInt(ans[0]);
            Integer point2 = Integer.parseInt(ans[1]);
            pointlist.add(point1);
            pointlist.add(point2);
        }

        Integer[] integers = pointlist.toArray(new Integer[pointlist.size()]);

        final MSeq<Integer> points = MSeq.ofLength(integers.length);
        for (int i = 0; i < integers.length; i++) {
            final int point = integers[i];
            points.set(i, point);
        }
        return new BuildFirst(points.toISeq());
    }
}
