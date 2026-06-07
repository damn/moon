(ns game.ctx.create-cursor
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn create-cursor
  [{:keys [ctx/app
           ctx/files]}
   path
   [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (files/internal files path))
        cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))
