(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.label
  "A text label, with optional word wrapping.
  
  The preferred size of the label is determined by the actual text bounds, unless set wrap is enabled."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label Skin)))

(defn create [^String text ^Skin skin]
  (Label. text skin))

(defn set-text!
  "Sets the text to the specified integer value. If the text is already equivalent to the specified value, a string is not
  allocated.
  
  Returns true if the text was changed."
  [^Label label ^String text]
  (.setText label text))

(defn label? [x]
  (instance? Label x))
