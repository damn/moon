(ns com.badlogic.gdx.graphics.g2d.bitmap-font.set-scale
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f! [^BitmapFont font scale]
  (.setScale (.getData font) scale))
