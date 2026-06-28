(ns ctx.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [pixmap.dispose :as dispose]
            [graphics.new-cursor :as new-cursor])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.graphics Pixmap)))

(defn step
  [{:keys [ctx/files
           ctx/graphics]}]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap (Pixmap. (Files/.internal files path))
                         cursor (new-cursor/f graphics pixmap hotspot-x hotspot-y)]
                     (dispose/f! pixmap)
                     cursor)))))
