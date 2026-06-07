(ns game.ctx.mouseover-actor
  (:require [com.badlogic.gdx.scenes.scene2d.stage.hit :refer [hit]]
            [com.badlogic.gdx.utils.viewport :as viewport]
            [game.ctx.mouse-position :refer [mouse-position]]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (hit stage
       (-> stage
           :stage/viewport
           (viewport/unproject (mouse-position ctx)))))
