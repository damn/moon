(ns game.ctx.mouseover-actor
  (:require [gdl.stage.hit :refer [hit]]
            [gdl.viewport.unproject :as unproject]
            [game.ctx.mouse-position :refer [mouse-position]]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (hit stage
       (-> stage
           :stage/viewport
           (unproject/f (mouse-position ctx)))))
