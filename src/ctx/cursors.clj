(ns ctx.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.gdx :as gdx]))

(defn step
  [{:keys [ctx/files
           ctx/graphics]}]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap (gdx/pixmap (gdx/internal files path))
                         cursor (gdx/new-cursor graphics pixmap hotspot-x hotspot-y)]
                     (gdx/pixmap-dispose! pixmap)
                     cursor)))))
