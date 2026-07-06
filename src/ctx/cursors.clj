(ns ctx.cursors
  (:require
            [com.badlogic.gdx.graphics :as graphics] [clojure.edn :as edn]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
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
