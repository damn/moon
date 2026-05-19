(ns game.impl.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdl.app :as app]
            [gdl.files :as files]
            [gdl.graphics :as graphics]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn create
  [{:keys [ctx/app]}]
  (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path [hotspot-x hotspot-y]]]
                   (let [pixmap (pixmap/create (files/internal (app/files app) (format path-format path)))
                         cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
                     (pixmap/dispose! pixmap)
                     cursor)))))
