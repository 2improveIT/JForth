package jforth.forthwords;

import jforth.PrimitiveWord;
import jforth.WordsList;
import jforth.seq.DoubleSequence;
import jforth.seq.Haar;
import org.apache.commons.math3.analysis.function.HarmonicOscillator;
import org.apache.commons.math3.analysis.function.Signum;
import tools.ClipBoard;
import tools.Utilities;

import java.math.BigInteger;

import static jforth.PositionalNumberSystem.getPnsInst;
import static tools.Utilities.readDoubleSequence;
import static tools.Utilities.readString;

final class Filler3 {
    static void fill(WordsList _fw, PredefinedWords predefinedWords) {

        _fw.add(new PrimitiveWord
                (
                        "pns-init", "initialize positional number converter",
                        (dStack, vStack) ->
                        {
                            try {
                                String s = readString(dStack);
                                getPnsInst().newInstance(s);
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));


        _fw.add(new PrimitiveWord
                (
                        "pns-do", "convert decimal to arbitrary positional number",
                        (dStack, vStack) ->
                        {
                            try {
                                BigInteger bi = Utilities.readBig(dStack);
                                String s = getPnsInst().toString(bi);
                                dStack.push(s);
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

        _fw.add(new PrimitiveWord
                (
                        "pns-undo", "convert psn string back to number",
                        (dStack, vStack) ->
                        {
                            try {
                                String s = readString(dStack);
                                BigInteger bi = getPnsInst().toNumber(s);
                                dStack.push(bi);
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

        _fw.add(new PrimitiveWord
                (
                        "harmonic", "create harmonic oscillating seq",
                        (dStack, vStack) ->
                        {
                            try {
                                DoubleSequence parms = Utilities.readDoubleSequence(dStack);
                                DoubleSequence in = Utilities.readDoubleSequence(dStack);
                                HarmonicOscillator ha = new HarmonicOscillator(parms.pick(0),
                                        parms.pick(1), parms.pick(2));
                                DoubleSequence out = new DoubleSequence();
                                for (double d : in.asPrimitiveArray()) {
                                    out.get_list().add(ha.value(d));
                                }
                                dStack.push(out);
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

        _fw.add(new PrimitiveWord
                (
                        "sign", "signum of sequence or singe value",
                        (dStack, vStack) ->
                        {
                            try {
                                Object o = dStack.pop();
                                if (o instanceof DoubleSequence) {
                                    DoubleSequence in = (DoubleSequence) o;
                                    Signum si = new Signum();
                                    DoubleSequence out = new DoubleSequence();
                                    for (double d : in.asPrimitiveArray()) {
                                        out.get_list().add(si.value(d));
                                    }
                                    dStack.push(out);
                                    return 1;
                                } else if (o instanceof Double) {
                                    dStack.push(Math.signum((Double) o));
                                    return 1;
                                } else if (o instanceof Long) {
                                    dStack.push((long) Math.signum((Long) o));
                                    return 1;
                                }

                                return 0;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

//        _fw.add(new PrimitiveWord
//                (
//                        "sign", "signum of sequence or singe value",
//                        (dStack, vStack) ->
//                        {
//                            try {
//                                Object o = dStack.pop();
//                                if (o instanceof DoubleSequence) {
//                                    DoubleSequence in = (DoubleSequence)o;
//                                    Signum si = new Signum();
//                                    DoubleSequence out = new DoubleSequence();
//                                    for (double d : in.asPrimitiveArray()) {
//                                        out.get_list().add(si.value(d));
//                                    }
//                                    dStack.push(out);
//                                    return 1;
//                                }
//                            } catch (Exception e) {
//                                return 0;
//                            }
//                        }
//                ));

        _fw.add(new PrimitiveWord
                (
                        "gclip", "put clipboard data on stack",
                        (dStack, vStack) ->
                        {
                            try {
                                dStack.push(ClipBoard.get());
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

        _fw.add(new PrimitiveWord
                (
                        "sclip", "send TOS to clipboard",
                        (dStack, vStack) ->
                        {
                            try {
                                ClipBoard.put(readString(dStack));
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

        _fw.add(new PrimitiveWord
                (
                        "haar", "haar wavelet transform",
                        (dStack, vStack) ->
                        {
                            try {
                                DoubleSequence ds = readDoubleSequence(dStack);
                                double[] prim = ds.asPrimitiveArray();
                                Haar.Forward(prim);
                                dStack.push (new DoubleSequence(prim));
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

        _fw.add(new PrimitiveWord
                (
                        "unhaar", "backward haar wavelet transform",
                        (dStack, vStack) ->
                        {
                            try {
                                DoubleSequence ds = readDoubleSequence(dStack);
                                double[] prim = ds.asPrimitiveArray();
                                Haar.Backward(prim);
                                dStack.push (new DoubleSequence(prim));
                                return 1;
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                ));

    }
}
