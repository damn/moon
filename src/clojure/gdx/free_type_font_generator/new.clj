(ns clojure.gdx.free-type-font-generator.new
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn f [^FileHandle file-handle]
  (FreeTypeFontGenerator. file-handle))
