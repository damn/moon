(ns game.dispose
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! dispose! (vals audio))
  (dispose! batch)
  (run! dispose! (vals cursors))
  (dispose! default-font)
  (dispose! shape-drawer-texture)
  (dispose! skin)
  (run! dispose! (vals textures))
  (dispose! tiled-map))
