(ns render.update-mouse-positions
  (:require [clojure.gdx :as gdx]
            [ctx.mouse-position :refer [mouse-position]]))

(defn step
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (assoc ctx
         :ctx/ui-mouse-position (mouse-position ctx)
         :ctx/world-mouse-position (gdx/unproject (:stage/viewport stage)
                                                   (mouse-position ctx))))
