(ns draw.with-line-width
  (:require [clojure.gdx.shape-drawer.get-default-line-width :as get-default-line-width]
            [clojure.gdx.shape-drawer.set-default-line-width :as set-default-line-width]
            [ctx.draw :refer [draw!]]))

(defn f
  [{:keys [ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (let [old-line-width (get-default-line-width/f shape-drawer)]
    (set-default-line-width/f shape-drawer (* width old-line-width))
    (draw! ctx draws)
    (set-default-line-width/f shape-drawer old-line-width)))
