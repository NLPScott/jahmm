/*
 * Copyright (c) 2004-2009, Jean-Marc François. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */
package jahmm;

import jahmm.io.FileFormatException;
import jahmm.io.HmmBinaryReader;
import jahmm.io.HmmBinaryWriter;
import jahmm.io.HmmReader;
import jahmm.io.HmmWriter;
import jahmm.io.ObservationIntegerReader;
import jahmm.io.ObservationSequencesReader;
import jahmm.io.ObservationVectorReader;
import jahmm.io.OpdfGaussianMixtureReader;
import jahmm.io.OpdfGaussianMixtureWriter;
import jahmm.io.OpdfGaussianReader;
import jahmm.io.OpdfGaussianWriter;
import jahmm.io.OpdfIntegerReader;
import jahmm.io.OpdfIntegerWriter;
import jahmm.io.OpdfMultiGaussianReader;
import jahmm.io.OpdfMultiGaussianWriter;
import jahmm.io.OpdfReader;
import jahmm.io.OpdfWriter;
import jahmm.observables.Observation;
import jahmm.observables.ObservationInteger;
import jahmm.observables.ObservationVector;
import jahmm.observables.Opdf;
import jahmm.observables.OpdfInteger;
import jahmm.observables.OpdfIntegerFactory;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author kommusoft
 */
public class IOTest
        extends TestCase {

    /**
     *
     */
    protected final String integerSequences
            = "# A simple data file with integer observations.\n"
            + "1;2;3; # The first sequence\n"
            + "# And the second one\n"
            + "2;4;5;\n\n"
            + "# A big one, spawning on multiple lines\n"
            + "1;2;3; \\\n"
            + "2;4;5; # Don't forget to end the file with a newline\n";

    /**
     *
     */
    protected final String vectorSequences
            = "[ 1.1 2.2 ] ; [ 4.4 5.5 ] ; [ 4.3 6.0 ] ; [ 7.7 8.8 ] ;\n"
            + "[ 0.5 1.5 ] ; [ 1.5 2.5 ] ; [ 4.5 5.5 ] ; [ 8. 8. ] ; [ 7. 8. ] ;\n";

    /**
     *
     */
    protected final String hmmString
            = "# A simple Hmm\n"
            + "Hmm v1.0\n"
            + "NbStates 2\n\n"
            + "State Pi 0.7\n"
            + "A 0.1 0.9\n"
            + "IntegerOPDF [ .2 .3 .4 .1 ]\n\n"
            + "State Pi 0.3\n"
            + "A 0.4 0.6\n"
            + "IntegerOPDF [ .1 .1 .1 .7 ]";

    /**
     *
     */
    protected final String integerOPDFString = "IntegerOPDF [ .32 .68 ]";

    /**
     *
     */
    protected final String gaussianOPDFString = "GaussianOPDF [ 1.2 .3 ]";

    /**
     *
     */
    protected final String gaussianMixtureOPDFString
            = "GaussianMixtureOPDF [ [ 1.2 2. ] [ .1 .9 ] [ .4 .6 ] ]";

    /**
     *
     */
    protected final String multiGaussianOPDFString
            = "MultiGaussianOPDF [ [ 5. 5. ] [ [ 1.2 .3 ] [ .3 4. ] ] ]";

    /**
     *
     */
    public void testBinaryHmm() {
        PipedInputStream pis = new PipedInputStream();
        RegularHmmBase<?> hmm = null;

        try {
            hmm = new RegularHmmBase<>(4,
                    new OpdfIntegerFactory(3));
            PipedOutputStream pos = new PipedOutputStream(pis);

            HmmBinaryWriter.write(pos, hmm);
            hmm = HmmBinaryReader.read(pis);
        } catch (IOException e) {
            fail(e.toString());
        }

        assertEquals(4, hmm.nbStates());
        assertEquals(3, ((OpdfInteger) hmm.getOpdf(0)).nbEntries());
    }

    /**
     *
     * @throws IOException
     */
    public void testOPDF()
            throws IOException {
        opdfCheck(integerOPDFString, new OpdfIntegerReader(),
                new OpdfIntegerWriter());
        opdfCheck(gaussianOPDFString, new OpdfGaussianReader(),
                new OpdfGaussianWriter());
        opdfCheck(gaussianMixtureOPDFString, new OpdfGaussianMixtureReader(),
                new OpdfGaussianMixtureWriter());
        opdfCheck(multiGaussianOPDFString, new OpdfMultiGaussianReader(),
                new OpdfMultiGaussianWriter());
    }

    private <O extends Observation, D extends Opdf<O>> void
            opdfCheck(String opdfString, OpdfReader<D> reader,
                    OpdfWriter<D> writer)
            throws IOException {
        try {
            PipedWriter pw = new PipedWriter();
            PipedReader pr = new PipedReader(pw);

            StreamTokenizer st
                    = new StreamTokenizer(new StringReader(opdfString));
            D opdf = reader.read(st);
            writer.write(pw, opdf);
            reader.read(new StreamTokenizer(pr));
        } catch (FileFormatException e) {
            fail(e.toString());
        }
    }

    /**
     *
     * @throws IOException
     */
    public void testHmm()
            throws IOException {
        RegularHmmBase<ObservationInteger> hmm = null;

        /* Test HmmReader */
        try {
            Reader reader = new StringReader(hmmString);
            hmm = HmmReader.read(reader, new OpdfIntegerReader(4));
        } catch (FileFormatException e) {
            fail(e.toString());
        }

        assertEquals(2, hmm.nbStates());
        assertEquals(4, ((OpdfInteger) hmm.getOpdf(0)).nbEntries());

        /* Test HmmWriter */
        PipedWriter pw = new PipedWriter();
        PipedReader pr = new PipedReader(pw);

        try {
            HmmWriter.write(pw, new OpdfIntegerWriter(), hmm);
            hmm = HmmReader.read(pr, new OpdfIntegerReader(4));
        } catch (FileFormatException e) {
            fail(e.toString());
        }

        assertEquals(2, hmm.nbStates());
        assertEquals(4, ((OpdfInteger) hmm.getOpdf(0)).nbEntries());
    }

    /**
     *
     * @throws IOException
     * @throws FileFormatException
     */
    public void testInteger()
            throws IOException, FileFormatException {
        Reader reader = new StringReader(integerSequences);
        List<? extends List<ObservationInteger>> sequences
                = ObservationSequencesReader.
                readSequences(new ObservationIntegerReader(), reader);
        reader.close();

        assertEquals("Wrong number of sequences read", 3, sequences.size());
        assertEquals("Wrong first observation",
                1, sequences.get(0).get(0).value);
        assertEquals("Wrong last observation",
                5, sequences.get(2).get(5).value);
    }

    /**
     *
     * @throws IOException
     * @throws FileFormatException
     */
    public void testVector()
            throws IOException, FileFormatException {
        Reader reader = new StringReader(vectorSequences);
        List<? extends List<ObservationVector>> sequences
                = ObservationSequencesReader.
                readSequences(new ObservationVectorReader(), reader);
        reader.close();

        assertEquals("Wrong number of sequences read", 2, sequences.size());
        assertTrue("Wrong first observation", GaussianTest.
                equalsArrays(sequences.get(0).get(0).values(),
                        new double[]{1.1, 2.2}, 0.));
        assertTrue("Wrong last observation", GaussianTest.
                equalsArrays(sequences.get(1).get(4).values(),
                        new double[]{7., 8.}, 0.));
    }
}
