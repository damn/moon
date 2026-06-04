(ns game.ctx.create-cursor
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.files :as files]
            [clojure.gdx.graphics :as graphics]
            [clojure.gdx.graphics.pixmap :as pixmap]))

(defn create-cursor
  [{:keys [ctx/app]} path [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (files/internal (app/files app) path))
        cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))
