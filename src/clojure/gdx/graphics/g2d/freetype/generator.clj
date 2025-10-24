(ns clojure.gdx.graphics.g2d.freetype.generator
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn create [file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn font [^FreeTypeFontGenerator generator parameters]
  (.generateFont generator parameters))

(defn dispose! [^FreeTypeFontGenerator generator]
  (.dispose generator))
