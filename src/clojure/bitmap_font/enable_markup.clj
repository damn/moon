(ns clojure.bitmap-font.enable-markup
  (:require [clojure.bitmap-font.get-data :refer [get-data]]
            [clojure.bitmap-font.data.enable-markup :refer [enable-markup!]]))

(defn f! [font]
  (enable-markup! (get-data font)))
