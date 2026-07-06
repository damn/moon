(ns com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn generate-font [generator ^FreeTypeFontGenerator$FreeTypeFontParameter parameter]
  (FreeTypeFontGenerator/.generateFont generator parameter))

(defn new [^FileHandle file-handle]
  (FreeTypeFontGenerator. file-handle))
