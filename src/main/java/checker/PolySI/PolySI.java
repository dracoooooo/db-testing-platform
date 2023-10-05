package checker.PolySI;

import checker.Checker;
import checker.IsolationLevel;
import checker.PolySI.verifier.Pruning;
import checker.PolySI.verifier.SIVerifier;
import history.History;
import util.Profiler;

import java.util.Properties;

public class PolySI<VarType, ValType> implements Checker<VarType, ValType> {
    private final Boolean noPruning = false;

    private final Boolean noCoalescing = false;

    private final Boolean dotOutput = false;

    private final Profiler profiler = Profiler.getInstance();

    public static final String NAME = "PolySI";
    public static IsolationLevel ISOLATION_LEVEL;

    static {

        System.err.println(System.getProperty("java.library.path"));
        System.loadLibrary("monosat");
    }

    public PolySI(Properties config) {
        ISOLATION_LEVEL = IsolationLevel.valueOf(config.getProperty("db.isolation"));
    }

    @Override
    public boolean verify(History<VarType, ValType> history) {
        history.addInitSession();
        Pruning.setEnablePruning(!noPruning);
        SIVerifier.setCoalesceConstraints(!noCoalescing);
        SIVerifier.setDotOutput(dotOutput);

        profiler.startTick("ENTIRE_EXPERIMENT");
        var pass = true;
        var verifier = new SIVerifier<>(history);
        pass = verifier.audit();
        profiler.endTick("ENTIRE_EXPERIMENT");

        if (pass) {
            System.err.println("[[[[ ACCEPT ]]]]");
        } else {
            System.err.println("[[[[ REJECT ]]]]");
        }
        history.removeInitSession();
        return pass;
    }
}
