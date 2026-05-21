(ns com.badlogic.gdx.graphics.g2d.bitmap-font.data
  (:require [clojure.graphics.g2d.bitmap-font.data :as font.data])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(extend-type BitmapFont$BitmapFontData
  font.data/Data
  (scale-x [data]
    (.scaleX data))

  (set-scale! [data scale]
    (.setScale data scale))

  (set-markup-enabled! [data enabled?]
    (set! (.markupEnabled data) enabled?)))
