(ns clojure.tooltip-manager-opts
  (:require [clojure.tooltip-manager :as tooltip-manager]))

(defn step [_ctx]
  (tooltip-manager/set-initial-time! (tooltip-manager/get-instance) 0))
