(ns ctx.mouseover-actor
  (:require [scene2d.stage.hit :refer [hit]]
            [clojure.gdx :as gdx]
            [ctx.mouse-position :refer [mouse-position]]))

(defn mouseover-actor
  [{:keys [ctx/stage]
    :as ctx}]
  (hit stage
       (gdx/unproject (:stage/viewport stage)
                      (mouse-position ctx))))
