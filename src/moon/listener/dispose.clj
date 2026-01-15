(ns moon.listener.dispose
  (:require [moon.audio :as audio]
            [moon.graphics :as graphics]
            [moon.ui :as ui]
            [moon.world :as world])
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/audio
           ctx/graphics
           ctx/skin
           ctx/stage
           ctx/textures
           ctx/world]
    :as ctx}]
  (audio/dispose! audio)
  (graphics/dispose! graphics)
  (ui/dispose! stage)
  (Disposable/.dispose skin)
  (run! Disposable/.dispose (vals textures))
  (world/dispose! world)
  ctx)
