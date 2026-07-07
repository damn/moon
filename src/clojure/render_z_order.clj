(ns clojure.render-z-order
  (:require [clojure.define-order :as define-order]))

(defn step
  [{:keys [ctx/z-orders]}]
  (define-order/f z-orders))
