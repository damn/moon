(ns moon.listener.dispose
  (:require [moon.world :as world])
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/audio
           ctx/graphics
           ctx/skin
           ctx/textures
           ctx/world]}]
  (run! Disposable/.dispose (vals audio))
  (let [{:keys [graphics/batch
                graphics/cursors
                graphics/default-font
                graphics/shape-drawer-texture]} graphics]
    (Disposable/.dispose batch)
    (run! Disposable/.dispose (vals cursors))
    (Disposable/.dispose default-font)
    (Disposable/.dispose shape-drawer-texture))
  (Disposable/.dispose skin)
  (run! Disposable/.dispose (vals textures))
  (world/dispose! world)
  nil)
