(ns ctx.draw.with-line-width
  (:require [gdx.graphics.shape-drawer :as shape-drawer]
            [ctx.draw :refer [draw!]]))

(defn f
  [{:keys [ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (let [old-line-width (shape-drawer/get-default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
    (draw! ctx draws)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
