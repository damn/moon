(ns clojure.moon.create-init-tooltip
  (:require [gdl.colors :as colors]
            [clojure.new-color]
            [gdl.tooltip-manager :as tooltip-manager]))

(defn f [ctx]
  (tooltip-manager/set-initial-time! (tooltip-manager/get-instance) 0)
  (colors/put! "PRETTY_NAME" (clojure.new-color/f [0.84 0.8 0.52 1]))
  ctx)
