(ns ctx.cursors
  (:require [clojure.graphics :as graphics]
            [clojure.edn :as edn]
            [clojure.disposable :as disposable]
            [clojure.files :as files]
            [clojure.pixmap :as pixmap]
            [clojure.java.io :as io]))

(defn step
  [{:keys [ctx/files
           ctx/graphics]}]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap (pixmap/new (files/internal files path))
                         cursor (graphics/new-cursor graphics pixmap hotspot-x hotspot-y)]
                     (disposable/dispose! pixmap)
                     cursor)))))
