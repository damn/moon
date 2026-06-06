(ns clojure.malli-form)

(defmulti malli-form (fn [[k] _schemas]
                       k))

; Ok we just go from the bottom
; see if it is simple 'as in 1 form/function only - no state'!
; green/red
; _simple_
; => then we know what to focus on next...

; => Then we saw test comments in one namespace
; malli.utils
; => We write tests for our functions maybe!

; FORGET ABOUT NAMES FOR NOW
; WE LOOK AT FULL HIERARCHY
; LATER BIGGEST FILES/COMPLICATES
; FIRST 1-1 function
; THEN TESTS MAYBE ( IN SAME FOLDER? - is good for explanation! )

; => THEN GO FOR COMMENTS ETC.....

; => FIRST EACH IMPORT FIELD/GETTER/METHOD/CONSTCUCTOR EACH ONE FILE

;;;;;

; The ULTIMATE CODE ORGANIZATION
; AND APPPLY IT TO LIBGDX!
; ONE ONE FUNCTION NO STATE !
; APPLY TO YOUR GAME FIRST !

; ONLY VERBS
; SIMILAR WORDS & BEHAVIOUR == ABSTRACTION
; => ALL IMPORTS AS 'clojure.gdx' language inherited?!

