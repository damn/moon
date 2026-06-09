(ns game.ctx.create-cursor
  (:require [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.new-cursor :as new-cursor]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn create-cursor
  [{:keys [ctx/files
           ctx/graphics]}
   path
   [hotspot-x hotspot-y]]
  (let [pixmap (pixmap/create (files/internal files path))
        cursor (new-cursor/f graphics pixmap hotspot-x hotspot-y)]
    (pixmap/dispose! pixmap)
    cursor))
