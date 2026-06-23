(ns game.ctx.mouseover-actor
  (:require [scene2d.stage.hit :refer [hit]]
            [viewport.unproject :as unproject]
            [game.ctx.mouse-position :refer [mouse-position]]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (hit stage
       (-> stage
           :stage/viewport
           (unproject/f (mouse-position ctx)))))
