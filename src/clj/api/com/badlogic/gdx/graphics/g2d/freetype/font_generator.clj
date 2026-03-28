(ns clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn create [file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn generate-font [^FreeTypeFontGenerator generator parameters]
  (.generateFont generator parameters))
