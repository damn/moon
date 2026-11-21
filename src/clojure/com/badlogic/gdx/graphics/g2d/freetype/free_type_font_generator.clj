(ns clojure.com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create [^FileHandle file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn generate-font [^FreeTypeFontGenerator generator
                     ^FreeTypeFontGenerator$FreeTypeFontParameter parameter]
  (.generateFont generator parameter))
