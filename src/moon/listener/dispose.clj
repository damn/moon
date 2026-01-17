(ns moon.listener.dispose
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/world]}]
  (run! Disposable/.dispose (vals audio))
  (Disposable/.dispose batch)
  (run! Disposable/.dispose (vals cursors))
  (Disposable/.dispose default-font)
  (Disposable/.dispose shape-drawer-texture)
  (Disposable/.dispose skin)
  (run! Disposable/.dispose (vals textures))
  (Disposable/.dispose (:world/tiled-map world))
  nil)
