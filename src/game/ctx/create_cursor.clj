(ns game.ctx.create-cursor
  (:require [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn create-cursor
  [{:keys [ctx/files
           ctx/graphics]}
   path
   [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (files/internal files path))
        cursor (graphics/new-cursor graphics pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))
