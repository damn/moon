(ns clojure.render-z-order
  (:require [clojure.define-order :as define-order]
            [clojure.z-orders :refer [z-orders]]))

(defn step [_ctx]
  (define-order/f z-orders))
