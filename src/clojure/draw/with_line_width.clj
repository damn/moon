(ns clojure.draw.with-line-width
  (:require [game.ctx.draw :refer [draw!]]
            [gdl.default-line-width :refer [default-line-width]]
            [gdl.set-default-line-width :refer [set-default-line-width!]]))

(defn f
  [{:keys [ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (let [old-line-width (default-line-width shape-drawer)]
    (set-default-line-width! shape-drawer (* width old-line-width))
    (draw! ctx draws)
    (set-default-line-width! shape-drawer old-line-width)))
