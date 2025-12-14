(ns moon.application.dispose
  (:require [moon.audio :as audio]
            [moon.graphics :as graphics]
            [moon.ui :as ui]
            [moon.world :as world]))

(defn do!
  [{:keys [ctx/audio
           ctx/graphics
           ctx/stage
           ctx/world]
    :as ctx}]
  (audio/dispose! audio)
  (graphics/dispose! graphics)
  (ui/dispose! stage)
  (world/dispose! world)
  ctx)
