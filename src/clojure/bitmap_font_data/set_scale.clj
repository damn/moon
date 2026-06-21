(ns clojure.bitmap-font-data.set-scale
  (:require [clojure.bitmap-font.get-data :refer [get-data]])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f! [font scale]
  (.setScale (get-data font) scale))
