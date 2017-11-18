"Smalltalk import: 'st.redline.kernel.JavaClass' ."

Java. "ensure Java class is bootstrapped"
JavaClass. "ensure JavaClass class is bootstrapped"

"Using a block, otherwise we can't have variables."
[
    | system out stringClass aString |
    system := JavaClass for: 'java.lang.System'.
    out := system field: 'out'.
    out call: 'println' with: 'This is writing out a string to the console'.
    out call: 'println' with: '';
        call: 'println' with: 'another line after a blank' . "this appears to be broken currently"
    
    stringClass := JavaClass for: 'java.lang.String'.
    
    aString := stringClass new.
    out call: 'println' with: aString.
    
    aString := stringClass newWith: 'a wrapped instance of String'.
    out call: 'println' with: aString.
    
    ^aString
] value