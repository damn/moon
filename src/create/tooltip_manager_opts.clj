(ns create.tooltip-manager-opts
  (:require [clojure.scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn step [ctx]
  (tooltip-manager/set-initial-time! 0)
  ctx)
