(ns clojure.gdx.bitmap-font$bitmap-font-data.set-scale
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn f [^BitmapFont$BitmapFontData data scale]
  (BitmapFont$BitmapFontData/.setScale data scale))
