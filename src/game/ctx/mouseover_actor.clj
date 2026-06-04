(ns game.ctx.mouseover-actor
  (:require [game.ctx.mouse-position :refer [mouse-position]]
            [gdx.stage :as stage]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (stage/mouseover-actor stage (mouse-position ctx)))
