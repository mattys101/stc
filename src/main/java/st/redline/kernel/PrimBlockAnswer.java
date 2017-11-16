/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */
package st.redline.kernel;

public class PrimBlockAnswer extends RuntimeException {

    private final PrimObject answer;

    public PrimBlockAnswer(PrimObject answer) {
        this.answer = answer;
    }

    public PrimObject answer() {
        return answer;
    }
}
