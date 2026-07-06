(ns ctx.cursors
  (:require [clojure.edn :as edn]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [clojure.gdx.files.internal :as internal]
            [clojure.gdx.graphics.new-cursor :as new-cursor]
            [clojure.gdx.pixmap.new :as pixmap]
            [clojure.java.io :as io]))

(defn step
  [{:keys [ctx/files
           ctx/graphics]}]
  (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path-segment [hotspot-x hotspot-y]]]
                   (let [path (format path-format path-segment)
                         pixmap (pixmap/f (internal/f files path))
                         cursor (new-cursor/f graphics pixmap hotspot-x hotspot-y)]
                     (disposable/dispose! pixmap)
                     cursor)))))
