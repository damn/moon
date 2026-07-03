(ns ctx.dispose
  (:require [clojure.gdx.disposable.dispose :as dispose]))

(defn do!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! dispose/f (vals audio))
  (dispose/f batch)
  (run! dispose/f (vals cursors))
  (dispose/f default-font)
  (dispose/f shape-drawer-texture)
  (dispose/f skin)
  (run! dispose/f (vals textures))
  (dispose/f tiled-map))
