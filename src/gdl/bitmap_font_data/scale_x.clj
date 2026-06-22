(ns gdl.bitmap-font-data.scale-x
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn f [^BitmapFont$BitmapFontData data]
  (.scaleX data))
