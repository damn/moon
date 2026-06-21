(ns clojure.ctx.tooltip-manager-opts
  (:require [clojure.ui.tooltip-manager :as tooltip-manager]))

(defn step [_ctx]
  (tooltip-manager/set-initial-time! 0))
