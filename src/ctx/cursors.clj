(ns ctx.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.graphics Pixmap)
           (com.badlogic.gdx Graphics)))

(defn step
  [{:keys [ctx/files
           ctx/graphics]}]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap (Pixmap. (Files/.internal files path))
                         cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
                     (Pixmap/.dispose pixmap)
                     cursor)))))
