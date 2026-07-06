(ns ctx.render.update-mouse-positions
  (:require [gdx.unproject :as unproject]
            [ctx.mouse-position :refer [mouse-position]]))

(defn step
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (unproject/f world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (unproject/f mp))))))
