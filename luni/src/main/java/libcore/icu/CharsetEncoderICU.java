/**
*******************************************************************************
* Copyright (C) 1996-2006, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                  *
*******************************************************************************
*
*******************************************************************************
*/
/**
 * A JNI interface for ICU converters.
 *
 *
 * @author Ram Viswanadha, IBM
 */
package libcore.icu;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;
import java.util.Map;

public final class CharsetEncoderICU extends CharsetEncoder {
    private static final Map<String, byte[]> DEFAULT_REPLACEMENTS = new HashMap<String, byte[]>();
    static {
        // ICU has different default replacements to the RI in some cases. There are many
        // additional cases, but this covers all the charsets that Java guarantees will be
        // available, which is where compatibility seems most important. (The RI even uses
        // the byte corresponding to '?' in ASCII as the replacement byte for charsets where that
        // byte corresponds to an entirely different character.)
        // It's odd that UTF-8 doesn't use U+FFFD, given that (unlike ISO-8859-1 and US-ASCII) it
        // can represent it, but this is what the RI does...
        byte[] questionMark = new byte[] { (byte) '?' };
        DEFAULT_REPLACEMENTS.put("UTF-8",      questionMark);
        DEFAULT_REPLACEMENTS.put("ISO-8859-1", questionMark);
        DEFAULT_REPLACEMENTS.put("US-ASCII",   questionMark);
    }

    private static final int INPUT_OFFSET = 0;
    private static final int OUTPUT_OFFSET = 1;
    private static final int INVALID_CHARS = 2;
    private static final int INPUT_HELD = 3;
    /*
     * data[INPUT_OFFSET]   = on input contains the start of input and on output the number of input chars consumed
     * data[OUTPUT_OFFSET]  = on input contains the start of output and on output the number of output bytes written
     * data[INVALID_CHARS]  = number of invalid chars
     * data[INPUT_HELD]     = number of input chars held in the converter's state
     */
    private int[] data = new int[4];
    /* handle to the ICU converter that is opened */
    private long converterHandle=0;

    private char[] input = null;
    private byte[] output = null;

    private char[] allocatedInput = null;
    private byte[] allocatedOutput = null;

    // These instance variables are always assigned in the methods before being used. This class
    // is inherently thread-unsafe so we don't have to worry about synchronization.
    private int inEnd;
    private int outEnd;
    private int ec;
    private int savedInputHeldLen;

    public static CharsetEncoderICU newInstance(Charset cs, String icuCanonicalName) {
        // This complexity is necessary to ensure that even if the constructor, superclass
        // constructor, or call to updateCallback throw, we still free the native peer.
        long address = 0;
        try {
            address = NativeConverter.openConverter(icuCanonicalName);
            float averageBytesPerChar = NativeConverter.getAveBytesPerChar(address);
            float maxBytesPerChar = NativeConverter.getMaxBytesPerChar(address);
            byte[] replacement = makeReplacement(icuCanonicalName, address);
            CharsetEncoderICU result = new CharsetEncoderICU(cs, averageBytesPerChar, maxBytesPerChar, replacement, address);
            address = 0; // CharsetEncoderICU has taken ownership; its finalizer will do the free.
            result.updateCallback();
            return result;
        } finally {
            if (address != 0) {
                NativeConverter.closeConverter(address);
            }
        }
    }

    private static byte[] makeReplacement(String icuCanonicalName, long address) {
        // We have our own map of RI-compatible default replacements (where ICU disagrees)...
        byte[] replacement = DEFAULT_REPLACEMENTS.get(icuCanonicalName);
        if (replacement != null) {
            return replacement.clone();
        }
        // ...but fall back to asking ICU.
        return NativeConverter.getSubstitutionBytes(address);
    }

    private CharsetEncoderICU(Charset cs, float averageBytesPerChar, float maxBytesPerChar, byte[] replacement, long address) {
        super(cs, averageBytesPerChar, maxBytesPerChar, replacement);
        this.converterHandle = address;
    }

    /**
     * Sets this encoders replacement string. Substitutes the string in output if an
     * unmappable or illegal sequence is encountered
     * @param newReplacement to replace the error chars with
     * @stable ICU 2.4
     */
    protected void implReplaceWith(byte[] newReplacement) {
        if (converterHandle != 0) {
            if (newReplacement.length > NativeConverter.getMaxBytesPerChar(converterHandle)) {
                throw new IllegalArgumentException("Number of replacement Bytes are greater than max bytes per char");
            }
            updateCallback();
        }
    }

    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param newAction action to be taken
     * @exception IllegalArgumentException
     * @stable ICU 2.4
     */
    protected void implOnMalformedInput(CodingErrorAction newAction) {
        updateCallback();
    }

    /**
     * Sets the action to be taken if an illegal sequence is encountered
     * @param newAction action to be taken
     * @exception IllegalArgumentException
     * @stable ICU 2.4
     */
    protected void implOnUnmappableCharacter(CodingErrorAction newAction) {
        updateCallback();
    }

    private void updateCallback() {
        ec = NativeConverter.setCallbackEncode(converterHandle, this);
        if (ErrorCode.isFailure(ec)) {
            throw ErrorCode.throwException(ec);
        }
    }

    /**
     * Flushes any characters saved in the converter's internal buffer and
     * resets the converter.
     * @param out action to be taken
     * @return result of flushing action and completes the decoding all input.
     *       Returns CoderResult.UNDERFLOW if the action succeeds.
     * @stable ICU 2.4
     */
    protected CoderResult implFlush(ByteBuffer out) {
        try {
            data[OUTPUT_OFFSET] = getArray(out);
            ec = NativeConverter.flushCharToByte(converterHandle,/* Handle to ICU Converter */
                                                 output, /* output array of chars */
                                                 outEnd, /* output index+1 to be written */
                                                 data /* contains data, inOff,outOff */
                                                );

            /* If we don't have room for the output, throw an exception*/
            if (ErrorCode.isFailure(ec)) {
                if (ec == ErrorCode.U_BUFFER_OVERFLOW_ERROR) {
                    return CoderResult.OVERFLOW;
                } else if (ec == ErrorCode.U_TRUNCATED_CHAR_FOUND) {//CSDL: add this truncated character error handling
                    if (data[INPUT_OFFSET] > 0) {
                        return CoderResult.malformedForLength(data[INPUT_OFFSET]);
                    }
                } else {
                    throw ErrorCode.throwException(ec);
                }
            }
            return CoderResult.UNDERFLOW;
        } finally {
            setPosition(out);
            implReset();
        }
    }

    /**
     * Resets the from Unicode mode of converter
     * @stable ICU 2.4
     */
    protected void implReset() {
        NativeConverter.resetCharToByte(converterHandle);
        data[INPUT_OFFSET] = 0;
        data[OUTPUT_OFFSET] = 0;
        data[INVALID_CHARS] = 0;
        data[INPUT_HELD] = 0;
        savedInputHeldLen = 0;
    }

    /**
     * Encodes one or more chars. The default behavior of the
     * converter is stop and report if an error in input stream is encountered.
     * To set different behavior use @see CharsetEncoder.onMalformedInput()
     * @param in buffer to decode
     * @param out buffer to populate with decoded result
     * @return result of decoding action. Returns CoderResult.UNDERFLOW if the decoding
     *       action succeeds or more input is needed for completing the decoding action.
     * @stable ICU 2.4
     */
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        if (!in.hasRemaining()) {
            return CoderResult.UNDERFLOW;
        }

        data[INPUT_OFFSET] = getArray(in);
        data[OUTPUT_OFFSET]= getArray(out);
        data[INPUT_HELD] = 0;
        data[INVALID_CHARS] = 0; // Make sure we don't see earlier errors.

        try {
            /* do the conversion */
            ec = NativeConverter.encode(converterHandle,/* Handle to ICU Converter */
                                        input, /* input array of bytes */
                                        inEnd, /* last index+1 to be converted */
                                        output, /* output array of chars */
                                        outEnd, /* output index+1 to be written */
                                        data, /* contains data, inOff,outOff */
                                        false /* don't flush the data */
                                        );
            if (ErrorCode.isFailure(ec)) {
                /* If we don't have room for the output return error */
                if (ec == ErrorCode.U_BUFFER_OVERFLOW_ERROR) {
                    return CoderResult.OVERFLOW;
                } else if (ec == ErrorCode.U_INVALID_CHAR_FOUND) {
                    return CoderResult.unmappableForLength(data[INVALID_CHARS]);
                } else if (ec == ErrorCode.U_ILLEGAL_CHAR_FOUND) {
                    // in.position(in.position() - 1);
                    return CoderResult.malformedForLength(data[INVALID_CHARS]);
                }
            }
            return CoderResult.UNDERFLOW;
        } finally {
            /* save state */
            setPosition(in);
            setPosition(out);
        }
    }

    public boolean canEncode(char c) {
        return canEncode((int) c);
    }

    public boolean canEncode(int codePoint) {
        return NativeConverter.canEncode(converterHandle, codePoint);
    }

    /**
     * Releases the system resources by cleanly closing ICU converter opened
     * @exception Throwable exception thrown by super class' finalize method
     * @stable ICU 2.4
     */
    @Override protected void finalize() throws Throwable {
        try {
            NativeConverter.closeConverter(converterHandle);
            converterHandle=0;
        } finally {
            super.finalize();
        }
    }

    //------------------------------------------
    // private utility methods
    //------------------------------------------
    private int getArray(ByteBuffer out) {
        if (out.hasArray()) {
            output = out.array();
            outEnd = out.arrayOffset() + out.limit();
            return out.arrayOffset() + out.position();
        } else {
            outEnd = out.remaining();
            if (allocatedOutput == null || (outEnd > allocatedOutput.length)) {
                allocatedOutput = new byte[outEnd];
            }
            output = allocatedOutput;
            //since the new
            // buffer start position
            // is 0
            return 0;
        }
    }

    private int getArray(CharBuffer in) {
        if (in.hasArray()) {
            input = in.array();
            inEnd = in.arrayOffset() + in.limit();
            return in.arrayOffset() + in.position() + savedInputHeldLen;/*exclude the number fo bytes held in previous conversion*/
        } else {
            inEnd = in.remaining();
            if (allocatedInput == null || (inEnd > allocatedInput.length)) {
                allocatedInput = new char[inEnd];
            }
            input = allocatedInput;
            // save the current position
            int pos = in.position();
            in.get(input,0,inEnd);
            // reset the position
            in.position(pos);
            // the start position
            // of the new buffer
            // is whatever is savedInputLen
            return savedInputHeldLen;
        }

    }
    private void setPosition(ByteBuffer out) {

        if (out.hasArray()) {
            // in getArray method we accessed the
            // array backing the buffer directly and wrote to
            // it, so just just set the position and return.
            // This is done to avoid the creation of temp array.
            out.position(out.position() + data[OUTPUT_OFFSET] - out.arrayOffset());
        } else {
            out.put(output, 0, data[OUTPUT_OFFSET]);
        }
        // release reference to output array, which may not be ours
        output = null;
    }
    private void setPosition(CharBuffer in){

        // Slightly rewired original code to make it cleaner. Also
        // added a fix for the problem where input characters got
        // lost when invalid characters were encountered. Not sure
        // what happens when data[INVALID_CHARS] is > 1, though,
        // since we never saw that happening.
        int len = in.position() + data[INPUT_OFFSET] + savedInputHeldLen;
        len -= data[INVALID_CHARS]; // Otherwise position becomes wrong.
        in.position(len);
        savedInputHeldLen = data[INPUT_HELD];
        // was there input held in the previous invocation of encodeLoop
        // that resulted in output in this invocation?
        if(!(data[OUTPUT_OFFSET]>0 && savedInputHeldLen>0)){
            in.position(in.position() - savedInputHeldLen);
        }

        // release reference to input array, which may not be ours
        input = null;
    }
}
