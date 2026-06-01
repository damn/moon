(ns create.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [clojure.gdx.graphics :as graphics]
            [clojure.gdx.graphics.pixmap :as pixmap]))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                            (update-vals data
                                         (fn [[path [hotspot-x hotspot-y]]]
                                           (let [pixmap (pixmap/create (files/internal (app/files app) (format path-format path)))
                                                 cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
                                             (pixmap/dispose! pixmap)
                                             cursor))))))
