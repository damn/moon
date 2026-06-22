(ns gdl.bitmap-font.get-data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn get-data [^BitmapFont font]
  (.getData font))
