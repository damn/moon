(ns clojure.gdx.bitmap-font.get-data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f [font]
  (BitmapFont/.getData font))
