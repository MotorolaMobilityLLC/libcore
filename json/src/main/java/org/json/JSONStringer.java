package org.json;

/*
Copyright (c) 2005 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**
 * JSONStringer provides a quick and convenient way of producing JSON text.
 * The texts produced strictly conform to JSON syntax rules. No whitespace is 
 * added, so the results are ready for transmission or storage. Each instance of 
 * JSONStringer can produce one JSON text.
 * <p>
 * A JSONStringer instance provides a <code>value</code> method for appending 
 * values to the 
 * text, and a <code>key</code> 
 * method for adding keys before values in objects. There are <code>array</code>
 * and <code>endArray</code> methods that make and bound array values, and 
 * <code>object</code> and <code>endObject</code> methods which make and bound 
 * object values. All of these methods return the JSONStringer instance, 
 * permitting cascade style. For example, <pre>
 * myString = new JSONStringer()
 *     .object()
 *         .key("JSON").value("Hello, World!")
 *     .endObject()
 *     .toString();</pre> which produces the string <pre>
 * {"JSON":"Hello, World!"}</pre>
 * <p>
 * The first method called must be <code>array</code> or <code>object</code>. 
 * There are no methods for adding commas or colons. JSONStringer adds them for 
 * you. Objects and arrays can be nested up to 20 levels deep.
 * <p>
 * This can sometimes be easier than using a JSONObject to build a string.
 * @author JSON.org
 * @version 2
 */
public class JSONStringer {
    private static final int maxdepth = 20;
    
    /**
     * The comma flag determines if a comma should be output before the next
     * value.
     */
    private boolean comma;
    
    /**
     * The current mode. Values: 
     * 'a' (array), 
     * 'd' (done), 
     * 'i' (initial), 
     * 'k' (key), 
     * 'o' (object).
     */
    private char mode;
    
    /**
     * The string buffer that holds the JSON text that is built.
     */
    private StringBuilder sb;
    
    /**
     * The object/array stack.
     */
    private char stack[];
    
    /**
     * The stack top index. A value of 0 indicates that the stack is empty.
     */
    private int top;
    
    /**
     * Make a fresh JSONStringer. It can be used to build one JSON text.
     */
    public JSONStringer() {
        this.sb = new StringBuilder();
        this.stack = new char[maxdepth];
        this.top = 0;
        this.mode = 'i';
        this.comma = false;
    }
    
    /**
     * Append a value.
     * @param s A string value.
     * @return this
     * @throws JSONException If the value is out of sequence.
     */
    private JSONStringer append(String s) 
            throws JSONException {
        if (s == null) {
            throw new JSONException("Null pointer");
        }
        if (this.mode == 'o' || this.mode == 'a') {
            if (this.comma && this.mode == 'a') {
                this.sb.append(',');
            }
            this.sb.append(s);
            if (this.mode == 'o') {
                this.mode = 'k';
            }
            this.comma = true;
            return this;
        } 
        throw new JSONException("Value out of sequence.");    
    }
    
    /**
     * Begin appending a new array. All values until the balancing 
     * <code>endArray</code> will be appended to this array. The 
     * <code>endArray</code> method must be called to mark the array's end.
     * @return this
     * @throws JSONException If the nesting is too deep, or if the object is 
     * started in the wrong place (for example as a key or after the end of the 
     * outermost array or object).
     */
    public JSONStringer array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            push('a');
            this.append("[");
            this.comma = false;
            return this;
        } 
        throw new JSONException("Misplaced array.");
    }
    
    /**
     * End something. 
     * @param m Mode
     * @param c Closing character
     * @return this
     * @throws JSONException If unbalanced.
     */
    private JSONStringer end(char m, char c) throws JSONException {
        if (this.mode != m) {
            throw new JSONException(m == 'o' ? "Misplaced endObject." :
                "Misplaced endArray.");            
        }
        pop(m);
        this.sb.append(c);
        this.comma = true;
        return this;
    }
    
    /**
     * End an array. This method most be called to balance calls to 
     * <code>array</code>.
     * @return this
     * @throws JSONException If incorrectly nested.
     */
    public JSONStringer endArray() throws JSONException {
        return end('a', ']');
    }
    
    /**
     * End an object. This method most be called to balance calls to 
     * <code>object</code>.
     * @return this
     * @throws JSONException If incorrectly nested.
     */
    public JSONStringer endObject() throws JSONException {
        return end('k', '}');
    }
    
    /**
     * Append a key. The key will be associated with the next value. In an
     * object, every value must be preceded by a key.
     * @param s A key string.
     * @return this
     * @throws JSONException If the key is out of place. For example, keys
     *     do not belong in arrays or if the key is null.
     */
    public JSONStringer key(String s) 
            throws JSONException {
        if (s == null) {
            throw new JSONException("Null key.");
        }
        if (this.mode == 'k') {
            if (this.comma) {
                this.sb.append(',');
            }
            this.sb.append(JSONObject.quote(s));
            this.sb.append(':');
            this.comma = false;
            this.mode = 'o';
            return this;
        } 
        throw new JSONException("Misplaced key.");    
    }

    
    /**
     * Begin appending a new object. All keys and values until the balancing 
     * <code>endObject</code> will be appended to this object. The 
     * <code>endObject</code> method must be called to mark the object's end.
     * @return this
     * @throws JSONException If the nesting is too deep, or if the object is 
     * started in the wrong place (for example as a key or after the end of the 
     * outermost array or object).
     */
    public JSONStringer object() throws JSONException {
        if (this.mode == 'i') {
            this.mode = 'o';
        }
        if (this.mode == 'o' || this.mode == 'a') {
            this.append("{");
            push('k');
            this.comma = false;
            return this;
        } 
        throw new JSONException("Misplaced object.");
        
    }
    
    
    /**
     * Pop an array or object scope.
     * @param c The scope to close.
     * @throws JSONException If nesting is wrong.
     */
    private void pop(char c) throws JSONException {
        if (this.top <= 0 || this.stack[this.top - 1] != c) {
            throw new JSONException("Nesting error.");
        }
        this.top -= 1;
        this.mode = this.top == 0 ? 'd' : this.stack[this.top - 1];
    }
    
    /**
     * Push an array or object scope.
     * @param c The scope to open.
     * @throws JSONException If nesting is too deep.
     */
    private void push(char c) throws JSONException {
        if (this.top >= maxdepth) {
            throw new JSONException("Nesting too deep.");
        }
        this.stack[this.top] = c;
        this.mode = c;
        this.top += 1;
    }
    
    
    /**
     * Append either the value <code>true</code> or the value 
     * <code>false</code>. 
     * @param b A boolean.
     * @return this
     * @throws JSONException
     */
    public JSONStringer value(boolean b) throws JSONException {
        return this.append(b ? "true" : "false");
    }
    
    /**
     * Append a double value.
     * @param d A double.
     * @return this
     * @throws JSONException If the number is not finite.
     */
    public JSONStringer value(double d) throws JSONException {
        return this.value(new Double(d));
    }
    
    /**
     * Append a long value.
     * @param l A long.
     * @return this
     * @throws JSONException
     */
    public JSONStringer value(long l) throws JSONException {
        return this.append(Long.toString(l));
    }
    
    
    /**
     * Append an object value.
     * @param o The object to append. It can be null, or a Boolean, Number,
     *   String, JSONObject, or JSONArray.
     * @return this
     * @throws JSONException If the value is out of sequence.
     */
    public JSONStringer value(Object o) throws JSONException {
        if (JSONObject.NULL.equals(o)) {
            return this.append("null");
        }
        if (o instanceof Number) {
            JSONObject.testValidity(o);
            return this.append(JSONObject.numberToString((Number)o));
        }
        if (o instanceof Boolean || 
                o instanceof JSONArray || o instanceof JSONObject) {
            return this.append(o.toString());
        }
        return this.append(JSONObject.quote(o.toString()));
    }
    
    /**
     * Return the JSON text. This method is used to obtain the product of the
     * JSONStringer instance. It will return <code>null</code> if there was a 
     * problem in the construction of the JSON text (such as the calls to 
     * <code>array</code> were not properly balanced with calls to 
     * <code>endArray</code>).
     * @return The JSON text.
     */
    public String toString() {
        return this.mode == 'd' ? this.sb.toString() : null;
    }
}
