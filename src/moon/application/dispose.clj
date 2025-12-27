(ns moon.application.dispose
  (:require [gdl.utils.disposable :as disposable]
            [moon.audio :as audio]
            [moon.graphics :as graphics]
            [moon.ui :as ui]
            [moon.world :as world]))

(defn do!
  [{:keys [ctx/audio
           ctx/graphics
           ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (audio/dispose! audio)
  (graphics/dispose! graphics)
  (ui/dispose! stage)
  (disposable/dispose! skin)
  (world/dispose! world)
  ctx)
