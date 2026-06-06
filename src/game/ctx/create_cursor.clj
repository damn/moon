(ns game.ctx.create-cursor
  (:require [gdx.application :as app]
            [gdx.files :as files]
            [gdx.graphics :as graphics]
            [gdx.pixmap :as pixmap]))

(defn create-cursor
  [{:keys [ctx/app]} path [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (files/internal (app/files app) path))
        cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))
