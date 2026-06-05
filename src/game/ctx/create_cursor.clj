(ns game.ctx.create-cursor
  (:require [clojure.application :as app]
            [clojure.files :as files]
            [clojure.graphics :as graphics]
            [clojure.pixmap :as pixmap]))

(defn create-cursor
  [{:keys [ctx/app]} path [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (files/internal (app/files app) path))
        cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))
