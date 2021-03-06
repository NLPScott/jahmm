package jahmm.learn;

import jahmm.InputHmm;
import jahmm.InputHmmBase;
import jahmm.RegularHmm;
import jahmm.RegularHmmBase;
import jahmm.observables.InputObservationTuple;
import jahmm.observables.ObservationInteger;
import jahmm.observables.OpdfInteger;
import jahmm.observables.OpdfIntegerFactory;
import jahmm.toolbox.InputMarkovGeneratorBase;
import jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import jahmm.toolbox.RegularMarkovGeneratorBase;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author kommusoft
 */
public class InputBaumWelchLearnerBaseTest extends TestCase {

    final static private double DELTA = 5.E-3;

    private RegularHmmBase<ObservationInteger> hmm;
    private InputHmmBase<ObservationInteger, Integer> ihmm;
    private List<List<ObservationInteger>> sequences;
    private List<List<InputObservationTuple<Integer, ObservationInteger>>> isequences;
    private KullbackLeiblerDistanceCalculator klc;

    @Override
    protected void setUp() {
        hmm = new RegularHmmBase<>(3, new OpdfIntegerFactory(10));
        hmm.getOpdf(0).fit(new ObservationInteger(1), new ObservationInteger(2));

        ihmm = new InputHmmBase<>(3, new OpdfIntegerFactory(10), 0x00);
        ihmm.getOpdf(0, 0).fit(new ObservationInteger(1), new ObservationInteger(2));

        RegularMarkovGeneratorBase<ObservationInteger,RegularHmmBase<ObservationInteger>> mg = new RegularMarkovGeneratorBase<>(hmm);
        InputMarkovGeneratorBase<ObservationInteger, Integer,InputHmmBase<ObservationInteger, Integer>> img = new InputMarkovGeneratorBase<>(ihmm, new OpdfInteger(0x01));

        sequences = new ArrayList<>();
        isequences = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            sequences.add(mg.observationSequence(100));
            isequences.add(img.interactionSequence(100));
        }

        klc = new KullbackLeiblerDistanceCalculator();
    }

    /**
     *
     */
    @Test
    public void testThinBaumWelch() {
        //TODO: input instead of regular.
        /* Model sequences using BW algorithm */
        RegularBaumWelchLearnerBase<ObservationInteger,RegularHmmBase<ObservationInteger>> bwl = new RegularBaumWelchLearnerBase<>();
        RegularHmm<ObservationInteger,RegularHmmBase<ObservationInteger>> bwHmm = bwl.learn(hmm, sequences);
        assertEquals(0., klc.distance(bwHmm, hmm), DELTA);

        InputBaumWelchLearnerBase<ObservationInteger, Integer,InputHmmBase<ObservationInteger, Integer>> ibwl = new InputBaumWelchLearnerBase<>();
        InputHmmBase<ObservationInteger, Integer> ibwHmm = ibwl.learn(ihmm, isequences);
        assertEquals(0., klc.distance(ibwHmm, ihmm), DELTA);

        /* Model sequences using the scaled BW algorithm */
        RegularBaumWelchScaledLearnerBase<ObservationInteger,RegularHmmBase<ObservationInteger>> bwsl = new RegularBaumWelchScaledLearnerBase<>();
        bwHmm = bwsl.learn(hmm, sequences);
        assertEquals(0., klc.distance(bwHmm, hmm), DELTA);
    }

}
