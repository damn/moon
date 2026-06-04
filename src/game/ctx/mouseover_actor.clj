(ns game.ctx.mouseover-actor
  (:require [clojure.gdx.scene2d.stage.hit :refer [hit]]
            [clojure.gdx.utils.viewport :as viewport]
            [game.ctx.mouse-position :refer [mouse-position]]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (hit stage
       (-> stage
           :stage/viewport
           (viewport/unproject (mouse-position ctx)))))
