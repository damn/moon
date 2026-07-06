(ns ctx.mouseover-actor
  (:require [scene2d.stage.hit :refer [hit]]
            [gdx.unproject :as unproject]
            [ctx.mouse-position :refer [mouse-position]]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (hit stage
       (-> stage
           :stage/viewport
           (unproject/f (mouse-position ctx)))))
