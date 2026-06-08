(ns draw.with-line-width
  (:require [game.ctx.draw :refer [draw!]]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(defn f
  [{:keys [ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
    (draw! ctx draws)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
