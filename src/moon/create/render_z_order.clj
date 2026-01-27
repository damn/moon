(ns moon.create.render-z-order
  (:require [moon.order :as order]))

(defn step
  [{:keys [ctx/z-orders]
    :as ctx}]
  (assoc ctx :ctx/render-z-order (order/define-order z-orders)))
