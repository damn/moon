(ns moon.render.tick-entities
  (:require [moon.ctx :as ctx]
            [moon.ui :as ui]
            [moon.throwable :as throwable]
            [moon.world :as world]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do (try
         (ctx/handle! ctx (world/tick-entities! world))
         (catch Throwable t
           (throwable/pretty-pst t)
           (ui/show-error-window! stage skin t)))
        ctx)))
