(ns clojure.render-z-order
  (:require [clojure.define-order :as define-order]
            [clojure.z-orders :as z-orders]))

(defn step [_ctx]
  (define-order/f z-orders/z-orders))
