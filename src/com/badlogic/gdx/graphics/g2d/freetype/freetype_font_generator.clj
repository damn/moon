(ns com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn create [file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn generate-font [^FreeTypeFontGenerator generator freetype-font-parameter]
  (.generateFont generator freetype-font-parameter))

(defn dispose! [^FreeTypeFontGenerator generator]
  (.dispose generator))
