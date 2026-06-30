(ns ctx.dispose
  (:require [clojure.gdx :as gdx]))

(defn do!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! gdx/dispose! (vals audio))
  (gdx/dispose! batch)
  (run! gdx/dispose! (vals cursors))
  (gdx/dispose! default-font)
  (gdx/dispose! shape-drawer-texture)
  (gdx/dispose! skin)
  (run! gdx/dispose! (vals textures))
  (gdx/dispose! tiled-map))
