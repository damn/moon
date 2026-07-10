(ns com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn new [^FileHandle file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn generateFont [generator ^FreeTypeFontGenerator$FreeTypeFontParameter parameter]
  (.generateFont ^FreeTypeFontGenerator generator parameter))
