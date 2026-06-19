(ns com.badlogic.gdx.graphics.g2d.freetype.font-generator.dispose
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn dispose! [^FreeTypeFontGenerator generator]
  (.dispose generator))
