(ns clojure.graphics.g2d.bitmap-font.set-scale
  (:require [clojure.graphics.g2d.bitmap-font.get-data :refer [get-data]])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f! [font scale]
  (.setScale (get-data font) scale))
