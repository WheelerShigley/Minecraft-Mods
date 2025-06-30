package www.wheelershigley.me.configurable_sponges.utils;

public class MathFunctions {
    public static int CenteredOctahedralNumber(int depth) {
        double accumulator = 1.0;

        double _depth = (double)depth;
        accumulator += 8.0/3.0 * _depth;

        _depth *= _depth;
        accumulator += 2.0*_depth;

        _depth *= _depth;
        accumulator += 4.0/3.0 * _depth;

        return (int)Math.round(accumulator);
    }
}
