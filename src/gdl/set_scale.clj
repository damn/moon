(ns gdl.set-scale
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn f [^BitmapFont$BitmapFontData data scale]
  (.setScale data scale))
