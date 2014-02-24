package jahmm.calculators;

import jahmm.RegularHmmBase;
import jahmm.observables.Observation;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import jutlis.lists.ListArray;
import jutlis.tuples.Tuple3;

/**
 *
 * @author kommusoft
 * @param <TAlpha> The type of alpha (in most cases a nD-array of doubles).
 * @param <TBeta> The type of beta (in most cases a nD-array of doubles;
 * equivalent to TAlpha).
 */
public abstract class ForwardBackwardCalculatorRaw<TAlpha, TBeta> implements ForwardBackwardCalculator<TAlpha, TBeta> {

    @Override
    public <O extends Observation> TAlpha computeAlpha(RegularHmmBase<? super O> hmm, O... oseq) {
        return this.computeAlpha(hmm, new ListArray<>(oseq));
    }

    @Override
    public <O extends Observation> TBeta computeBeta(RegularHmmBase<? super O> hmm, O... oseq) {
        return this.computeBeta(hmm, new ListArray<>(oseq));
    }

    @Override
    public <O extends Observation> Tuple3<TAlpha, TBeta, Double> computeAll(RegularHmmBase<? super O> hmm, O... oseq) {
        return this.computeAll(hmm, new ListArray<>(oseq));
    }

    /**
     * Computes the probability of occurrence of an observation sequence given a
     * Hidden Markov Model. This computation computes the <code>alpha</code>
     * array as a side effect.
     *
     * @param <O>
     * @param oseq
     * @param hmm
     * @return
     * @see #ForwardBackwardCalculator(List, Hmm, EnumSet)
     */
    @Override
    public <O extends Observation> double computeProbability(RegularHmmBase<O> hmm, List<? extends O> oseq) {
        return computeProbability(hmm, EnumSet.of(ComputationType.ALPHA), oseq);
    }

    /**
     * Computes the probability of occurrence of an observation sequence given a
     * Hidden Markov Model. This computation computes the <code>alpha</code>
     * array as a side effect.
     *
     * @param <O>
     * @param oseq
     * @param flags determines which tasks should be executed by the calculator
     * @param hmm
     * @return
     * @see #ForwardBackwardCalculator(List, Hmm, EnumSet)
     */
    @Override
    public <O extends Observation> double computeProbability(RegularHmmBase<O> hmm, Collection<ComputationType> flags, O... oseq) {
        return this.computeProbability(hmm, flags, new ListArray<>(oseq));
    }

    /**
     * Computes the probability of occurrence of an observation sequence given a
     * Hidden Markov Model. This computation computes the <code>alpha</code>
     * array as a side effect.
     *
     * @param <O>
     * @param oseq
     * @param hmm
     * @return
     * @see #ForwardBackwardCalculator(List, Hmm, EnumSet)
     */
    @Override
    public <O extends Observation> double computeProbability(RegularHmmBase<O> hmm, O... oseq) {
        return this.computeProbability(hmm, new ListArray<>(oseq));
    }

}