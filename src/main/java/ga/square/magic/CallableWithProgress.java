package ga.square.magic;

import java.util.concurrent.Callable;

public interface CallableWithProgress<R> extends Callable<R> {
    R currentProgress();
}
