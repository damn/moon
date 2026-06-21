(ns clojure.bitmap-font.get-scale-x
  (:require [clojure.bitmap-font.get-data :refer [get-data]])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f [font]
  (.scaleX (get-data font)))
