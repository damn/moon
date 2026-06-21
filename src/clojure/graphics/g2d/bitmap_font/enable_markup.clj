(ns clojure.graphics.g2d.bitmap-font.enable-markup
  (:require [clojure.graphics.g2d.bitmap-font.get-data :refer [get-data]]
            [clojure.graphics.g2d.bitmap-font.data.enable-markup :refer [enable-markup!]]))

(defn f! [font]
  (enable-markup! (get-data font)))
